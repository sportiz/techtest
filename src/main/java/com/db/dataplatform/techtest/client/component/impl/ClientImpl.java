package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
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
    @Qualifier("clientRestTemplate")
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
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        Map<String, String> params = new HashMap<>();
        params.put("blockType", blockType);

       ResponseEntity<List<DataEnvelope>> envelops = restTemplate.exchange(
                URI_GETDATA.toString(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<DataEnvelope>>(){},
                params);
        return envelops.getStatusCode().is2xxSuccessful() ? envelops.getBody() : emptyList();
    }

    @Override
    public boolean updateData(String blockName, String newBlockType) {
        log.info("Updating blocktype to {} for block with name {}", newBlockType, blockName);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        Map<String, String> params = new HashMap<>();
        params.put("name", blockName);
        params.put("newBlockType", newBlockType);

        ResponseEntity<Boolean> isUpdated = restTemplate.exchange(
                URI_PATCHDATA.toString(),
                HttpMethod.POST,
                entity,
                Boolean.class,
                params);
        log.info("Received response for updating blocktype {}", isUpdated.getBody());
        return isUpdated.getStatusCode().is2xxSuccessful();
    }


}
