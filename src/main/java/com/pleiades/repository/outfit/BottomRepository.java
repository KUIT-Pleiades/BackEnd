package com.pleiades.repository.outfit;

import com.pleiades.entity.User;
import com.pleiades.entity.outfit.Bottom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BottomRepository extends JpaRepository<Bottom, String> {

}
