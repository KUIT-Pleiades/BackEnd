package com.pleiades.repository.outfit;

import com.pleiades.entity.User;
import com.pleiades.entity.outfit.Bottom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BottomRepository extends JpaRepository<Bottom, String> {
    Optional<Bottom> findById(Long id);
    Optional<Bottom> findByName(String name);

}
