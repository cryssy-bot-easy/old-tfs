CREATE FUNCTION DAYOFYEAR(TIMESTAMP) RETURNS DECIMAL(6) PARAMETER STYLE JAVA NO SQL LANGUAGE JAVA EXTERNAL NAME 'com.ucpb.tfs.batch.sql.DbUtils.dayOfYear'