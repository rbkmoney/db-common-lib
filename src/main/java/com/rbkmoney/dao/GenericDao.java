package com.rbkmoney.dao;

import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;

public interface GenericDao {

    <T> T fetchOne(Query query, Class<T> type) throws DaoException;

    <T> T fetchOne(Query query, Class<T> type, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;

    <T> T fetchOne(Query query, RowMapper<T> rowMapper) throws DaoException;

    <T> T fetchOne(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper) throws DaoException;

    <T> T fetchOne(Query query, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;

    <T> T fetchOne(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;

    <T> List<T> fetch(Query query, RowMapper<T> rowMapper) throws DaoException;

    <T> List<T> fetch(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper) throws DaoException;

    <T> List<T> fetch(Query query, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;

    <T> List<T> fetch(String namedSql, SqlParameterSource parameterSource, RowMapper<T> rowMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;

    void executeOne(Query query) throws DaoException;

    void executeOne(String namedSql, SqlParameterSource parameterSource) throws DaoException;

    void executeOne(Query query, KeyHolder keyHolder) throws DaoException;

    void executeOne(String namedSql, SqlParameterSource parameterSource, KeyHolder keyHolder) throws DaoException;

    int execute(Query query) throws DaoException;

    int execute(Query query, int expectedRowsAffected) throws DaoException;

    int execute(Query query, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;

    int execute(String namedSql) throws DaoException;

    int execute(String namedSql, SqlParameterSource parameterSource) throws DaoException;

    int execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected) throws DaoException;

    int execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;

    int execute(Query query, KeyHolder keyHolder) throws DaoException;

    int execute(Query query, int expectedRowsAffected, KeyHolder keyHolder) throws DaoException;

    int execute(Query query, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate, KeyHolder keyHolder) throws DaoException;

    int execute(String namedSql, KeyHolder keyHolder) throws DaoException;

    int execute(String namedSql, SqlParameterSource parameterSource, KeyHolder keyHolder) throws DaoException;

    int execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected, KeyHolder keyHolder) throws DaoException;

    int execute(String namedSql, SqlParameterSource parameterSource, int expectedRowsAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate, KeyHolder keyHolder) throws DaoException;

    long batchExecute(List<Query> queries) throws DaoException;

    long batchExecute(List<Query> queries, int expectedRowsPerQueryAffected) throws DaoException;

    long batchExecute(List<Query> queries, int expectedRowsPerQueryAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;

    long batchExecute(String namedSql, List<SqlParameterSource> parameterSources) throws DaoException;

    long batchExecute(String namedSql, List<SqlParameterSource> parameterSources, int expectedRowsPerQueryAffected) throws DaoException;

    long batchExecute(String namedSql, List<SqlParameterSource> parameterSources, int expectedRowsPerQueryAffected, NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;
}
