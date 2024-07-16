package com.ucpb.tfs.util;

import com.ucpb.tfs.utils.DbUtil;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

/**
 */
public class DbUtilTest {


    @Test
    public void invokeCloseOnNonNullConnection() throws SQLException {
        Connection connection = mock(Connection.class);
        DbUtil.closeQuietly(connection);
        verify(connection).close();
    }

}
