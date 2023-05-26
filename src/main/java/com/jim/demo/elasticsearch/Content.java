package com.jim.demo.elasticsearch;

public class Content {

	private String content;

	public Content() {
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "{" +
				"content='" + content + '\'' +
				'}';
	}
}
