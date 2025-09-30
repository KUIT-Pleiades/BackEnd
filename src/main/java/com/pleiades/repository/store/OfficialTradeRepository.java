package com.pleiades.repository.store;

import com.pleiades.entity.store.OfficialTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficialTradeRepository extends JpaRepository<OfficialTrade, Long> {
    
}
