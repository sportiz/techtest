package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.DataLakeAdaptor;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataEnvelopeApiObject;
import static com.db.dataplatform.techtest.server.persistence.BlockTypeEnum.BLOCKTYPEA;
import static com.db.dataplatform.techtest.server.persistence.BlockTypeEnum.BLOCKTYPEB;
import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerServiceTests {

    @Mock
    private DataBodyService dataBodyServiceImplMock;
    @Mock
    private DataLakeAdaptor dataLakeAdaptor;

    private ModelMapper modelMapper;

    private DataBodyEntity expectedDataBodyEntity;
    private DataEnvelope testDataEnvelope;
    private ObjectMapper mapper;
    private Server server;

    @Before
    public void setup() {
        ServerMapperConfiguration serverMapperConfiguration = new ServerMapperConfiguration();
        modelMapper = serverMapperConfiguration.createModelMapperBean();
        mapper = new ObjectMapper();

        testDataEnvelope = createTestDataEnvelopeApiObject();
        expectedDataBodyEntity = modelMapper.map(testDataEnvelope.getDataBody(), DataBodyEntity.class);
        expectedDataBodyEntity.setDataHeaderEntity(modelMapper.map(testDataEnvelope.getDataHeader(), DataHeaderEntity.class));

        server = new ServerImpl(dataBodyServiceImplMock, modelMapper, dataLakeAdaptor, mapper);
    }

    @Test
    public void shouldSaveDataEnvelopeAsExpected() throws NoSuchAlgorithmException, IOException {
        doNothing().when(dataLakeAdaptor).pushDataToDataLake(any());
        ArgumentCaptor<DataBodyEntity> entityArgument = ArgumentCaptor.forClass(DataBodyEntity.class);

        boolean success = server.saveDataEnvelope(testDataEnvelope);

        assertThat(success).isTrue();
        verify(dataBodyServiceImplMock, times(1)).saveDataBody(entityArgument.capture());
        assertEquals(entityArgument.getValue().getDataHeaderEntity().getName(),
                expectedDataBodyEntity.getDataHeaderEntity().getName());
    }
    @Test
    public void shouldFindDataEnvelopeByBlockTypeAsExpected() throws JsonProcessingException {
        when(dataBodyServiceImplMock.getDataByBlockType(any())).thenReturn(asList(expectedDataBodyEntity));

        List<DataEnvelope> result = server.findDataEnvelopeByBlockType(BLOCKTYPEA);

        verify(dataBodyServiceImplMock, times(1)).getDataByBlockType(BLOCKTYPEA);

        assertEquals(mapper.writeValueAsString(testDataEnvelope), mapper.writeValueAsString(result.get(0)));
    }

    @Test
    public void shouldFindDataEnvelopeByNameAsExpected() throws JsonProcessingException {
        when(dataBodyServiceImplMock.getDataByBlockName(any())).thenReturn(of(expectedDataBodyEntity));
        ArgumentCaptor<DataBodyEntity> entityArgument = ArgumentCaptor.forClass(DataBodyEntity.class);

        server.updateDataBlockName(TestDataHelper.TEST_NAME, BLOCKTYPEB);

        verify(dataBodyServiceImplMock, times(1)).saveDataBody(entityArgument.capture());

        assertEquals(BLOCKTYPEB, entityArgument.getValue().getDataHeaderEntity().getBlocktype());
    }
}
