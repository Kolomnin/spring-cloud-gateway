package ru.acron.eurekaclient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.acron.eurekaclient.model.SqlScript;

import java.util.Optional;


@Repository
public interface SqLScriptRepository extends JpaRepository<SqlScript, Long> {

    Optional<SqlScript> findBySqlId(Long sqlId);
    Optional<SqlScript> findBySqlText(String sqlText);

}