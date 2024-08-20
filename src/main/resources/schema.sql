 -- 모임 게시글 검색 기능에 대한 부분 패턴 일치에 대한 인덱싱 처리
 DO $$
 BEGIN
   IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'idx_meeting_fulltext') THEN
 CREATE INDEX idx_meeting_fulltext ON meeting USING GIN (to_tsvector('simple', title || ' ' || description));
 END IF;
 END $$;
