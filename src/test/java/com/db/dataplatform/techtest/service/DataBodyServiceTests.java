package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.service.impl.DataBodyServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Array;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataBodyEntity;
import static com.db.dataplatform.techtest.TestDataHelper.createTestDataHeaderEntity;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataBodyServiceTests {

    public static final String TEST_NAME_NO_RESULT = "TestNoResult";

    @Mock
    private DataStoreRepository dataStoreRepositoryMock;

    private DataBodyService dataBodyService;
    private DataBodyEntity expectedDataBodyEntity;

    @Before
    public void setup() {
        DataHeaderEntity testDataHeaderEntity = createTestDataHeaderEntity(Instant.now());
        expectedDataBodyEntity = createTestDataBodyEntity(testDataHeaderEntity);

        dataBodyService = new DataBodyServiceImpl(dataStoreRepositoryMock);
    }

    @Test
    public void shouldSaveDataBodyEntityAsExpected(){
        dataBodyService.saveDataBody(expectedDataBodyEntity);

        verify(dataStoreRepositoryMock, times(1))
                .save(eq(expectedDataBodyEntity));
    }

    @Test
    public void shouldGetDataByBlockTypeAsExpected(){
        //Given
        List<DataBodyEntity> expectedDataBodyEntities = Arrays.asList(expectedDataBodyEntity);
        when(dataStoreRepositoryMock.findByBlockType(BlockTypeEnum.BLOCKTYPEA)).thenReturn(expectedDataBodyEntities);

        //When
        List<DataBodyEntity> actualDataBodyEntity = dataBodyService.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA);

        //Then
        assertThat(actualDataBodyEntity).isEqualTo(expectedDataBodyEntities);
        verify(dataStoreRepositoryMock, times(1))
                .findByBlockType(eq(BlockTypeEnum.BLOCKTYPEA));
    }

    @Test
    public void shouldGetDataByBlockNameAsExpected(){
        String name = expectedDataBodyEntity.getDataHeaderEntity().getName();
        //Given
        when(dataStoreRepositoryMock.findByBlockName(name)).thenReturn(of(expectedDataBodyEntity));

        //When
        DataBodyEntity actualDataBodyEntity = dataBodyService.getDataByBlockName(name).get();

        //Then
        assertThat(actualDataBodyEntity).isEqualTo(expectedDataBodyEntity);
        verify(dataStoreRepositoryMock, times(1))
                .findByBlockName(eq(name));
        verify(dataStoreRepositoryMock, times(0))
                .findByBlockName(eq(TEST_NAME_NO_RESULT));
    }

}
