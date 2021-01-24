package com.task.tech.repositories;

import com.task.tech.models.Field;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FieldRepository extends CrudRepository<Field, Long> {
    Optional<Field> findByFieldUuid(UUID uuid);
    List<Field> findAll(Pageable pageable);
}
