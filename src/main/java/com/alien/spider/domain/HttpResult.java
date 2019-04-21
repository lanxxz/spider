package com.alien.spider.domain;

/**
 * HTTP 请求返回结果
 *
 * @author Alien
 * @since 2019/4/21 17:22
 */
public class HttpResult {

    /**
     * 状态码
     */
    private int code;

    /**
     * 响应数据
     */
    private String content;

    public HttpResult() { }

    public HttpResult(int code) {
        this.code = code;
    }

    public HttpResult(String content) {
        this.content = content;
    }

    public HttpResult(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "HttpResult{" + "\n" +
                "code=" + code + "\n" +
                ", content='" + content + '\'' + "\n" +
                '}';
    }
}
