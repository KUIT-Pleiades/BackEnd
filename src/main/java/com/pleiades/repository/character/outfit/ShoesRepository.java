//package com.pleiades.repository.character.outfit;
//
//import com.pleiades.entity.character.outfit.Shoes;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface ShoesRepository extends JpaRepository<Shoes, String> {
//    Optional<Shoes> findByName(String name);
//
//    @Query("SELECT s.name FROM Shoes s")
//    List<String> findAllNames();
//}
