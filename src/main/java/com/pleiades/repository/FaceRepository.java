package com.pleiades.repository;

import com.pleiades.entity.face.Face;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FaceRepository extends JpaRepository<Face, String> {
    Optional<Face> findById(String faceId);
}
