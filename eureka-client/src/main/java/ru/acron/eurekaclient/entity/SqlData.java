package ru.acron.eurekaclient.entity;

import jakarta.persistence.*;

@Entity
public class SqlData {

    @Id
    private String dataId;
    private String dataValue;

    // Геттеры и сеттеры
    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }
}