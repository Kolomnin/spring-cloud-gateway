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
import ru.acron.eurekaclient.repository.SqlScriptRepository;

import java.util.*;

/**
 * Сервис для выполнения SQL-запросов с параметрами и обработки результатов.
 */
@Service
public class SqlDataService {

    private final SqlScriptRepository sqlScriptRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(SqlDataController.class);

    /**
     * Конструктор для создания экземпляра сервиса с необходимыми зависимостями.
     *
     * @param sqlScriptRepository           Репозиторий для доступа к SQL-скриптам.
     * @param namedParameterJdbcTemplate   Объект для выполнения SQL-запросов с именованными параметрами.
     */
    public SqlDataService(SqlScriptRepository sqlScriptRepository, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.sqlScriptRepository = sqlScriptRepository;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /**
     * Выполняет SQL-запрос по его идентификатору с заданными параметрами.
     *
     * @param sqlId   Идентификатор SQL-запроса.
     * @param params  Параметры для SQL-запроса.
     * @return        Ответ на запрос.
     * Метод принимает sqlId (идентификатор SQL-запроса) и params (параметры для SQL-запроса).
     * Запрашивает SQL-текст по идентификатору с помощью метода getSqlText.
     * Заменяет параметры в тексте запроса.
     * В зависимости от типа запроса, вызывает соответствующий метод для выполнения запроса.
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
     * List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(sqlQuery, new MapSqlParameterSource());
     * Метод queryForList из NamedParameterJdbcTemplate выполняет SQL-запрос, переданный в sqlQuery, и возвращает
     * результаты в виде списка строк (каждая строка представлена как Map<String, Object>).
     * Параметры для запроса передаются через MapSqlParameterSource, который в данном случае пуст
     * (означает, что запрос не содержит именованных параметров).
     *  @param sqlQuery  Текст SQL-запроса.
     *  @return          Ответ на запрос.
     */
    private ResponseEntity<?> executeSelect(String sqlQuery) {
        List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(sqlQuery, new MapSqlParameterSource());
        if (rows == null || rows.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        /**
          Метод createResponse используется для создания структуры ответа из результатов запроса.
          Он преобразует список строк в специальный формат,
          включающий информацию о столбцах (reqsInfo) и значения строк (values).
         */
        Map<String, Object> response = createResponse(rows);
        return ResponseEntity.ok(response);
    }

    /**
     * Логика выполнения запросов типа INSERT, UPDATE, DELETE.
     * Метод update объекта namedParameterJdbcTemplate используется для выполнения SQL-запроса.
     *
     * sqlQuery: Строка с SQL-запросом, которую нужно выполнить.
     * new MapSqlParameterSource(): Пустой объект MapSqlParameterSource, который используется
     * для передачи параметров в запрос. В данном случае он пуст, так как параметры уже встроены в sqlQuery.
     */
    private ResponseEntity<?> executeUpdateInsertDelete(String sqlQuery) {

        try {
            namedParameterJdbcTemplate.update(sqlQuery, new MapSqlParameterSource());
            /**
             * После успешной вставки данных метод вернет ответ: {"result": "OK"}
             */
            return ResponseEntity.ok(Map.of("result", "OK"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось выполнить запрос");
        }

    }

    /**
     * Создание ответа для SELECT запроса.
     */
    private Map<String, Object> createResponse(List<Map<String, Object>> rows) {
        /**
         * Здесь создается список reqsInfo, содержащий информацию о параметрах.
         * Для этого вызывается метод createReqInfo дважды: первый раз для поля DATA_ID,
         * который является первичным ключом, и второй раз для поля DATA_VALUE.
         */
        List<Map<String, Object>> reqsInfo = Arrays.asList(createReqInfo("DATA_ID", true),
                createReqInfo("DATA_VALUE", false));

        /**
         * На этом шаге создается список values, содержащий значения данных из строк результата SQL-запроса.
         * Для каждой строки (row) в rows, создается список значений (List<Object>)
         * из полей DATA_ID и DATA_VALUE и добавляется в values.
         */
        List<List<Object>> values = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            values.add(Arrays.asList(row.get("DATA_ID"), row.get("DATA_VALUE")));
        }
        /**
         * Формирует структуру ответа для запроса типа SELECT.
         * В этом шаге создается карта (Map<String, Object>) response, в которую добавляются reqsInfo и values.
         */
        Map<String, Object> response = new HashMap<>();
        response.put("reqsInfo", reqsInfo);
        response.put("values", values);

        return response;
    }

    /**
     * Создает информацию о параметре запроса.
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

        /**
         * перед обращением к базе поискать в Redis текс запроса по SQL_ID
         */

        SqlScript sqlScript = sqlScriptRepository.findBySqlId(sqlId).orElse(null);
        /**
         * Добавить в SQL_Script в Redis
         */
        return sqlScript != null ? sqlScript.getSqlText() : null;
    }



    /**
     * Обрабатывает запросы типа SELECT.
     *
     *Метод проверяет, содержит ли requestBody (карта, представляющая тело запроса) ключ DATA_ID.
     * Если ключа DATA_ID нет, метод возвращает ответ с кодом состояния 400 Bad Request
     * и сообщением "Отсутствует DATA_ID в теле запроса".
     * 1L — идентификатор SQL-скрипта, который указывает, какой скрипт SELECT нужно выполнить.
     * Map.of("DATA_IDF", requestBody.get("DATA_ID")) — карта параметров, где ключом является DATA_IDF,
     * а значением — значение, соответствующее ключу DATA_ID из requestBody.
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