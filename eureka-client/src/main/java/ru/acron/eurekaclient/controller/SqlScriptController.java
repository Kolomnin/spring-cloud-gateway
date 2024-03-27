package ru.acron.eurekaclient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.acron.eurekaclient.model.SqlScript;
import ru.acron.eurekaclient.service.SqlScriptService;

import java.util.List;

@RestController
@RequestMapping("/sql-scripts")
public class SqlScriptController {

    private final SqlScriptService sqlScriptService;

    public SqlScriptController(SqlScriptService sqlScriptService) {
        this.sqlScriptService = sqlScriptService;
    }

    @Operation(
            summary = "Get SQL script by ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "SQL script received successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "SQL script not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SqlScript.class)
                            )
                    )
            }
    )
    @GetMapping("/{sqlId}")
    public String getSQLScript(@PathVariable Long sqlId) {
        return sqlScriptService.findSQLScriptById(sqlId);
    }

    @Operation(
            summary = "Add SQL script",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "SQL script added successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Failed to add SQL script",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SqlScript.class)
                            )
                    )
            }
    )
    @PostMapping
    public void addSQLScript(@RequestBody Long sqlId, String sqlText) {
        sqlScriptService.insert(sqlId, sqlText);
    }

    @Operation(
            summary = "Update SQL script by ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "SQL script updated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SqlScript.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Failed to update SQL script",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SqlScript.class)
                            )
                    )
            }
    )
    @PutMapping("/{sqlId}")
    public void updateSQLScript(@PathVariable Long sqlId, @RequestBody String sqlText) {
        sqlScriptService.update(sqlId, sqlText);
    }

    @Operation(
            summary = "Delete SQL script by ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @DeleteMapping("/{sqlId}")
    public void deleteSQLScript(@PathVariable Long sqlId) {
        sqlScriptService.delete(sqlId);
    }

    @Operation(
            summary = "Get all SQL scripts",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SqlScript.class))
                            }
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @GetMapping
    public List<SqlScript> getAllSQLScripts() {
        return sqlScriptService.findAll();
    }
}