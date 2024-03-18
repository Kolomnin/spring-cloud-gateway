package ru.acron.eurekaclient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.acron.eurekaclient.entity.SqlData;

@Repository
public interface SqlDataRepository extends JpaRepository<SqlData, String> {
}