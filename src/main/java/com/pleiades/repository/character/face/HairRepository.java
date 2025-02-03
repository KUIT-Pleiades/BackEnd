package com.pleiades.repository.character.face;

import com.pleiades.entity.character.face.Hair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HairRepository extends JpaRepository<Hair, String> {
    Optional<Hair> findByName(String name);
}
