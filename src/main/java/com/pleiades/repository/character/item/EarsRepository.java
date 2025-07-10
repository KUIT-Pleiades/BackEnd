//package com.pleiades.repository.character.item;
//
//import com.pleiades.entity.character.Item.Ears;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface EarsRepository extends JpaRepository<Ears, String> {
//    Optional<Ears> findByName(String name);
//
//    @Query("SELECT e.name FROM Ears e")
//    List<String> findAllNames();
//}
