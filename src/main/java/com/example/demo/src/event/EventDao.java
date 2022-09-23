package com.example.demo.src.event;

import com.example.demo.src.event.model.GetEventListRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class EventDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /** 홈 화면 이벤트 목록 조회 **/
    public List<GetEventListRes> getEventList() {
        String query = "SELECT event_id, event_image, url\n" +
                "FROM event\n" +
                "WHERE status = 'Y'";
        return jdbcTemplate.query(query,
                (rs,rowNum)->new GetEventListRes(
                        rs.getInt("event_id"),
                        rs.getString("event_image"),
                        rs.getString("url")
                ));
    }
}
