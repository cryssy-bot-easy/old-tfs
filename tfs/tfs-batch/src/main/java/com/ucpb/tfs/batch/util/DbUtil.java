package com.ucpb.tfs.batch.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 */
public final class DbUtil {

    public static final void closeQuietly(Connection connection){
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                //ignore
            }
        }
    }


}
