package com.ucpb.tfs.batch.util;

import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 */
public class IOUtilTest {


    @Test
    public void invokeCloseOnNonNullIO() throws IOException {
        Closeable io = mock(Closeable.class);
        IOUtil.closeQuietly(io);
        verify(io).close();
    }
}
