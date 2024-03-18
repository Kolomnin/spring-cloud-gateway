package ru.acron.eurekaclient.entity;

import jakarta.persistence.Entity;

import javax.persistence.*;

@Entity
@Table(name = "SQL_SCRIPT")
public class SqlScript {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sqlId;

    @Column(name = "SQL_TEXT")
    private String sqlText;

    // Геттеры и сеттеры
    public Long getSqlId() {
        return sqlId;
    }

    public void setSqlId(Long sqlId) {
        this.sqlId = sqlId;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }
}
