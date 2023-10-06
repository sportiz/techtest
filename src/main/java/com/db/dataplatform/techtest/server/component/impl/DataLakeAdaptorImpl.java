package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.component.DataLakeAdaptor;
import com.db.dataplatform.techtest.server.exception.HadoopClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataLakeAdaptorImpl implements DataLakeAdaptor {

    private final RestTemplate serverRestTemplate;
    public static final String URI_PUSHBIGDATA = "http://localhost:8090/hadoopserver/pushbigdata";

    /**
     * @param payload Data to push to data lake
     */
    @Override
    public void pushDataToDataLake(String payload) {
        saveToDataLake(payload);
        log.info("Successfully pushed data {} to data lake.", payload);
    }

    /**
     * @param payload Data which failed to push to data lake
     */
    @Override
    public void handlePushDataFailure(String payload) {
        log.error("Failed to store payload {} after retries.", payload);
    }

    private void saveToDataLake(String payload) throws HadoopClientException {
        ResponseEntity<Void> bigDataPushResponse = serverRestTemplate.postForEntity(URI_PUSHBIGDATA, payload, Void.class);

        if(!bigDataPushResponse.getStatusCode().is2xxSuccessful()) {
          throw new HadoopClientException("Failed to push to data lake.");
        }
    }
}
