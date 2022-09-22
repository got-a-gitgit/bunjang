package com.example.demo.src.wish;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;


@Repository
public class WishDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void createWish(int userId, int productId) {
        String query = "INSERT INTO wish(user_id,product_id) VALUES(?, ?) ON DUPLICATE KEY UPDATE status= 'Y'";
        Object[] params = new Object[]{userId, productId};
        this.jdbcTemplate.update(query, params);
    }

    public void deleteWish(int userId, int productId) {
        String query = "UPDATE wish SET status = 'N' WHERE user_id=? AND product_id=? AND status='Y'";
        Object[] params = new Object[]{userId, productId};
        this.jdbcTemplate.update(query, params);
    }
}
