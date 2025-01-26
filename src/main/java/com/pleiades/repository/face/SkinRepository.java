package com.pleiades.repository.face;

import com.pleiades.entity.User;
import com.pleiades.entity.face.Skin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkinRepository extends JpaRepository<Skin, String> {

}
