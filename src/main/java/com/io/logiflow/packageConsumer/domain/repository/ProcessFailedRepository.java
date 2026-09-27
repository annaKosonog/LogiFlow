package com.io.logiflow.packageConsumer.domain.repository;

import com.io.logiflow.packageConsumer.domain.dao.ProcessFailed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessFailedRepository extends JpaRepository<ProcessFailed, Long> {
}
