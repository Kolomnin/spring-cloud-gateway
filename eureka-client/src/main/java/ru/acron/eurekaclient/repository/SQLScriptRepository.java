package ru.acron.eurekaclient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.acron.eurekaclient.entity.SqlScript;

import java.util.Optional;


@Repository
public interface SQLScriptRepository extends JpaRepository<SqlScript, Long> {
    Optional<SqlScript> findBySqlText(String sqlText);
}