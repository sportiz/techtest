package com.db.dataplatform.techtest.client.api.model;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@JsonSerialize(as = DataHeader.class)
@JsonDeserialize(as = DataHeader.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DataHeader {

    @NotNull
    public String name;

    @NotNull
    private BlockTypeEnum blockType;

    @NotNull
    private String dataCheckSum;

}
