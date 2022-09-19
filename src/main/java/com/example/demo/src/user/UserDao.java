package com.example.demo.src.user;

import com.example.demo.src.user.model.UserInfoRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Insert SQL
    /** 회원가입 **/
    public int insertUser(String email) {
        String query = "INSERT INTO user (email) VALUES(?)";

        this.jdbcTemplate.update(query, email);

        String userIdQuery = "SELECT last_insert_id()";

        return this.jdbcTemplate.queryForObject(userIdQuery, int.class);

    }

    /** SNS 연동 **/
    public void updateSNSFlag(int userId){
        String query = "UPDATE user SET sns_flag = 'Y' WHERE user_id = ?";

        this.jdbcTemplate.update(query, userId);
    }

    public void insertSNSInfo(int userId, int snsTypeId ){
        String query = "INSERT INTO sns(user_id, sns_type_id) VALUES(?, ?)";

        this.jdbcTemplate.update(query, userId, snsTypeId);

    }


    // Check SQL
    /** 이메일 확인 **/
    public UserInfoRes checkUserEmail(String email) {
        String query = "SELECT user_id, sns_flag FROM user WHERE email = ?";

        try {
            return this.jdbcTemplate.queryForObject(query,
                    (rs, rowNum) -> new UserInfoRes(
                            rs.getInt("user_id"),
                            rs.getString("sns_flag"))
                    , email);

        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

}
