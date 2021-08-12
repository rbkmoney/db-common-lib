package com.rbkmoney.dao.impl;

import com.rbkmoney.dao.DaoException;
import org.jooq.DataType;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.conf.ParamType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AbstractGenericDaoTest {

    private TestDao testDaoSpy;

    @Before
    public void setUp() throws Exception {
        testDaoSpy = spy(new TestDao(mock(DataSource.class)));
    }

    @Test
    public void batchExecuteTest() {
        final NamedParameterJdbcTemplate namedParameterJdbcTemplateMock = mock(NamedParameterJdbcTemplate.class);
        int[] rowPerBatchAffected = {1, 1};
        when(namedParameterJdbcTemplateMock.batchUpdate(anyString(), any(SqlParameterSource[].class)))
                .thenReturn(rowPerBatchAffected);
        when(testDaoSpy.getNamedParameterJdbcTemplate()).thenReturn(namedParameterJdbcTemplateMock);

        final Map<String, Param<?>> firstParamMap = paramMapMock("testString", "testValue", String.class);
        firstParamMap.putAll(paramMapMock("testDate", LocalDateTime.now(), LocalDateTime.class));

        final Query firstQueryMock = mock(Query.class);
        when(firstQueryMock.getSQL(ParamType.NAMED)).thenReturn("test sql");
        when(firstQueryMock.getParams()).thenReturn(firstParamMap);

        final Map<String, Param<?>> secondParamMap = paramMapMock("testInteger", 12345, Integer.class);
        secondParamMap.putAll(paramMapMock("testLong", 12345L, Long.class));

        final Query secondQueryMock = mock(Query.class);
        when(secondQueryMock.getSQL(ParamType.NAMED)).thenReturn("test sql");
        when(secondQueryMock.getParams()).thenReturn(secondParamMap);

        final List<Query> queryList = Arrays.asList(firstQueryMock, secondQueryMock);
        final long rowsAffected = testDaoSpy.batchExecute(queryList, 2);
        Assert.assertEquals(2, rowsAffected);
    }

    @Test(expected = DaoException.class)
    public void batchExceptionTest() {
        final NamedParameterJdbcTemplate namedParameterJdbcTemplateMock = mock(NamedParameterJdbcTemplate.class);
        int[] rowPerBatchAffected = {1};
        when(namedParameterJdbcTemplateMock.batchUpdate(anyString(), any(SqlParameterSource[].class)))
                .thenReturn(rowPerBatchAffected);
        when(testDaoSpy.getNamedParameterJdbcTemplate()).thenReturn(namedParameterJdbcTemplateMock);

        final Map<String, Param<?>> firstParamMap = paramMapMock("testString", "testValue", String.class);
        firstParamMap.putAll(paramMapMock("testDate", LocalDateTime.now(), LocalDateTime.class));

        final Query firstQueryMock = mock(Query.class);
        when(firstQueryMock.getSQL(ParamType.NAMED)).thenReturn("test sql");
        when(firstQueryMock.getParams()).thenReturn(firstParamMap);

        final List<Query> queryList = Collections.singletonList(firstQueryMock);
        final long rowsAffected = testDaoSpy.batchExecute(queryList, 2);
    }

    @Test
    public void toSqlParameterSourceNullByteTest() {
        final Map<String, Param<?>> paramMap = paramMapMock("test", "\u0000", String.class);
        final SqlParameterSource sqlParameterSource = testDaoSpy.toSqlParameterSource(paramMap);
        Assert.assertEquals("\\u0000", sqlParameterSource.getValue("test"));
    }

    @Test
    public void toSqlParameterSourceDateTest() {
        final LocalDateTime dateTime = LocalDateTime.now();
        final Map<String, Param<?>> paramMap = paramMapMock("testDate", dateTime, LocalDateTime.class);
        final SqlParameterSource sqlParameterSource = testDaoSpy.toSqlParameterSource(paramMap);
        Assert.assertEquals(dateTime, sqlParameterSource.getValue("testDate"));
        Assert.assertEquals(Types.OTHER, sqlParameterSource.getSqlType("testDate"));
    }

    private Map<String, Param<?>> paramMapMock(String key, Object value, Class<?> dataType) {
        final Param paramMock = mock(Param.class);
        final DataType dataTypeMock = mock(DataType.class);
        when(dataTypeMock.getType()).thenReturn(dataType);
        when(paramMock.getDataType()).thenReturn(dataTypeMock);
        when(paramMock.getValue()).thenReturn(value);
        final Map<String, Param<?>> paramMap = new HashMap<>();
        paramMap.put(key, paramMock);

        return paramMap;
    }

    private class TestDao extends AbstractGenericDao {

        TestDao(DataSource dataSource) {
            super(dataSource);
        }
    }

}
