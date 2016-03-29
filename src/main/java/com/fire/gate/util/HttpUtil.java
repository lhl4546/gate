/**
 * 
 */
package com.fire.gate.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.fire.gate.Component;

/**
 * 基于Apache http client连接池实现的HTTP GET\POST方法
 * 
 * @author lhl
 *
 *         2015年12月30日 下午2:41:08
 */
final public class HttpUtil implements Component
{
    private PoolingHttpClientConnectionManager clientPool;
    private ResponseHandler<String> stringResponseHandler;

    private HttpUtil() {
        initialize();
    }

    private void initialize() {
        clientPool = new PoolingHttpClientConnectionManager();
        stringResponseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity == null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
    }

    public static HttpUtil INSTANCE = new HttpUtil();

    @Override
    public void start() throws Exception {
        clientPool.setMaxTotal(200);
        clientPool.setDefaultMaxPerRoute(200);
        SocketConfig socketConfig = SocketConfig.custom().setSoLinger(0).setTcpNoDelay(true).build();
        clientPool.setDefaultSocketConfig(socketConfig);
    }

    @Override
    public void stop() throws Exception {
        clientPool.shutdown();
    }

    /**
     * 
     * @param url
     * @return
     * @throws IOException
     */
    public static String GET(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createMinimal(INSTANCE.clientPool);
        HttpGet httpGet = new HttpGet(url);
        return httpClient.execute(httpGet, INSTANCE.stringResponseHandler);
    }

    /**
     * 
     * @param url
     * @param kvPairs
     * @return
     * @throws IOException
     */
    public static String POST(String url, String kvPairs) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createMinimal(INSTANCE.clientPool);
        HttpPost httpPost = new HttpPost(url);
        HttpEntity entity = new StringEntity(kvPairs);
        httpPost.setEntity(entity);
        return httpClient.execute(httpPost, INSTANCE.stringResponseHandler);
    }
}
