package com.example.demo.src.store;

import com.example.demo.src.store.model.GetFollowRes;
import com.example.demo.src.store.model.PatchStoreProfileReq;
import com.example.demo.src.store.model.PatchStoreProfileRes;
import com.example.demo.src.store.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

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

    /** 팔로우 상태 수정**/
    public String insertFollowing(int userId, int followId){
        String query = "INSERT INTO follow(followee, follower, status) VALUES(?, ?, 'Y') " +
                        "ON DUPLICATE KEY UPDATE status = CASE " +
                        "WHEN status = 'Y' THEN 'N' " +
                        "WHEN status = 'N' THEN 'Y' " +
                        "END";
        this.jdbcTemplate.update(query, userId, followId);  // 팔로우 관계 수정

        String resultQuery = "SELECT status FROM follow WHERE followee = ? and follower = ?";

        return this.jdbcTemplate.queryForObject(resultQuery, String.class, userId, followId); // 결과 반환
    }

    // Select SQL
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

    /** 상점 프로필 조회 **/
    public PatchStoreProfileRes selectStoreProfile(int userId){
        String query = "SELECT profile_image_url, store_name, description FROM store WHERE user_id = ?";

        return this.jdbcTemplate.queryForObject(query,
                (rs, rowNum) -> new PatchStoreProfileRes(
                        rs.getString("profile_image_url"),
                        rs.getString("store_name"),
                        rs.getString("description")),
                userId);
    }


    /** 팔로워 목록 조회 **/
    public List<GetFollowRes> selectFollowers(int storeId, int lastId){
        String query = "SELECT s.user_id, profile_image_url, store_name, alarm_flag, IFNULL(followers, 0) AS followers, IFNULL(products, 0) AS products " +
                        "FROM store s " +
                        "LEFT JOIN (SELECT follower, alarm_flag, COUNT(IF(follower IS NULL, 'NULL', follower)) AS followers, updated_at  FROM follow GROUP BY follower) followers_tb " +
                        "    ON s.user_id = followers_tb.follower " +
                        "LEFT JOIN (SELECT user_id, COUNT(product_id) AS products FROM product GROUP BY user_id) products_tb " +
                        "    ON s.user_id = products_tb.user_id " +
                        "WHERE s.user_id IN (SELECT followee FROM follow origin WHERE origin.follower = ? AND origin.status ='Y') " +
                        "AND s.user_id > ? " +
                        "ORDER BY followers_tb.updated_at, user_id " +
                        "LIMIT 20";


        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetFollowRes(
                        rs.getInt("s.user_id"),
                        rs.getString("profile_image_url"),
                        rs.getString("store_name"),
                        rs.getString("alarm_flag"),
                        rs.getInt("followers"),
                        rs.getInt("products")),
                storeId, lastId);
    }

    /** 팔로잉 목록 조회 **/
    public List<GetFollowRes> selectFollowings(int storeId, int lastId){
        String query = "SELECT s.user_id, profile_image_url, store_name, alarm_flag, IFNULL(followers, 0) AS followers, IFNULL(products, 0) AS products " +
                "FROM store s " +
                "LEFT JOIN (SELECT follower, alarm_flag, COUNT(IF(follower IS NULL, 'NULL', follower)) AS followers, updated_at FROM follow GROUP BY follower) followers_tb " +
                "    ON s.user_id = followers_tb.follower " +
                "LEFT JOIN (SELECT user_id, COUNT(product_id) AS products FROM product GROUP BY user_id) products_tb " +
                "    ON s.user_id = products_tb.user_id " +
                "WHERE s.user_id IN (SELECT follower FROM follow origin WHERE origin.followee = ? AND origin.status ='Y') " +
                "AND s.user_id > ? " +
                "ORDER BY followers_tb.updated_at, user_id " +
                "LIMIT 20";

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetFollowRes(
                        rs.getInt("s.user_id"),
                        rs.getString("profile_image_url"),
                        rs.getString("store_name"),
                        rs.getString("alarm_flag"),
                        rs.getInt("followers"),
                        rs.getInt("products")),
                storeId, lastId);
    }

    /** 상점별 판매목록 3개 가져오기 **/
    public List<Product> selectProductsByStore(int userId){
        String query = "SELECT user_id, p.product_id, pi.url, price " +
                        "FROM product p " +
                        "INNER JOIN product_image pi ON p.product_id = pi.product_id " +
                        "WHERE user_id = ? AND p.status= 'S' LIMIT 3";
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new Product(
                        rs.getInt("p.product_id"),
                        rs.getString("pi.url"),
                        rs.getInt("price")
                ), userId);
    }


    // Update SQL
    /** 상점 소개 수정 **/
    public int updateStoreProfile(int userId, PatchStoreProfileReq storeProfile){
        String query = "UPDATE store " +
                       "SET profile_image_url = ?, store_name = ?, description = ? " +
                       "WHERE user_id = ? AND status = 'Y'";

        Object[] storeProfileParams = new Object[]{storeProfile.getOriginImageUrl(),
            storeProfile.getStoreName(), storeProfile.getDescription(), userId};

        return this.jdbcTemplate.update(query, storeProfileParams);
    }

}
