package com.alien.spider.connect;

import com.alien.spider.domain.HttpResult;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;

/**
 * Http 请求模拟类
 *
 * @author Alien
 * @since 2019/4/21 17:02
 */
public class HttpConnect {

    private HttpType type;

    public HttpConnect(HttpType type) {
        this.type = type;
    }

    public HttpResult doGet(String url, Map<String, String> headers) throws URISyntaxException, IOException {
        //创建 httpClient 对象
        CloseableHttpClient httpClient = null;
        if (type == HttpType.HTTP) {
            httpClient = HttpClients.createDefault();
        } else if (type == HttpType.HTTPS) {
            httpClient = sslClient();
        }

        if (httpClient == null) {
            throw new NullPointerException("httpClient create failed.");
        }

        //创建访问的地址
        URIBuilder uriBuilder = new URIBuilder(url);

        //创建 http 对象
        HttpGet httpGet = new HttpGet(uriBuilder.build());

        //设置超时
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(HttpContants.CONNECT_TIMEOUT)
                .setSocketTimeout(HttpContants.SOCKET_TIMEOUT).build();
        httpGet.setConfig(requestConfig);

        //设置请求头
//        httpGet.setHeader("Content-Type", "text/html; charset=utf-8");
//        httpGet.setHeader("Connection", "keep-alive");
//        httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
//        httpGet.setHeader("User-Agent",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
        for (Map.Entry<String, String> header: headers.entrySet()) {
            httpGet.setHeader(header.getKey(), header.getValue());
        }
        //执行请求
        CloseableHttpResponse response = httpClient.execute(httpGet);

        //处理结果
        if (response != null && response.getStatusLine() != null) {
            String content = "";
            if (response.getEntity() != null ) {
                content = EntityUtils.toString(response.getEntity(), HttpContants.ENCODING);
            }
            return new HttpResult(response.getStatusLine().getStatusCode(), content);
        }
        return new HttpResult(HttpStatus.SC_INTERNAL_SERVER_ERROR);

    }

    private CloseableHttpClient sslClient() {
        CloseableHttpClient closeableHttpClient = null;
        try {
            X509TrustManager trustManager = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            SSLContext ctx = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            ctx.init(null, new TrustManager[] {trustManager}, null);
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
            // 创建Registry
            RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT)
                    .setExpectContinueEnabled(Boolean.TRUE).setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM,AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https",socketFactory).build();
            // 创建ConnectionManager，添加Connection配置信息
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            closeableHttpClient = HttpClients.custom().setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(requestConfig).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } finally {
            return closeableHttpClient;
        }

    }

}
