package com.db.dataplatform.techtest.server.mapper;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class ServerMapperConfiguration {

    @Bean
    public ModelMapper createModelMapperBean() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true);
        modelMapper.addConverter(new AbstractConverter<byte[], String>() {
            @Override
            protected String convert(byte[] source) {
                return new String(source, StandardCharsets.UTF_8);
            }
        });
        return modelMapper;
    }
}
