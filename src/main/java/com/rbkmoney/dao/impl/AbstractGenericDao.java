package com.rbkmoney.dao.impl;

import com.rbkmoney.dao.DaoException;
import com.rbkmoney.dao.GenericDao;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public abstract class AbstractGenericDao extends NamedParameterJdbcDaoSupport implements GenericDao {

    private final DSLContext dslContext;

    public AbstractGenericDao(DataSource dataSource) {
        setDataSource(dataSource);
        Configuration configuration = new DefaultConfiguration();
        configuration.set(SQLDialect.POSTGRES);
        this.dslContext = DSL.using(configuration);
    }

    protected DSLContext getDslContext() {
        return dslContext;
    }

    @Override
    public <T> T fetchOne(Query query, Class<T> type) throws DaoException {
        return fetchOne(query, type, getNamedParameterJdbcTemplate());
    }

    @Override
    public <T> T fetchOne(Query query, Class<T> type, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return fetchOne(query, new SingleColumnRowMapper<>(type), namedParameterJdbcTemplate);
    }

    @Override
    public <T> T fetchOne(Query query, RowMapper<T> rowMapper) throws DaoException {
        return fetchOne(query, rowMapper, getNamedParameterJdbcTemplate());
    }

    @Override
    public <T> T fetchOne(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper) throws DaoException {
        return fetchOne(namedSql, parameterSource, rowMapper, getNamedParameterJdbcTemplate());
    }

    @Override
    public <T> T fetchOne(Query query, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return fetchOne(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), rowMapper, namedParameterJdbcTemplate);
    }

    @Override
    public <T> T fetchOne(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            return namedParameterJdbcTemplate.queryForObject(
                    namedSql,
                    parameterSource,
                    rowMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public <T> List<T> fetch(Query query, RowMapper<T> rowMapper) throws DaoException {
        return fetch(query, rowMapper, getNamedParameterJdbcTemplate());
    }

    @Override
    public <T> List<T> fetch(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper) throws DaoException {
        return fetch(namedSql, parameterSource, rowMapper, getNamedParameterJdbcTemplate());
    }

    @Override
    public <T> List<T> fetch(Query query, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return fetch(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), rowMapper, namedParameterJdbcTemplate);
    }

    @Override
    public <T> List<T> fetch(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            return namedParameterJdbcTemplate.query(
                    namedSql,
                    parameterSource,
                    rowMapper
            );
        } catch (NestedRuntimeException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void executeOne(Query query) throws DaoException {
        execute(query, 1);
    }

    @Override
    public void executeOne(String namedSql, SqlParameterSource parameterSource) throws DaoException {
        execute(namedSql, parameterSource, 1);
    }

    @Override
    public void executeOne(Query query, KeyHolder keyHolder) throws DaoException {
        executeOne(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), keyHolder);
    }

    @Override
    public void executeOne(String namedSql, SqlParameterSource parameterSource, KeyHolder keyHolder) throws DaoException {
        execute(namedSql, parameterSource, 1, keyHolder);
    }

    @Override
    public int execute(Query query) throws DaoException {
        return execute(query, -1);
    }

    @Override
    public int execute(Query query, int expectedRowsAffected) throws DaoException {
        return execute(query, expectedRowsAffected, getNamedParameterJdbcTemplate());
    }

    @Override
    public int execute(Query query, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        return execute(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), expectedRowsAffected, namedParameterJdbcTemplate);
    }

    @Override
    public int execute(String namedSql) throws DaoException {
        return execute(namedSql, EmptySqlParameterSource.INSTANCE);
    }

    @Override
    public int execute(String namedSql, SqlParameterSource parameterSource) throws DaoException {
        return execute(namedSql, parameterSource, -1);
    }

    @Override
    public int execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected) throws DaoException {
        return execute(namedSql, parameterSource, expectedRowsAffected, getNamedParameterJdbcTemplate());
    }

    @Override
    public int execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            int rowsAffected = namedParameterJdbcTemplate.update(
                    namedSql,
                    parameterSource);

            if (expectedRowsAffected != -1 && rowsAffected != expectedRowsAffected) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(namedSql, expectedRowsAffected, rowsAffected);
            }
            return rowsAffected;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public int execute(Query query, KeyHolder keyHolder) throws DaoException {
        return execute(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), -1, keyHolder);
    }

    @Override
    public int execute(Query query, int expectedRowsAffected, KeyHolder keyHolder) throws DaoException {
        return execute(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), expectedRowsAffected, getNamedParameterJdbcTemplate(), keyHolder);
    }

    @Override
    public int execute(Query query, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate, KeyHolder keyHolder) throws DaoException {
        return execute(query.getSQL(ParamType.NAMED), toSqlParameterSource(query.getParams()), expectedRowsAffected, namedParameterJdbcTemplate, keyHolder);
    }

    @Override
    public int execute(String namedSql, KeyHolder keyHolder) throws DaoException {
        return execute(namedSql, EmptySqlParameterSource.INSTANCE, keyHolder);
    }

    @Override
    public int execute(String namedSql, SqlParameterSource parameterSource, KeyHolder keyHolder) throws DaoException {
        return execute(namedSql, parameterSource, -1, keyHolder);
    }

    @Override
    public int execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected, KeyHolder keyHolder) throws DaoException {
        return execute(namedSql, parameterSource, expectedRowsAffected, getNamedParameterJdbcTemplate(), keyHolder);
    }

    @Override
    public int execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate, KeyHolder keyHolder) throws DaoException {
        try {
            int rowsAffected = namedParameterJdbcTemplate.update(
                    namedSql,
                    parameterSource,
                    keyHolder);

            if (expectedRowsAffected != -1 && rowsAffected != expectedRowsAffected) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(namedSql, expectedRowsAffected, rowsAffected);
            }
            return rowsAffected;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public long batchExecute(List<Query> queries) throws DaoException {
        return batchExecute(queries, -1);
    }

    @Override
    public long batchExecute(List<Query> queries, int expectedRowsAffected) throws DaoException {
        return batchExecute(queries, expectedRowsAffected, getNamedParameterJdbcTemplate());
    }

    @Override
    public long batchExecute(List<Query> queries, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        AtomicLong affectedRowCounter = new AtomicLong();
        queries.stream()
                .collect(
                        Collectors.groupingBy(
                                query -> query.getSQL(ParamType.NAMED),
                                LinkedHashMap::new,
                                Collectors.mapping(query -> toSqlParameterSource(query.getParams()), Collectors.toList())
                        )
                )
                .forEach(
                        (namedSql, parameterSources) -> {
                            long affectedRowCount = batchExecute(
                                    namedSql,
                                    parameterSources,
                                    expectedRowsAffected,
                                    namedParameterJdbcTemplate
                            );
                            affectedRowCounter.getAndAccumulate(affectedRowCount, Long::sum);
                        }
                );
        return affectedRowCounter.get();
    }

    @Override
    public long batchExecute(String namedSql, List<SqlParameterSource> parameterSources) throws DaoException {
        return batchExecute(namedSql, parameterSources, -1);
    }

    @Override
    public long batchExecute(String namedSql, List<SqlParameterSource> parameterSources, int expectedRowsAffected) throws DaoException {
        return batchExecute(namedSql, parameterSources, expectedRowsAffected, getNamedParameterJdbcTemplate());
    }

    @Override
    public long batchExecute(String namedSql, List<SqlParameterSource> parameterSources, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException {
        try {
            int[] rowsPerBatchAffected = namedParameterJdbcTemplate.batchUpdate(namedSql, parameterSources.toArray(new SqlParameterSource[0]));

            if (rowsPerBatchAffected.length != parameterSources.size()) {
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(namedSql, parameterSources.size(), rowsPerBatchAffected.length);
            }

            int count = 0;
            for (int i : rowsPerBatchAffected) {
                count += i;
            }
            if (expectedRowsAffected != -1) {
                if (count != expectedRowsAffected) {
                    throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(namedSql, expectedRowsAffected, count);
                }
            }

            return count;
        } catch (NestedRuntimeException ex) {
            throw new DaoException(ex);
        }
    }

    protected Condition appendDateTimeRangeConditions(Condition condition,
                                                      Field<LocalDateTime> field,
                                                      Optional<LocalDateTime> fromTime,
                                                      Optional<LocalDateTime> toTime) {
        if (fromTime.isPresent()) {
            condition = condition.and(field.ge(fromTime.get()));
        }

        if (toTime.isPresent()) {
            condition = condition.and(field.lt(toTime.get()));
        }
        return condition;
    }

    protected SqlParameterSource toSqlParameterSource(Map<String, Param<?>> params) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        for (Map.Entry<String, Param<?>> entry : params.entrySet()) {
            Param<?> param = entry.getValue();
            Class<?> type = param.getDataType().getType();
            if (String.class.isAssignableFrom(type)) {
                String value = Optional.ofNullable(param.getValue())
                        .map(stringValue -> ((String) stringValue).replace("\u0000", "\\u0000"))
                        .orElse(null);
                sqlParameterSource.addValue(entry.getKey(), value);
            } else if (LocalDateTime.class.isAssignableFrom(type) || EnumType.class.isAssignableFrom(type)) {
                sqlParameterSource.addValue(entry.getKey(), param.getValue(), Types.OTHER);
            } else {
                sqlParameterSource.addValue(entry.getKey(), param.getValue());
            }
        }
        return sqlParameterSource;
    }

}
