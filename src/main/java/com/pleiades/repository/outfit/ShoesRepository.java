package com.pleiades.repository.outfit;

import com.pleiades.entity.User;
import com.pleiades.entity.outfit.Shoes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoesRepository extends JpaRepository<Shoes, String> {

}
