//package com.pleiades.repository.character.item;
//
//import com.pleiades.entity.character.Item.Neck;
//import com.pleiades.entity.character.Item.RightWrist;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface NeckRepository extends JpaRepository<Neck, String> {
//    Optional<Neck> findByName(String name);
//
//    @Query("SELECT n.name FROM Neck n")
//    List<String> findAllNames();
//}
