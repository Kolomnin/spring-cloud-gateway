package ru.acron.eurekaclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.acron.eurekaclient.entity.SqlScript;
import ru.acron.eurekaclient.service.SqlScriptService;

import java.util.List;

@RestController
@RequestMapping("/sql-scripts")
public class SqlScriptController {

    private final SqlScriptService sqlScriptService;

    public SqlScriptController(SqlScriptService sqlScriptService) {
        this.sqlScriptService = sqlScriptService;
    }

    @GetMapping("/{sqlId}")
    public String getSQLScript(@PathVariable Long sqlId) {
        return sqlScriptService.findSQLScriptById(sqlId);
    }

    @PostMapping
    public void addSQLScript(@RequestBody String sqlText) {
        sqlScriptService.insert(sqlText);
    }

    @PutMapping("/{sqlId}")
    public void updateSQLScript(@PathVariable Long sqlId, @RequestBody String sqlText) {
        sqlScriptService.update(sqlId, sqlText);
    }

    @DeleteMapping("/{sqlId}")
    public void deleteSQLScript(@PathVariable Long sqlId) {
        sqlScriptService.delete(sqlId);
    }

    @GetMapping
    public List<SqlScript> getAllSQLScripts() {
        return sqlScriptService.findAll();
    }
}