package com.example.demo.src.product;


import com.example.demo.src.product.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createProduct(PostProductReq postProductReq){
        String createProductQuery = "insert into product("+
                "name, " +
                "user_id, " +
                "price, " +
                "category_id, " +
                "shipping_fee_included_flag, " +
                "location, " +
                "amount, " +
                "used_flag, " +
                "safe_payment_flag, " +
                "exchange_flag, " +
                "contents) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        Object[] createProductParams = new Object[]{
                postProductReq.getName(),
                postProductReq.getUserId(),
                postProductReq.getPrice(),
                postProductReq.getCategoryId(),
                postProductReq.getShippingFeeIncluded(),
                postProductReq.getLocation(),
                postProductReq.getAmount(),
                postProductReq.getUsed(),
                postProductReq.getSafePayment(),
                postProductReq.getExchangePayment(),
                postProductReq.getContents()
        };

        this.jdbcTemplate.update(createProductQuery, createProductParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public List<Integer> createTags(List<String> tags) {
//        String createTagsQuery = "INSERT INTO tag(name) SELECT ? FROM dual WHERE NOT EXISTS(SELECT name FROM tag WHERE name = ?)";
        String createTagsQuery = "INSERT IGNORE INTO tag(name) value (?)";
        List<Integer> tagIds = new ArrayList<>();

        for (int i = 0; i < tags.size(); i++) {
            String tag=tags.get(i);
            int rowsAffected = this.jdbcTemplate.update(createTagsQuery, tag);

            //tagId 반환
            if (rowsAffected == 0) {
                String getTagIdQuery = "SELECT tag_id FROM tag WHERE name = ?";
                tagIds.add(this.jdbcTemplate.queryForObject(getTagIdQuery, int.class, tag));
            } else {
                String lastInserIdQuery = "select last_insert_id()";
                tagIds.add(this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class));
            }
        }

        return tagIds;
    }

    public int createProductTags(int productId, List<Integer> tagIds) {
        String query = "INSERT IGNORE INTO product_tag (product_id, tag_id) VALUES (?,?) ";

//        int numOfAffectedRows=0;
//        for (int i = 0; i < tagIds.size(); i++) {
//            Object[] params = new Object[]{productId, tagIds.get(i)};
//            jdbcTemplate.update(query, params);
//        }
//
//        return numOfAffectedRows;

        return this.jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, productId);
                    ps.setInt(2, tagIds.get(i));
                }

                @Override
                public int getBatchSize() {
                    return tagIds.size();
                }
            }).length;

    }


    public int createProductImages(int productId, List<String> productImages) {
        String query = "INSERT INTO product_image (product_id, url) VALUES (?,?) ";

        return this.jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, productId);
                ps.setString(2, productImages.get(i));
            }
            @Override
            public int getBatchSize() {
                return productImages.size();
            }
        }).length;

    }
}
