package com.unbank.entity;

public class News {

	public int id;
	public String title;
	public String content;
	public Long time;
	public String url;
	public Long crawlTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getCrawlTime() {
		return crawlTime;
	}

	public void setCrawlTime(Long crawlTime) {
		this.crawlTime = crawlTime;
	}

}
