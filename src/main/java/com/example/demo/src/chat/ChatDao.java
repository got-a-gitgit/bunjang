package com.example.demo.src.chat;

import com.example.demo.src.chat.model.PostSendMessageReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ChatDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public int checkChatroom(int userId, int productId) {
        String query = "SELECT c.room_id as room_id\n" +
                "FROM chatroom c\n" +
                "JOIN user_chatroom uc on c.room_id = uc.room_id\n" +
                "WHERE uc.user_id=? AND c.product_id=?";

        Object[] params = new Object[]{userId, productId};

        try {
            return jdbcTemplate.queryForObject(query, Integer.class, params);
        } catch (Exception exception) {
            return -1;
        }
    }

    public int sendMessage(int userId, int chatroomId, PostSendMessageReq postSendMessageReq) {
        String query = "INSERT INTO chat_message(room_id, user_id, message, message_type) VALUES(?,?,?,?)";
        Object[] params = new Object[]{chatroomId, userId, postSendMessageReq.getMessage(), postSendMessageReq.getMessageType()};

        return jdbcTemplate.update(query, params);
    }

    public int createChatroom(int productId) {
        String query = "INSERT INTO chatroom(product_id) VALUE (?)";
        jdbcTemplate.update(query, productId);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public void joinChatroom(int userId, int chatroomId) {
        String query = "INSERT INTO user_chatroom(user_id, room_id) VALUES (?,?)";
        Object[] params = new Object[]{userId, chatroomId};

        jdbcTemplate.update(query, params);
    }

    public void activateChatroom(int chatroomId) {
        String query ="UPDATE user_chatroom SET status='Y' WHERE room_id=?";
        jdbcTemplate.update(query, chatroomId);
    }

    public void leaveChatroom(int userId, int chatroomId) {
        String query ="UPDATE user_chatroom SET status='N' WHERE user_id=? AND room_id=?";
        Object[] params = new Object[]{userId, chatroomId};
        jdbcTemplate.update(query, params);
    }
}
