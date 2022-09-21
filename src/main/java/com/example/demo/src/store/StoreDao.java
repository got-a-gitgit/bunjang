package com.example.demo.src.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class StoreDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    // Insert SQL
    /** 상점명 삽입 **/
    public int insertStoreName(int userId, String name) {
        String query = "INSERT INTO store(user_id, store_name) VALUES (?, ?)";

        return this.jdbcTemplate.update(query, userId, name);
    }

    // Check SQL
    /** 유저 식별번호 확인 **/
    public int selectUserId(int userId){
        String query = "SELECT EXISTS (SELECT user_id FROM user WHERE user_id = ?)";

        return this.jdbcTemplate.queryForObject(query, int.class, userId);
    }

}
