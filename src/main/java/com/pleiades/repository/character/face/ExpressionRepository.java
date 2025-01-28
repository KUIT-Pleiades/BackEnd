package com.pleiades.repository.character.face;

import com.pleiades.entity.character.face.Expression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpressionRepository extends JpaRepository<Expression, String> {
    Optional<Expression> findById(Long Id);
    Optional<Expression> findByName(String name);
}
