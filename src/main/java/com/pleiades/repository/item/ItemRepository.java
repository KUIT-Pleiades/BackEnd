package com.pleiades.repository.item;

import com.pleiades.entity.User;
import com.pleiades.entity.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

}
