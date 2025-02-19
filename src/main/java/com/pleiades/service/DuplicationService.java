package com.pleiades.service;

import com.pleiades.strings.ValidationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DuplicationService<T> {
    JpaRepository<T, String> repository;

    public DuplicationService(JpaRepository<T, String> repository) {
        this.repository = repository;
    }

    private ValidationStatus checkIdDuplication(String id) {
        Optional<T> object= repository.findById(id);
        return object.isPresent()? ValidationStatus.NOT_VALID:ValidationStatus.VALID;
    }

    public ResponseEntity<Map<String, Object>> responseIdDuplication(String id) {
        Map<String, Object> body = new HashMap<>();
        ValidationStatus idValidation = checkIdDuplication(id);

        if (idValidation == ValidationStatus.NOT_VALID) {
            body.put("available", false);
            body.put("message", "The username is already taken.");
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(body);
        }

        body.put("available", true);
        body.put("message", "The username is available.");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(body);
    }
}
