package com.ucpb.tfs.interfaces.util;

import com.ucpb.tfs.interfaces.sql.DbUtils;
import org.apache.ibatis.mapping.DatabaseIdProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

/**
 */
public class DefaultDatabaseIdProvider implements DatabaseIdProvider{

    private Properties properties;

    @Override
    public void setProperties(Properties p) {
        this.properties = p;
    }

    @Override
    public String getDatabaseId(DataSource dataSource) throws SQLException {
        Connection con = null;
        String productName;
        try{
            con = dataSource.getConnection();
            DatabaseMetaData meta = con.getMetaData();
            productName = meta.getDatabaseProductName();
        }finally{
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){
                    //do nothing
                }
            }
        }

        return productName;
    }
}
