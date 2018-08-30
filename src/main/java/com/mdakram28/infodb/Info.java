package com.mdakram28.infodb;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.TextScore;

import com.mdakram28.infodb.datamanager.IDataSource;

public class Info {
	
	public String source;
	public String title;
	public String toolName;
	public String toolType;
	public Map<String, Object> raw;
	public List<String> keywords;
	
	@TextScore Float score;
	
	public Info() {}
	
	public Info(IDataSource dataSource) {
		super();
		this.toolName = dataSource.getName();
		this.toolType = dataSource.getType();
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public String getToolType() {
		return toolType;
	}

	public void setToolType(String toolType) {
		this.toolType = toolType;
	}

	public Map<String, Object> getRaw() {
		return raw;
	}

	public void setRaw(Map<String, Object> raw) {
		this.raw = raw;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
}
