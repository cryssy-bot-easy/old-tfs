CREATE FUNCTION TO_DATE(dateString VARCHAR(6),format VARCHAR(6)) RETURNS TIMESTAMP PARAMETER STYLE JAVA NO SQL LANGUAGE JAVA EXTERNAL NAME 'com.ucpb.tfs.interfaces.sql.DbUtils.to_date'