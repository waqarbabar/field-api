package com.task.tech.repositories;

import com.task.tech.models.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {
    Optional<Field> findByFieldUuid(UUID uuid);
}
