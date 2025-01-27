package com.pleiades.service;

import com.pleiades.strings.ValidationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public class DuplicationService<T> {
    JpaRepository<T, String> repository;

    public DuplicationService(JpaRepository<T, String> repository) {
        this.repository = repository;
    }

    public ValidationStatus checkIdDuplication(String id) {
        Optional<T> object= repository.findById(id);
        return object.isPresent()? ValidationStatus.NOT_VALID:ValidationStatus.VALID;
    }
}
