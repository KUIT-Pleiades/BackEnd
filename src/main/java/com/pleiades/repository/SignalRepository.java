package com.pleiades.repository;

import com.pleiades.entity.Signal;
import com.pleiades.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignalRepository extends JpaRepository<Signal, Long> {
    List<Signal> findByReceiver(User receiver);
    boolean existsBySenderAndReceiver(User sender, User receiver);
    void deleteBySenderAndReceiver(User sender, User receiver);
    void deleteAllBySender(User sender);
    void deleteAllByReceiver(User receiver);
}
