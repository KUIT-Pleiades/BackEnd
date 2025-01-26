package com.pleiades.repository.face;

import com.pleiades.entity.User;
import com.pleiades.entity.face.Expression;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpressionRepository extends JpaRepository<Expression, String> {

}
