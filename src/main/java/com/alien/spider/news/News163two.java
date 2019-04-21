package com.alien.spider.news;

import com.alien.spider.connect.HttpConnect;
import com.alien.spider.connect.HttpType;
import com.alien.spider.domain.Comment;
import com.alien.spider.domain.HttpResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

/**
 * 爬取 163 新闻数据
 *
 * @author Alien
 * @since 2019/4/21 19:31
 */
public class News163two {

    /**
     * 评论 url 的模板
     */
    private StringBuilder sb = new StringBuilder();
    /**
     * 存放评论内容
     */
    private Set<Comment> comments = new HashSet<>(134);
    /**
     * 评论总条数
     */
    private int newListSize;
    /**
     * 爬取开始的条数
     */
    private int startNum;

    public static void main(String[] args) {
        String url = "https://news.163.com/19/0421/08/ED9AODCP0001875N.html";
        int index = url.lastIndexOf("/");
        String param = url.substring(index).split("\\.")[0];
        System.out.println(param);

        News163two news163 = new News163two();
        String commentUrl = news163.getUrl(param);

        HttpResult httpResult = news163.getData(commentUrl);

        if (httpResult != null && httpResult.getCode() == 200) {
            news163.parse(httpResult.getContent());
            news163.save();
        }

    }

    private String getUrl(String param) {
        sb.append("http://comment.api.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads")
                .append(param)
                .append("/comments/newList?ibc=newspc&limit=30&showLevelThreshold=72&headLimit=1&tailLimit=2");
        StringBuilder sbTmp = new StringBuilder();
        long timeMillis = System.currentTimeMillis();
        long timeMillisNext = timeMillis + 1L;
        sbTmp.append(sb).append("&offset=").append(startNum)
                .append("&callback=jsonp_")
                .append(timeMillis).append("&_=").append(timeMillisNext);
        return sbTmp.toString();
    }

    /**
     * 获取下一页的 URL
     * @return
     */
    private String getNextUrl() {
        StringBuilder sbTmp = new StringBuilder();
        long timeMillis = System.currentTimeMillis();
        long timeMillisNext = timeMillis + 1L;
        sbTmp.append(sb).append("&offset=").append(startNum)
                .append("&callback=jsonp_")
                .append(timeMillis).append("&_=").append(timeMillisNext);
        return sbTmp.toString();
    }

    /**
     * 请求结果
     * @param url
     * @return
     */
    private HttpResult getData(String url) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Connection", "keep-alive");
        headers.put("Content-Type", "application/json;charset=UTF-8");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");

        HttpResult httpResult = null;
        try {
            httpResult = new HttpConnect(HttpType.HTTP).doGet(url, headers);
            System.out.println(httpResult.toString());
            return httpResult;
        } catch (Exception e) {
            e.printStackTrace();
            return httpResult;
        }
    }

    /**
     * 处理数据
     * @param content
     */
    private void parse(String content) {
        int startIndex = content.indexOf("{");
        int endIndex = content.lastIndexOf("}");
        content = content.substring(startIndex, endIndex + 1);

        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(content, JsonObject.class);

        newListSize = obj.get("newListSize").getAsInt();
//        System.out.println(newListSize);

        Set<Map.Entry<String, JsonElement>> entrys = obj.get("comments").getAsJsonObject().entrySet();
        for (Map.Entry<String, JsonElement> entry: entrys) {
            JsonObject jsonObject = entry.getValue().getAsJsonObject();
            Comment comment = new Comment();
            comment.setContent(jsonObject.get("content").getAsString());
            JsonElement jsonElement = jsonObject.get("user").getAsJsonObject().get("nickname");
            String nickName = jsonElement == null ? "" : jsonElement.getAsString();
            comment.setNickname( nickName);
            comments.add(comment);
        }

        //获取下一页数据并解析
        if (startNum < newListSize) {
            startNum += 30;
            //10s 内随机休眠
            Random random = new Random();
            long sleep = random.nextInt(5000);
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String nextUrl = getNextUrl();
            HttpResult httpResult = getData(nextUrl);
            if (httpResult != null && httpResult.getCode() == 200) {
                parse(httpResult.getContent());
            }
        }

    }

    /**
     * 保存数据
     */
    private void save() {
        String property = System.getProperty("user.dir");
        String dirName = property + File.separator + "data";
        File dir = new File(dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = dirName + File.separator + System.currentTimeMillis() + ".txt";
        try (BufferedWriter outputStream = new BufferedWriter(
                new OutputStreamWriter (new FileOutputStream(fileName), "UTF-8"))) {
            File file = new File(fileName);
            if (!file.exists()) {
                    file.createNewFile();
            }

            for (Comment comment: comments) {
                StringBuilder data = new StringBuilder();
                data.append("用户名:").append(comment.getNickname())
                        .append(" 评论:").append(comment.getContent()).append("\n");
                outputStream.write(data.toString());
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
