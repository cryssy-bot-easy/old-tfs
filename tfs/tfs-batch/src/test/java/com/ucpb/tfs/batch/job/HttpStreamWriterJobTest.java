package com.ucpb.tfs.batch.job;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class HttpStreamWriterJobTest {

    private HttpStreamWriterJob job;

    private MessageChannel channel;

    private HttpClient client;
    private HttpResponse response;
    private HttpEntity entity;
    private InputStream is;

    @Before
    public void setup() throws IOException {
        channel = mock(MessageChannel.class);
        client = mock(HttpClient.class);
        response = mock(HttpResponse.class);
        entity = mock(HttpEntity.class);
        is = mock(InputStream.class);

        when(client.execute(any(HttpGet.class))).thenReturn(response);
        when(response.getEntity()).thenReturn(entity);
        when(entity.getContent()).thenReturn(is);

        job = new HttpStreamWriterJob(channel,"'TEMP.txt'");
        job.setClient(client);
    }

    @Before
    @After
    public void cleanup(){
        File tempFile = new File("TEMP.txt");
        tempFile.delete();
    }

    @Test
    public void sendMessageToChannel() throws IOException {
        when(is.read(any(byte[].class))).thenReturn(12, 12, -1);

        job.setUrl("http://www.google.com");
        job.execute();
        verify(channel).send(any(Message.class));
        assertTrue(new File("TEMP.txt").exists());
        verify(is).close();
    }
}
