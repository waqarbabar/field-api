package com.task.tech.repositories;

import com.task.tech.models.Field;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FieldRepository extends JpaRepository<Field, Long> {
    Optional<Field> findByFieldUuid(UUID uuid);
}
