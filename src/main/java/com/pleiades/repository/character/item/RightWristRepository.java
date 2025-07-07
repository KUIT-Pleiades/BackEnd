package com.pleiades.repository.character.item;

import com.pleiades.entity.character.Item.RightWrist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RightWristRepository extends JpaRepository<RightWrist, String> {
    Optional<RightWrist> findByName(String name);

    @Query("SELECT r.name FROM RightWrist r")
    List<String> findAllNames();
}
