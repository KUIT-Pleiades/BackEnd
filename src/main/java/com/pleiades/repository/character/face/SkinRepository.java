package com.pleiades.repository.character.face;

import com.pleiades.entity.character.face.Skin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkinRepository extends JpaRepository<Skin, String> {
    Optional<Skin> findByName(String name);
}
