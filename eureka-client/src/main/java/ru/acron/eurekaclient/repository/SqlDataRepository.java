package ru.acron.eurekaclient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.acron.eurekaclient.model.SqlData;

import java.util.Optional;

@Repository
public interface SqlDataRepository extends JpaRepository<SqlData, String> {

   Optional<SqlData> findByDataId(String dataId);
   Optional<SqlData> findByDataValue(String dataValue);
}