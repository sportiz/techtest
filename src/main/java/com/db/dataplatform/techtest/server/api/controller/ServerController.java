package com.db.dataplatform.techtest.server.api.controller;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.validator.ValidBlockName;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static java.lang.Boolean.TRUE;

@Slf4j
@Controller
@RequestMapping("/dataserver")
@RequiredArgsConstructor
@Validated
public class ServerController {

    private final Server server;

    @PostMapping(value = "/pushdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> pushData(@Valid @RequestBody DataEnvelope dataEnvelope) throws IOException, NoSuchAlgorithmException {

        log.info("Data envelope received: {}", dataEnvelope.getDataHeader().getName());
        boolean checksumPass = server.saveDataEnvelope(dataEnvelope);

        if(checksumPass) {
            log.info("Data envelope persisted. Attribute name: {}", dataEnvelope.getDataHeader().getName());
        }
        return ResponseEntity.ok(checksumPass);
    }

    @GetMapping(value="/data/{blockType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DataEnvelope>> getDataByBlockType(@PathVariable("blockType") BlockTypeEnum blockType)  {

        log.info("Requested block type: {}", blockType);
        List<DataEnvelope> envelopes = server.findDataEnvelopeByBlockType(blockType);

        log.info("Retrieved data envelopes: {}", envelopes);
        return ResponseEntity.ok(envelopes);
    }

    @PostMapping(value = "/update/{name}/{newBlockType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateBlock(@NotNull @ValidBlockName @PathVariable("name") String blockName,
                                               @Valid @NotNull @PathVariable("newBlockType") BlockTypeEnum newBlockType) {

        log.info("Updating envelope name: {} block type to {}", blockName, newBlockType);
        server.updateDataBlockName(blockName, newBlockType);

        log.info("Updated envelope name: {} block type to {} successfully", blockName, newBlockType);
        return ResponseEntity.ok(TRUE);
    }
}
