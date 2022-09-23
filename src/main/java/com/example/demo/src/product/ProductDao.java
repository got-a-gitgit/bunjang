package com.example.demo.src.product;


import com.example.demo.config.BaseException;
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

import static com.example.demo.config.BaseResponseStatus.*;

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

    public int deleteProduct(int productId) {
        String query = "UPDATE product SET status = 'D' WHERE product_id=? AND status!='D'";
        return this.jdbcTemplate.update(query, productId);
    }

    public GetProductRes getProduct(int productId) throws BaseException {
        String query = "SELECT " +
                "p.product_id as product_id, " +
                "p.name as name, " +
                "p.user_id as user_id, " +
                "price, " +
                "p.category_id as category_id, " +
                "c.name as category_name, " +
                "shipping_fee_included_flag, " +
                "location, " +
                "amount, " +
                "used_flag, " +
                "safe_payment_flag, "+
                "exchange_flag, "+
                "contents, "+
                "view, "+
                "COUNT(wish_id) as wishes, "+
                "p.status as status, " +
                "c.created_at as created_at\n" +
                "FROM product p\n" +
                "JOIN category c on p.category_id = c.category_id\n" +
                "JOIN wish w on p.product_id = w.product_id\n" +
                "WHERE p.product_id = ? AND p.status!='D'\n " +
                "GROUP BY p.product_id";

        try {
            return this.jdbcTemplate.queryForObject(query,
                    (rs, rowNum) -> new GetProductRes(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getInt("user_id"),
                            rs.getInt("price"),
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getString("shipping_fee_included_flag"),
                            rs.getString("location"),
                            rs.getInt("amount"),
                            rs.getString("used_flag"),
                            rs.getString("safe_payment_flag"),
                            rs.getString("exchange_flag"),
                            rs.getString("contents"),
                            rs.getInt("view"),
                            rs.getInt("wishes"),
                            rs.getString("status"),
                            rs.getString("created_at")
                    ),
                    productId);
        } catch (Exception exception) {
            throw new BaseException(NON_EXISTENT_PRODUCT);
        }
    }

    public List<String> getProductImages(int productId) {
        String query = "SELECT url\n" +
                "FROM product p\n" +
                "JOIN product_image pi on p.product_id = pi.product_id\n" +
                "WHERE p.product_id = ?";

        return this.jdbcTemplate.query(query,
                (rs,rowNum)-> new String(
                        rs.getString("url")
                ),productId);
    }

    public List<String> getProductTags(int productId) {
        String query = "SELECT t.name as tag\n" +
                "FROM product p\n" +
                "JOIN product_tag pt on p.product_id = pt.product_id\n" +
                "JOIN tag t on t.tag_id = pt.tag_id\n" +
                "WHERE p.product_id = ?";

        return this.jdbcTemplate.query(query,
                (rs,rowNum)-> new String(
                        rs.getString("tag")
                ),productId);
    }

    public void increaseProductView(int productId, int view) {
        String query = "UPDATE product SET view = ? WHERE product_id=?";
        Object[] params = new Object[]{view,productId};
        this.jdbcTemplate.update(query, params);
    }

    public List<GetStoreProductRes> getProductListByStoreId(int userId, int storeId, Integer lastProductId, Integer size) {
        String query = "SELECT p.product_id as product_id,\n" +
                "       p.user_id    as user_id,\n" +
                "       price,\n" +
                "       name,\n" +
                "       safe_payment_flag,\n" +
                "        CASE\n" +
                "            WHEN w.status ='Y' THEN 'Y'\n" +
                "            ELSE 'N'\n" +
                "        END as wish,\n" +
                "        pi.url as image\n" +
                "FROM product p\n" +
                "         LEFT JOIN (SELECT product_id, status\n" +
                "                    FROM wish\n" +
                "                    WHERE user_id = ?) w on p.product_id = w.product_id\n" +
                "         LEFT JOIN (SELECT url, MIN(product_image_id), product_id\n" +
                "               FROM product_image\n" +
                "               GROUP BY product_id) pi on p.product_id = pi.product_id\n" +
                "WHERE p.user_id = ? AND p.product_id<? AND p.status!='D'\n"+
                "order by p.product_id DESC\n" +
                "LIMIT ?";

        Object[] params = new Object[]{userId, storeId, lastProductId, size};
        return this.jdbcTemplate.query(query,
                (rs,rowNum)->new GetStoreProductRes(
                    rs.getInt("product_id"),
                    rs.getInt("user_id"),
                    rs.getInt("price"),
                        rs.getString("image"),
                        rs.getString("name"),
                        rs.getString("safe_payment_flag"),
                        rs.getString("wish")
                ),
                params);
    }

    public List<GetStoreProductRes> getFirstProductListByStoreId(int userId, int storeId, Integer size) {
        String query = "SELECT p.product_id as product_id,\n" +
                "       p.user_id    as user_id,\n" +
                "       price,\n" +
                "       name,\n" +
                "       safe_payment_flag,\n" +
                "        CASE\n" +
                "            WHEN w.status ='Y' THEN 'Y'\n" +
                "            ELSE 'N'\n" +
                "        END as wish,\n" +
                "        pi.url as image\n" +
                "FROM product p\n" +
                "         LEFT JOIN (SELECT product_id, status\n" +
                "                    FROM wish\n" +
                "                    WHERE user_id = ?) w on p.product_id = w.product_id\n" +
                "         LEFT JOIN (SELECT url, MIN(product_image_id), product_id\n" +
                "               FROM product_image\n" +
                "               GROUP BY product_id) pi on p.product_id = pi.product_id\n" +
                "WHERE p.user_id = ? AND p.status!='D'"+
                "order by p.product_id DESC\n" +
                "LIMIT ?";

        Object[] params = new Object[]{userId, storeId, size};
        return this.jdbcTemplate.query(query,
                (rs,rowNum)->new GetStoreProductRes(
                        rs.getInt("product_id"),
                        rs.getInt("user_id"),
                        rs.getInt("price"),
                        rs.getString("image"),
                        rs.getString("name"),
                        rs.getString("safe_payment_flag"),
                        rs.getString("wish")),
                params);
    }

    public List<RecommendedProduct> getFirstProductList(int userId) {
        String query = "SELECT p.product_id as product_id, " +
                "user_id, " +
                "pi.url as image,\n" +
                "       CASE\n" +
                "           WHEN w.status ='Y' THEN 'Y'\n" +
                "           ELSE 'N'\n" +
                "        END as wish,\n" +
                "price, " +
                "name, " +
                "location, " +
                "p.created_at as created_at, " +
                "safe_payment_flag,\n" +
                "    CASE\n" +
                "        WHEN ISNULL(wc.wishes) THEN 0\n" +
                "        ELSE wc.wishes\n" +
                "    END as wishes\n" +
                "FROM product p\n" +
                "         LEFT JOIN(SELECT product_id, status\n" +
                "                   FROM wish\n" +
                "                   WHERE user_id = ?) w on p.product_id = w.product_id\n" +
                "         LEFT JOIN(SELECT url, MIN(product_image_id), product_id\n" +
                "                   FROM product_image\n" +
                "                   GROUP BY product_id) pi on p.product_id = pi.product_id\n" +
                "         LEFT JOIN(SELECT product_id, COUNT(product_id) as wishes\n" +
                "                   FROM wish\n" +
                "                   GROUP BY product_id) as wc on p.product_id = wc.product_id\n" +
                "WHERE p.status!='D'"+
                "ORDER BY p.product_id DESC\n" +
                "LIMIT 21";

        return this.jdbcTemplate.query(query,
                (rs,rowNum)->new RecommendedProduct(
                        rs.getInt("product_id"),
                        rs.getInt("user_id"),
                        rs.getString("image"),
                        rs.getString("wish"),
                        rs.getInt("price"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getString("created_at"),
                        rs.getString("safe_payment_flag"),
                        rs.getInt("wishes")
                ),
                userId);
    }

    public List<RecommendedProduct> getProductList(int userId, Integer lastProductId) {
        String query = "SELECT p.product_id as product_id, " +
                "user_id, " +
                "pi.url as image,\n" +
                "       CASE\n" +
                "           WHEN w.status ='Y' THEN 'Y'\n" +
                "           ELSE 'N'\n" +
                "        END as wish,\n" +
                "price, " +
                "name, " +
                "location, " +
                "p.created_at as created_at, " +
                "safe_payment_flag,\n" +
                "    CASE\n" +
                "        WHEN ISNULL(wc.wishes) THEN 0\n" +
                "        ELSE wc.wishes\n" +
                "    END as wishes\n" +
                "FROM product p\n" +
                "         LEFT JOIN(SELECT product_id, status\n" +
                "                   FROM wish\n" +
                "                   WHERE user_id = ?) w on p.product_id = w.product_id\n" +
                "         LEFT JOIN(SELECT url, MIN(product_image_id), product_id\n" +
                "                   FROM product_image\n" +
                "                   GROUP BY product_id) pi on p.product_id = pi.product_id\n" +
                "         LEFT JOIN(SELECT product_id, COUNT(product_id) as wishes\n" +
                "                   FROM wish\n" +
                "                   GROUP BY product_id) as wc on p.product_id = wc.product_id\n" +
                "WHERE p.product_id<? AND p.status!='D'"+
                "ORDER BY p.product_id DESC\n" +
                "LIMIT 21";

        Object[] params = new Object[]{userId, lastProductId};

        return this.jdbcTemplate.query(query,
                (rs,rowNum)->new RecommendedProduct(
                        rs.getInt("product_id"),
                        rs.getInt("user_id"),
                        rs.getString("image"),
                        rs.getString("wish"),
                        rs.getInt("price"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getString("created_at"),
                        rs.getString("safe_payment_flag"),
                        rs.getInt("wishes")
                ),
                params);
    }

    public List<GetStoreProductRes> getWholeProductListByStoreId(int userId, int storeId) {
        String query = "SELECT p.product_id as product_id,\n" +
                "       p.user_id    as user_id,\n" +
                "       price,\n" +
                "       name,\n" +
                "       safe_payment_flag,\n" +
                "        CASE\n" +
                "            WHEN w.status ='Y' THEN 'Y'\n" +
                "            ELSE 'N'\n" +
                "        END as wish,\n" +
                "        pi.url as image\n" +
                "FROM product p\n" +
                "         LEFT JOIN (SELECT product_id, status\n" +
                "                    FROM wish\n" +
                "                    WHERE user_id = ?) w on p.product_id = w.product_id\n" +
                "         LEFT JOIN (SELECT url, MIN(product_image_id), product_id\n" +
                "               FROM product_image\n" +
                "               GROUP BY product_id) pi on p.product_id = pi.product_id\n" +
                "WHERE p.user_id = ? AND p.status!='D'" +
                "order by p.product_id DESC\n";

        Object[] params = new Object[]{userId, storeId};
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetStoreProductRes(
                        rs.getInt("product_id"),
                        rs.getInt("user_id"),
                        rs.getInt("price"),
                        rs.getString("image"),
                        rs.getString("name"),
                        rs.getString("safe_payment_flag"),
                        rs.getString("wish")),
                params);
    }
}
