package com.pleiades.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {
    //todo: 비밀번호 .properties or .yml에 저장
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://pleiades.cpmukq0mgpjz.ap-southeast-2.rds.amazonaws.com:3306/pleiades?serverTimezone=Asia/Seoul";
    private static final String DB_USERNAME = "kuit";
    private static final String DB_PW = "kuitpleiades";

    private static BasicDataSource ds;
    public static DataSource getDataSource() {
        if (ds == null) {
            ds = new BasicDataSource();
            ds.setDriverClassName(DB_DRIVER);
            ds.setUrl(DB_URL);
            ds.setUsername(DB_USERNAME);
            ds.setPassword(DB_PW);
        }
        return ds;
    }

    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
