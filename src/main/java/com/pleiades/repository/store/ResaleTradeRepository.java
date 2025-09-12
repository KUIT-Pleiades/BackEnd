package com.pleiades.repository.store;

import com.pleiades.entity.store.ResaleTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResaleTradeRepository extends JpaRepository<ResaleTrade, Long> {
}
