package me.kr1s_d.ultimateantibot.common.objects.connectioncheck.proxycheck.query;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HTTPQuery {
    private final CloseableHttpClient httpClient = HttpClients.createDefault();


    private void close() throws IOException {
        this.httpClient.close();
    }

    public String sendGet(String url) {
        HttpGet get = new HttpGet(url);
        try (CloseableHttpResponse response = this.httpClient.execute(get)) {
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                return EntityUtils.toString(entity);
            }
            close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String sendPOST(String url, List<NameValuePair> postData) {
        HttpPost post = new HttpPost(url);

        List<NameValuePair> postParams = new ArrayList<>(postData);
        postParams.add(new BasicNameValuePair("User-Agent", "ProxyCheck-IO-Java-API"));
        postParams.add(new BasicNameValuePair("Accept-Encoding", "UTF-8"));
        postParams.add(new BasicNameValuePair("Content-Type", "application/json"));

        try {
            post.setEntity(new UrlEncodedFormEntity(postParams));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try (CloseableHttpResponse response = this.httpClient.execute(post)) {
            String responseEntity = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().toString().contains("200")) {
                return responseEntity;
            }
            throw new Exception("Error getting result from API: ResponseCode: " + response.getStatusLine());
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
