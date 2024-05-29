package ru.acron.eurekaclient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import ru.acron.eurekaclient.controller.SqlDataController;
import ru.acron.eurekaclient.model.SqlScript;
import ru.acron.eurekaclient.repository.SqLScriptRepository;

import java.util.*;

/**
 * Сервис для выполнения SQL-запросов с параметрами и обработки результатов.
 */
@Service
public class SqlDataService {

    private final SqLScriptRepository sqLScriptRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(SqlDataController.class);

    /**
     * Конструктор для создания экземпляра сервиса с необходимыми зависимостями.
     *
     * @param sqLScriptRepository           Репозиторий для доступа к SQL-скриптам.
     * @param namedParameterJdbcTemplate   Объект для выполнения SQL-запросов с именованными параметрами.
     */
    public SqlDataService(SqLScriptRepository sqLScriptRepository, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.sqLScriptRepository = sqLScriptRepository;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /**
     * Выполняет SQL-запрос по его идентификатору с заданными параметрами.
     *
     * @param sqlId   Идентификатор SQL-запроса.
     * @param params  Параметры для SQL-запроса.
     * @return        Ответ на запрос.
     */
    public ResponseEntity<?> executeQueryBySqlId(Long sqlId, Map<String, String> params) {
        String sqlText = getSqlText(sqlId);
        if (sqlText == null) {
            return ResponseEntity.notFound().build();
        }

        for (Map.Entry<String, String> param : params.entrySet()) {
            sqlText = sqlText.replace(":" + param.getKey(), "'" + param.getValue() + "'");
        }

        try {
            if (sqlId == 1L) {
                return executeSelect(sqlText);
            } else {
                return executeUpdateInsertDelete(sqlText);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при выполнении запроса");
        }
    }

    /**
     * Логика выполнения запроса типа SELECT.
     *
     * @param sqlQuery  Текст SQL-запроса.
     * @return          Ответ на запрос.
     */
    private ResponseEntity<?> executeSelect(String sqlQuery) {
        List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(sqlQuery, new MapSqlParameterSource());
        if (rows == null || rows.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = createResponse(rows);
        return ResponseEntity.ok(response);
    }

    /**
     * Логика выполнения запросов типа INSERT, UPDATE, DELETE.
     *
     * @param sqlQuery  Текст SQL-запроса.
     * @return          Ответ на запрос.
     */
    private ResponseEntity<?> executeUpdateInsertDelete(String sqlQuery) {
        namedParameterJdbcTemplate.update(sqlQuery, new MapSqlParameterSource());
        return ResponseEntity.ok(Map.of("result", "OK"));
    }

    /**
     * Создает структуру ответа для запроса типа SELECT.
     *
     * @param rows  Результаты запроса.
     * @return      Структура ответа.
     */
    private Map<String, Object> createResponse(List<Map<String, Object>> rows) {
        List<Map<String, Object>> reqsInfo = Arrays.asList(
                createReqInfo("DATA_ID", true),
                createReqInfo("DATA_VALUE", false)
        );

        List<List<Object>> values = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            values.add(Arrays.asList(row.get("DATA_ID"), row.get("DATA_VALUE")));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("reqsInfo", reqsInfo);
        response.put("values", values);

        return response;
    }

    /**
     * Создает информацию о параметре запроса.
     *
     * @param name           Имя параметра.
     * @param isPrimaryKey   Флаг первичного ключа.
     * @return               Информация о параметре запроса.
     */
    private Map<String, Object> createReqInfo(String name, boolean isPrimaryKey) {
        Map<String, Object> reqInfo = new LinkedHashMap<>();
        reqInfo.put("isPrimaryKey", isPrimaryKey);
        reqInfo.put("name", name);
        reqInfo.put("type", "String");
        return reqInfo;
    }

    /**
     * Получает текст SQL-запроса по его идентификатору.
     *
     * @param sqlId  Идентификатор SQL-запроса.
     * @return       Текст SQL-запроса.
     */
    private String getSqlText(Long sqlId) {
        SqlScript sqlScript = sqLScriptRepository.findBySqlId(sqlId).orElse(null);
        return sqlScript != null ? sqlScript.getSqlText() : null;
    }

    /**
     * Обрабатывает запросы типа SELECT.
     *
     * @param requestBody  Тело запроса.
     * @return             Ответ на запрос.
     */
    public ResponseEntity<?> getDataBySqlIdSelect(Map<String, String> requestBody) {
        if (!requestBody.containsKey("DATA_ID")) {
            return ResponseEntity.badRequest().body("Отсутствует DATA_ID в теле запроса");
        }
        return executeQueryBySqlId(1L, Map.of("DATA_IDF", requestBody.get("DATA_ID")));
    }

    /**
     * Обрабатывает запросы типа INSERT.
     *
     * @param requestBody  Тело запроса.
     * @return             Ответ на запрос.
     */
    public ResponseEntity<?> getDataBySqlIdInsert(Map<String, String> requestBody) {
        if (!requestBody.containsKey("DATA_ID") || !requestBody.containsKey("DATA_VALUE")) {
            return ResponseEntity.badRequest().body("Отсутствуют DATA_ID или DATA_VALUE в теле запроса");
        }
        return executeQueryBySqlId(2L, Map.of(
                "DATA_IDF", requestBody.get("DATA_ID"),
                "DATA_VALUEF", requestBody.get("DATA_VALUE")
        ));
    }

    /**
     * Обрабатывает запросы типа UPDATE.
     *
     * @param requestBody  Тело запроса.
     * @return             Ответ на запрос.
     */
    public ResponseEntity<?> getDataBySqlIdUpdate(Map<String, String> requestBody) {
        if (!requestBody.containsKey("DATA_ID") || !requestBody.containsKey("DATA_VALUE")) {
            return ResponseEntity.badRequest().body("Отсутствуют DATA_ID или DATA_VALUE в теле запроса");
        }

        try {
            return executeQueryBySqlId(3L, Map.of(
                    "DATA_IDF", requestBody.get("DATA_ID"),
                    "DATA_VALUEF", requestBody.get("DATA_VALUE")
            ));
        } catch (Exception e) {
            logger.error("Ошибка при выполнении запроса обновления данных: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при выполнении запроса обновления данных");
        }
    }

    /**
     * Обрабатывает запросы типа DELETE.
     *
     * @param requestBody  Тело запроса.
     * @return             Ответ на запрос.
     */
    public ResponseEntity<?> getDataBySqlIdDelete(Map<String, String> requestBody) {
        if (!requestBody.containsKey("DATA_ID")) {
            return ResponseEntity.badRequest().body("Отсутствует DATA_ID в теле запроса");
        }
        return executeQueryBySqlId(4L, Map.of("DATA_IDF", requestBody.get("DATA_ID")));
    }

    /**
     * Выполняет SQL-запрос непосредственно.
     *
     * @param sqlQuery  Текст SQL-запроса.
     * @return          Ответ на запрос.
     */
    public ResponseEntity<?> executeQuery(String sqlQuery) {
        return executeSelect(sqlQuery);
    }
}