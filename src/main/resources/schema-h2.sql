-- schema-h2.sql 파일
CREATE INDEX IF NOT EXISTS idx_meeting_fulltext ON meeting (title);
