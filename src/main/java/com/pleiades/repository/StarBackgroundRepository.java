package com.pleiades.repository;

import com.pleiades.entity.StarBackground;
import com.pleiades.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StarBackgroundRepository {
    Optional<StarBackground> findById(int id);
}
