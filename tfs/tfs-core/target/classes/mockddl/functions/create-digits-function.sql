DROP FUNCTION DIGITS;
CREATE FUNCTION DIGITS(numberToPad DECIMAL(6)) RETURNS VARCHAR(6) PARAMETER STYLE JAVA NO SQL LANGUAGE JAVA EXTERNAL NAME 'com.ucpb.tfs.interfaces.sql.DbUtils.digits';