package com.example.demo.src.review;

import com.example.demo.src.review.model.PostReviewReq;
import com.example.demo.src.review.model.ReviewInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ReviewDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Insert SQL
    public void insertReview(int userId, int storeId, PostReviewReq postReviewReq){
        String query = "INSERT INTO review(reviewer_id, target_user_id, rating, content) " +
                        "VALUES(?, ?, ?, ?) ";

        Object[] queryParams = new Object[]{userId, storeId,postReviewReq.getRating(), postReviewReq.getContents()};

        this.jdbcTemplate.update(query, queryParams);
    }

    //Select SQL
    public List<ReviewInfo> selectReviews(int storeId, int reviewId, String date, int size){
        String query = "SELECT r.review_id, rating, content, store_name, r.created_at, t.product_id, name " +
                        "FROM review r " +
                        "INNER JOIN store s on s.user_id = r.reviewer_id " +
                        "INNER JOIN trade t on r.trade_id = t.trade_id " +
                        "LEFT JOIN product p on t.product_id = p.product_id " +
                        "WHERE (target_user_id = ? AND r.status = 'Y') " +
                        "AND ((r.created_at = ? AND review_id > ?) " +
                        "OR r.created_at < ?) " +
                        "ORDER BY r.created_at desc , review_id " +
                        "LIMIT ?";

        Object[] reviewsParams = new Object[]{storeId, date, reviewId, date, size + 1};

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new ReviewInfo(
                        rs.getInt("review_id"),
                        rs.getFloat("rating"),
                        rs.getString("content"),
                        rs.getString("store_name"),
                        rs.getString("r.created_at"),
                        rs.getInt("t.product_id"),
                        rs.getString("name")),
                reviewsParams);
    }

}
