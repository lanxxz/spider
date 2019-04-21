package com.alien.spider;

import com.alien.spider.connect.HttpConnect;
import com.alien.spider.connect.HttpType;
import com.alien.spider.domain.HttpResult;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 163 新闻评论 测试
 *
 * @author Alien
 * @since 2019/4/21 18:02
 */
public class News163Test {

    /**
     * 测试 163 新闻页面的 http 连接
     */
    @Test
    public void TestHttp163() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Connection", "keep-alive");
        headers.put("Content-Type", "application/json;charset=UTF-8");
        headers.put("User-Agent",
               "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");

        String url = "http://comment.api.163.com/api/v1/products/" +
                "a2869674571f77b5a0867c3d71db5856/threads/ED97SL1E0001899O/comments/" +
                "hotList?ibc=newspc&limit=5&showLevelThreshold=72&headLimit=1&tailLimit=2&" +
                "offset=0&callback=jsonp_1555835533560&_=1555835533561";
        try {
            HttpResult httpResult = new HttpConnect(HttpType.HTTP).doGet(url, headers);
            System.out.println(httpResult.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试 163 新闻页面的 https 连接
     */
    @Test
    public void TestHttps163() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "text/html; charset=utf-8");
        headers.put("Connection", "keep-alive");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");

        String url = "https://news.163.com/19/0421/08/ED97SL1E0001899O.html";
        try {
            HttpResult httpResult = new HttpConnect(HttpType.HTTPS).doGet(url, headers);
            System.out.println(httpResult.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {
        System.out.println(System.currentTimeMillis());
    }
}
