//package com.pleiades.repository.character.face;
//
//import com.pleiades.entity.character.face.Skin;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface SkinRepository extends JpaRepository<Skin, String> {
//    Optional<Skin> findByName(String name);
//
//    @Query("SELECT s.name FROM Skin s")
//    List<String> findAllNames();
//}
