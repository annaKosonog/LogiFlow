package com.io.logiflow.packageConsumer.domain.repository;

import com.io.logiflow.packageConsumer.domain.dao.ProcessFailed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessFailedRepository extends JpaRepository<ProcessFailed, Long> {

    Page<ProcessFailed> findProcessFailedByUuid(Pageable pageable,UUID uuid);
}
