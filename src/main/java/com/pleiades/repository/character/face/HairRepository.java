//package com.pleiades.repository.character.face;
//
//import com.pleiades.entity.character.face.Hair;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface HairRepository extends JpaRepository<Hair, String> {
//    Optional<Hair> findByName(String name);
//
//    @Query("SELECT h.name FROM Hair h")
//    List<String> findAllNames();
//}
