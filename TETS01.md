import java.util.ArrayList;
import java.util.List;

/**
 * 单元格级结果（HTML绑定核心）
 */
public class CellResult {

    /** 列名 */
    private String column;

    /** 单元格值 */
    private String value;

    /** 错误信息 */
    private List<String> errors = new ArrayList<>();

    /** 状态（用于画面颜色） */
    private CellStatus status = CellStatus.NORMAL;

    public CellResult(String column, String value) {
        this.column = column;
        this.value = value;
    }

    public void addError(String message) {
        errors.add(message);
        status = CellStatus.ERROR;
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    // getters
    public String getColumn() { return column; }
    public String getValue() { return value; }
    public List<String> getErrors() { return errors; }
    public CellStatus getStatus() { return status; }
}


/**
 * 单元格状态（HTML样式控制）
 */
public enum CellStatus {
    NORMAL,
    ERROR,
    WARNING,
    UPDATED,
    NEW,
    DELETED
}


import java.util.*;

/**
 * 一行CSV结果
 */
public class RowResult {

    private int rowNo;

    /** key=列名 */
    private Map<String, CellResult> cells =
            new LinkedHashMap<>();

    /** 行级错误 */
    private List<String> globalErrors =
            new ArrayList<>();

    public RowResult(int rowNo) {
        this.rowNo = rowNo;
    }

    public void addCell(CellResult cell) {
        cells.put(cell.getColumn(), cell);
    }

    public CellResult getCell(String column) {
        return cells.get(column);
    }

    public Collection<CellResult> getCells() {
        return cells.values();
    }

    public int getRowNo() {
        return rowNo;
    }
}


import java.util.List;

/**
 * 整体CSV结果（画面model）
 */
public class CsvValidationResult {

    private List<String> headers;
    private List<RowResult> rows;

    public CsvValidationResult(
            List<String> headers,
            List<RowResult> rows) {
        this.headers = headers;
        this.rows = rows;
    }

    public List<String> getHeaders() { return headers; }
    public List<RowResult> getRows() { return rows; }
}

@Service
public class CsvValidatorService {

    public CsvValidationResult validate(
            List<CsvRow> csvRows,
            CsvSchema schema,
            List<String> headers) {

        List<RowResult> results = new ArrayList<>();

        int rowNo = 1;

        for (CsvRow row : csvRows) {

            RowResult rowResult = new RowResult(rowNo++);

            // 创建所有cell
            for (String header : headers) {
                rowResult.addCell(
                    new CellResult(header, row.get(header))
                );
            }

            validateRow(row, schema, rowResult);

            results.add(rowResult);
        }

        return new CsvValidationResult(headers, results);
    }


    private void validateRow(
        CsvRow row,
        CsvSchema schema,
        RowResult rowResult) {

    for (FieldRule rule : schema.getFields()) {

        CellResult cell =
                rowResult.getCell(rule.getFieldName());

        String value = cell.getValue();

        // 必填
        if (rule.isRequired()
                && (value == null || value.isBlank())) {

            cell.addError(
                rule.getLabel() + " required");
        }

        // 长度
        if (rule.getMaxLength() != null
                && value != null
                && value.length() > rule.getMaxLength()) {

            cell.addError(
                rule.getLabel() + " too long");
        }

        // regex
        if (rule.getRegex() != null
                && value != null
                && !value.matches(rule.getRegex())) {

            cell.addError(
                rule.getLabel() + " invalid");
        }
    }
}
