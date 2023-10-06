package com.db.dataplatform.techtest.server.component;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.exception.HadoopClientException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

public interface DataLakeAdaptor {
    @Retryable(maxAttempts = 3,
            backoff = @Backoff(delay = 1000))
    void pushDataToDataLake(String payload);

    @Recover
    void handlePushDataFailure(String payload);
}
