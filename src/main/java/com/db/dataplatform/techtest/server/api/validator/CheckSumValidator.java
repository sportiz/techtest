package com.db.dataplatform.techtest.server.api.validator;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Slf4j
public class CheckSumValidator implements ConstraintValidator<ValidChecksum, DataEnvelope> {

    @Override
    public boolean isValid(
            DataEnvelope envelope, ConstraintValidatorContext context) {

        if (envelope == null) {
            return true;
        }

        if (envelope.getDataHeader() == null
                || envelope.getDataHeader().getDataCheckSum() == null
                || envelope.getDataBody() == null
                || envelope.getDataBody().getDataBody() == null) {
            return false;
        }

        byte[] hash = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            hash = md5.digest(envelope.getDataBody().getDataBody().getBytes(StandardCharsets.UTF_8));

        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate MD5 checksum for the data {}", envelope.getDataBody().getDataBody(), e);
            return false;
        }

        // Calculate the hash
        return (hash != null && Arrays.equals(hash, envelope.getDataHeader().getDataCheckSum()));
    }
}
