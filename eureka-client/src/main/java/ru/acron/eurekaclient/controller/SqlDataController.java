package ru.acron.eurekaclient.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.acron.eurekaclient.service.SqlDataService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SqlDataController {

    private final SqlDataService sqlDataService;

    private static final Logger logger = LoggerFactory.getLogger(SqlDataController.class);

    public SqlDataController(SqlDataService sqlDataService) {
        this.sqlDataService = sqlDataService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllData() {
        return sqlDataService.executeQuery("SELECT * FROM SQL_DATA");
    }

    @PostMapping("/1")
    public ResponseEntity<?> select(@RequestBody Map<String, String> requestBody) {
        return sqlDataService.getDataBySqlIdSelect(requestBody);
    }

    @PostMapping("/2")
    public ResponseEntity<?> insert(@RequestBody Map<String, String> requestBody) {
        return sqlDataService.getDataBySqlIdInsert(requestBody);
    }

    @PutMapping("/3")
    public ResponseEntity<?> update(@RequestBody Map<String, String> requestBody) {
        logger.debug("Received PUT request with body: {}", requestBody);
        try {
            return sqlDataService.getDataBySqlIdUpdate(requestBody);
        } catch (Exception e) {
            logger.error("Error processing update request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при обработке запроса обновления данных");
        }
    }

    @DeleteMapping("/4")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> requestBody) {
        return sqlDataService.getDataBySqlIdDelete(requestBody);
    }
}
