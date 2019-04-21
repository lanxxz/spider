package com.alien.spider.news;

import com.alien.spider.connect.HttpConnect;
import com.alien.spider.connect.HttpType;
import com.alien.spider.domain.HttpResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 爬取 163 新闻数据
 *
 * @author Alien
 * @since 2019/4/21 17:05
 */
public class News163 {

    public static void main(String[] args) {
        String url = "https://news.163.com/19/0421/08/ED97SL1E0001899O.html";
        News163 news163 = new News163();
        HttpResult htmlHttpResult = news163.getHtml(url);
        if (htmlHttpResult != null && htmlHttpResult.getCode() == 200) {
            String commentUrl = news163.getCommentUrl(htmlHttpResult.getContent());
            System.out.println(commentUrl);
        }
    }

    /**
     * 获取评论数据的url
     * @param content
     * @return
     */
    private String getCommentUrl(String content) {
        System.out.println(content);
        content = content.replaceAll("\"", "'");
        Document doc = Jsoup.parse(content);
        Elements divElements = doc.getElementsByClass("post_comment_joincount");
        Elements aElements = divElements.tagName("a");
        String url = "";
        for (Element element: aElements) {
            //获取不到 url
            url = element.attr("href");
        }
        return url;
    }

    /**
     * 获取url对应的 html 页面
     * @param url
     * @return
     */
    private HttpResult getHtml(String url) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "text/html; charset=utf-8");
        headers.put("Connection", "keep-alive");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");

        HttpResult httpResult = null;
        try {
            httpResult = new HttpConnect(HttpType.HTTPS).doGet(url, headers);
            System.out.println("http code:" + httpResult.getCode());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return httpResult;
        }
    }

}
