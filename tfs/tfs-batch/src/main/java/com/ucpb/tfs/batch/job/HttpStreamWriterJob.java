package com.ucpb.tfs.batch.job;

import com.ucpb.tfs.batch.util.IOUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.util.Assert;

import java.io.*;

/**
 */
public class HttpStreamWriterJob implements SpringJob {

    private HttpClient client;
    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));
    private static final String DEFAULT_FILENAME = "temp";

    private MessageChannel channel;
    private String url;
    private String fileName;



    public HttpStreamWriterJob(MessageChannel channel){
        this.channel = channel;
    }

    public HttpStreamWriterJob(MessageChannel channel,String fileNameExpression){
        this(channel);
        this.fileName = (String) parser.parseExpression(fileNameExpression).getValue(new StandardEvaluationContext(this));
    }


    @Override
    public void execute() {
        Assert.notNull(url,"URL must not be null");

        File tempFile = null;
        FileOutputStream writer = null;
        InputStream inputStream = null;
        try {
            HttpResponse response = client.execute(new HttpGet(url));
            HttpEntity entity = response.getEntity();

            if(entity != null){
               tempFile = new File(getFileName());
               writer = new FileOutputStream(tempFile);
               inputStream = entity.getContent();
               IOUtil.copyTo(inputStream, writer);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to produce file output",e);
        }finally{
            IOUtil.closeQuietly(inputStream);
            IOUtil.closeQuietly(writer);
        }

        sendToChannel(tempFile);
    }

    @Override
    public void execute(String reporDate) {
        //TODO
        Assert.notNull(url,"URL must not be null");

        File tempFile = null;
        FileOutputStream writer = null;
        InputStream inputStream = null;
        try {
            HttpResponse response = client.execute(new HttpGet(url));
            HttpEntity entity = response.getEntity();

            if(entity != null){
                tempFile = new File(getFileName());
                writer = new FileOutputStream(tempFile);
                inputStream = entity.getContent();
                IOUtil.copyTo(inputStream, writer);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to produce file output",e);
        }finally{
            IOUtil.closeQuietly(inputStream);
            IOUtil.closeQuietly(writer);
        }

        sendToChannel(tempFile);
    }

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName != null ? fileName : DEFAULT_FILENAME;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private void sendToChannel(File file){
        MessageBuilder<File> builder = MessageBuilder.withPayload(file);
        channel.send(builder.build());
    }

    public void setClient(HttpClient client) {
        this.client = client;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
