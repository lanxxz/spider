package com.alien.spider.domain;

/**
 * 评论类
 *
 * @author Alien
 * @since 2019/4/21 20:25
 */
public class Comment {

    /**
     * 用户名
     */
    private String nickname;

    /**
     * 评论内容
     */
    private String content;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "用户" + nickname + "的评论为： " + content ;
    }

    @Override
    public int hashCode() {
        return this.nickname.hashCode() + this.content.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Comment comment = (Comment) obj;
        return this.nickname.equals(comment.getNickname()) &&
                this.content.equals(comment.getContent());
    }
}
