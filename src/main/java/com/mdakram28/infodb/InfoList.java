package com.mdakram28.infodb;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InfoList {

	@JsonProperty(value = "key")
	private String key;

	@JsonProperty(value = "data")
	private List<Info> data;
	
	@JsonProperty(value = "info")
	private List<Object> knowledge;

	public InfoList(String key) {
		this.key = key;
		this.data = new ArrayList<>();
		this.knowledge = new ArrayList<>();
	}

	@JsonIgnore
	public Info addInfo(Info info) {
		data.add(info);
		return info;
	}

	@JsonIgnore
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@JsonIgnore
	public int size() {
		return data.size();
	}
	
	public void addKnowledge(Object obj) {
		knowledge.add(obj);
	}

	public List<Info> getData() {
		return data;
	}

	public void setData(List<Info> data) {
		this.data = data;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<Object> getKnowledge() {
		return knowledge;
	}

	public void setKnowledge(List<Object> knowledge) {
		this.knowledge = knowledge;
	}
}
