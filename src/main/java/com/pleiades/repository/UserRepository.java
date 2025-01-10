package com.pleiades.repository;

import com.pleiades.jdbc.JdbcTemplate;
import com.pleiades.jdbc.PreparedStatementSetter;
import com.pleiades.jdbc.RowMapper;
import com.pleiades.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);

    Optional<User> findByPhoneNumber(String phone_number);

    List<User> findAll(User user);
}
