package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * Client code does not require any test coverage
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientImpl implements Client {

    public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
    public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
    public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void pushData(DataEnvelope dataEnvelope) {
        log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);

        ResponseEntity<Boolean> pushSuccessful = restTemplate
                .postForEntity(URI_PUSHDATA, dataEnvelope, Boolean.class);

        if (pushSuccessful.getStatusCode().is2xxSuccessful()
                && ofNullable(pushSuccessful.getBody()).orElse(Boolean.FALSE)) {
            log.info("Successfully pushed data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);
        } else {
            log.info("Failed to push data {} to {}. The HTTP status is {} and resposne is {}",
                    dataEnvelope.getDataHeader().getName(),
                    URI_PUSHDATA,
                    pushSuccessful.getStatusCode(),
                    pushSuccessful.getBody());
        }

    }

    @Override
    public List<DataEnvelope> getData(String blockType) {
        log.info("Query for data with header block type {}", blockType);
        return null;
    }

    @Override
    public boolean updateData(String blockName, String newBlockType) {
        log.info("Updating blocktype to {} for block with name {}", newBlockType, blockName);
        return true;
    }


}
