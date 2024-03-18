package ru.acron.eurekaclient.service;

import org.springframework.stereotype.Service;
import ru.acron.eurekaclient.entity.SqlScript;
import ru.acron.eurekaclient.repository.SQLScriptRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SQLScriptService {

    private final SQLScriptRepository sqlScriptRepository;

    public SQLScriptService(SQLScriptRepository sqlScriptRepository) {
        this.sqlScriptRepository = sqlScriptRepository;
    }

    public String findSQLScriptById(Long sqlId) {
        SqlScript sqlScript = sqlScriptRepository.findById(sqlId).orElse(null);
        return sqlScript != null ? sqlScript.getSqlText() : null;
    }

    // Для вставки данных:
    public void insert(String sqlText) {
        SqlScript script = new SqlScript();
        script.setSqlText(sqlText);
        sqlScriptRepository.save(script);
    }

    public void update(Long sqlId, String sqlText) {
        SqlScript script = sqlScriptRepository.findById(sqlId).orElse(null);
        if (script != null) {
            script.setSqlText(sqlText);
            sqlScriptRepository.save(script);
        }
    }

    public void delete(Long sqlId) {
        sqlScriptRepository.deleteById(sqlId);
    }

    public List<SqlScript> findAll() {
        return sqlScriptRepository.findAll();
    }

    public Optional<SqlScript> findById(Long sqlId) {
        return sqlScriptRepository.findById(sqlId);
    }
}