package ru.acron.eurekaclient.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "SQL_SCRIPT")
public class SqlScript {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sqlId;

    @Column(name = "SQL_TEXT")
    private String sqlText;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqlScript sqlScript = (SqlScript) o;
        return Objects.equals(sqlId, sqlScript.sqlId) && Objects.equals(sqlText, sqlScript.sqlText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sqlId, sqlText);
    }
}
