package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.component.DataLakeAdaptor;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.component.Server;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

    private final DataBodyService dataBodyServiceImpl;
    private final ModelMapper modelMapper;
    private final DataLakeAdaptor adaptor;
    private final ObjectMapper mapper;

    /**
     * @param envelope
     * @return true if there is a match with the client provided checksum.
     */
    @Override
    public boolean saveDataEnvelope(DataEnvelope envelope) {

        if(isValidCheckSum(envelope.getDataHeader().getDataCheckSum(), envelope.getDataBody().getDataBody())) {
            // Save to persistence.
            persist(envelope);

            log.info("Data persisted successfully, data name: {}", envelope.getDataHeader().getName());

            try {
                String dataLakePayload = mapper.writeValueAsString(envelope);
                log.info("Pushing data {} to data lake", dataLakePayload);
                adaptor.pushDataToDataLake(dataLakePayload);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            return true;
        }
        return false;
    }

    /**
     * @param blockType
     * @return list of data envelope of provided block type
     */
    @Override
    public List<DataEnvelope> findDataEnvelopeByBlockType(BlockTypeEnum blockType) {
        List<DataBodyEntity> entities = getDataByBlockType(blockType);

        return entities != null
                ? entities.stream().map(dataBodyEntity ->
                        new DataEnvelope(new DataHeader(
                                dataBodyEntity.getDataHeaderEntity().getName(),
                                dataBodyEntity.getDataHeaderEntity().getBlocktype(),
                                dataBodyEntity.getDataHeaderEntity().getDataCheckSum()),
                                new DataBody(dataBodyEntity.getDataBody()))).collect(Collectors.toList())
                : Collections.emptyList();
    }

    /**
     * @param blockName
     * @param newBlockType
     * @return
     */
    @Override
    public void updateDataBlockName(String blockName, BlockTypeEnum newBlockType) {
        DataBodyEntity entity = getDataByName(blockName);
        entity.getDataHeaderEntity().setBlocktype(newBlockType);
        saveData(entity);
    }

    private List<DataBodyEntity> getDataByBlockType(BlockTypeEnum blockType) {
        return dataBodyServiceImpl.getDataByBlockType(blockType);
    }

    private DataBodyEntity getDataByName(String name) {
        return dataBodyServiceImpl.getDataByBlockName(name)
                .orElseThrow(() -> new IllegalArgumentException("No data block found with name " + name));
    }
    private boolean isValidCheckSum(String checkSumFromRequest, String dataBody) {
        final String hash;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(dataBody.getBytes());
            byte[] digest = md.digest();
            hash = DatatypeConverter.printHexBinary(digest);

        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate MD5 checksum for the data {}",dataBody, e);
            return false;
        }

        // Calculate the hash
        return hash != null && hash.equalsIgnoreCase(checkSumFromRequest);

    }

    private void persist(DataEnvelope envelope) {
        log.info("Persisting data with attribute name: {}", envelope.getDataHeader().getName());
        DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);

        DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);

        saveData(dataBodyEntity);
    }

    private void saveData(DataBodyEntity dataBodyEntity) {
        dataBodyServiceImpl.saveDataBody(dataBodyEntity);
    }

}
