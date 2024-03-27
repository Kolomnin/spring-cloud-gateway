package ru.acron.eurekaclient.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "SQL_DATA")
public class SqlData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String dataId;

    @Column(name = "DATA_VALUE")
    private String dataValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqlData sqlData = (SqlData) o;
        return Objects.equals(dataId, sqlData.dataId) && Objects.equals(dataValue, sqlData.dataValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataId, dataValue);
    }
}