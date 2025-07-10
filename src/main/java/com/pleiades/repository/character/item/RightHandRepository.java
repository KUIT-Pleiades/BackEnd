//package com.pleiades.repository.character.item;
//
//import com.pleiades.entity.character.Item.RightHand;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface RightHandRepository extends JpaRepository<RightHand, String> {
//    Optional<RightHand> findByName(String name);
//
//    @Query("SELECT r.name FROM RightHand r")
//    List<String> findAllNames();
//}
