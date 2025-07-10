//package com.pleiades.repository.character.face;
//
//import com.pleiades.entity.character.face.Expression;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface ExpressionRepository extends JpaRepository<Expression, String> {
//    Optional<Expression> findByName(String name);
//
//    @Query("SELECT e.name FROM Expression e")
//    List<String> findAllNames();
//}
