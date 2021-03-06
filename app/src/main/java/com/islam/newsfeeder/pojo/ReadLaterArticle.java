package com.islam.newsfeeder.pojo;

import com.google.gson.annotations.SerializedName;

public class ReadLaterArticle {

    @SerializedName("resolved_title")
    private String title;
    @SerializedName("top_image_url")
    private String imageUrl;
    @SerializedName("excerpt")
    private String description;
    @SerializedName("resolved_url")
    private String articleUrl;
    @SerializedName("given_title")
    private String givenTitle;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getGivenTitle() {
        return givenTitle;
    }

    public void setGivenTitle(String givenTitle) {
        this.givenTitle = givenTitle;
    }
}
