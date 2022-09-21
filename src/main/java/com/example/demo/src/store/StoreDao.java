package com.example.demo.src.store;

import com.example.demo.src.store.model.PostStoreProfileReq;
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
        String query = "INSERT INTO store(user_id, store_name) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE user_id = ?, store_name = ?";

        return this.jdbcTemplate.update(query, userId, name, userId, name);
    }

    // Check SQL
    /** 유저 식별번호 확인 **/
    public int selectUserId(int userId){
        String query = "SELECT EXISTS (SELECT user_id FROM user WHERE user_id = ? AND status = 'Y')";

        return this.jdbcTemplate.queryForObject(query, int.class, userId);
    }

    /** 상점명 중복 확인**/
    public int selectStoreName(int userId, String storeName){
        String query = "SELECT EXISTS (SELECT store_name FROM store WHERE store_name = ? AND user_id <> ?)";

        return this.jdbcTemplate.queryForObject(query, int.class, storeName, userId);
    }

    // Update SQL
    public int updateStoreProfile(int userId, PostStoreProfileReq storeProfile){
        String query = "UPDATE store " +
                       "SET profile_image_url = ?, store_name = ?, description = ? " +
                       "WHERE user_id = ? AND status = 'Y'";

        return this.jdbcTemplate.update(query);
    }

}
