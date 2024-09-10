package com.zerozone.vintage.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
public class IndexInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void createIndexIfNotExists() {
        try {
            // 인덱스 존재 여부 확인 쿼리
            String checkIndexQuery = "SELECT to_regclass('public.idx_meeting_fulltext')";
            String indexExists = jdbcTemplate.queryForObject(checkIndexQuery, String.class);

            if (indexExists == null) {
                // 인덱스 생성 쿼리
                String createIndexQuery = "CREATE INDEX idx_meeting_fulltext ON meeting USING GIN (to_tsvector('simple', title || ' ' || description))";
                jdbcTemplate.execute(createIndexQuery);
                System.out.println("Index created: idx_meeting_fulltext");
            } else {
                System.out.println("Index already exists: idx_meeting_fulltext");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
