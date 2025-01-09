package com.pleiades.dao;

import com.pleiades.dto.KakaoTokenDto;
import com.pleiades.jdbc.JdbcTemplate;
import com.pleiades.jdbc.PreparedStatementSetter;
import com.pleiades.jdbc.RowMapper;
import com.pleiades.model.User;

import java.sql.SQLException;
import java.util.List;


public class UserDao {
    private JdbcTemplate<User> jdbcTemplate;

    //todo: insert
    public int insert(User user) throws SQLException
    {
        User findId = findByUserId(user.getUserId());
        if (findId != null) {return -1;}

        String sql = "INSERT INTO USER VALUES (?, ?, ?, ?)";
        PreparedStatementSetter pstmtSetter = pstmt -> {
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getNickname());
            pstmt.setTimestamp(3, user.getBirthDate());
            pstmt.setTimestamp(4, user.getSignupDate());
        };

        jdbcTemplate.update(sql, pstmtSetter);
        return 1;
    }

    //todo: update
    public void update(User user) throws SQLException
    {
        String sql = "UPDATE USER SET nickname=?, birthdate=? WHERE userId = ?";
        PreparedStatementSetter pstmtSetter = pstmt -> {
            pstmt.setString(1, user.getNickname());
            pstmt.setTimestamp(2, user.getBirthDate());
        };
        jdbcTemplate.update(sql, pstmtSetter);
    }

    //todo: delete
    public void delete(User user) throws SQLException
    {
        String sql = "DELETE FROM USER WHERE userId = ?";
        PreparedStatementSetter pstmtSetter = pstmt -> {
            pstmt.setString(1, user.getUserId());
        };

        jdbcTemplate.update(sql, pstmtSetter);
    }

    //todo: find by userid
    public User findByUserId(String userId) throws SQLException
    {
        String sql = "SELECT * FROM USER WHERE userId = ?";
        PreparedStatementSetter pstmtSetter = pstmt -> {
            pstmt.setString(1, userId);
        };
        RowMapper<User> rowMapper = (rs) ->
                new User(rs.getString("userId"),
                rs.getString("nickname"),
                rs.getTimestamp("birthDate"),
                rs.getTimestamp("signupDate"));
        return jdbcTemplate.queryOne(sql, pstmtSetter, rowMapper);
    }

    //todo: find by socialid
    public User findBySocialId(String email) throws SQLException
    {
        String sql = "SELECT userId FROM SOCIAL_USER WHERE socialId = ?";
        PreparedStatementSetter pstmtSetter = pstmt -> {
            pstmt.setString(1, email);
        };
        RowMapper<User> rowMapper = (rs) ->
                new User(rs.getString("userId"),
                        rs.getString("nickname"),
                        rs.getTimestamp("birthDate"),
                        rs.getTimestamp("signupDate"));
        return jdbcTemplate.queryOne(sql, pstmtSetter, rowMapper);
    }

    //todo: find all
    public List<User> findAll(User user) throws SQLException
    {
        String sql = "SELECT * FROM USERS";
        RowMapper<User> rowMapper = (rs) ->
                new User(rs.getString("userId"),
                        rs.getString("nickname"),
                        rs.getTimestamp("birthDate"),
                        rs.getTimestamp("signupDate"));
        return jdbcTemplate.queryAll(sql, rowMapper);
    }
}
