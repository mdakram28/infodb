package com.mdakram28.infodb.tools.ke;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joestelmach.natty.Parser;
import com.mdakram28.infodb.InfoList;
import com.mdakram28.infodb.datamanager.IKnowledgeEngine;
import com.mdakram28.infodb.tools.scraper.WikipediaScraper;

@Component
public class PersonExtractor implements IKnowledgeEngine {

	private static final Pattern REGEX_AGE = Pattern.compile("/age\\D+(\\d+)/i");

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Object process(InfoList infoList) {
		ObjectMapper m = new ObjectMapper();
		Person ret = new Person();
		infoList.getData().forEach(info -> {
			if (!info.getToolName().equals("Wikipedia Scraper"))
				return;
			Map<String, Object> raw = info.getRaw();
			if (raw.get("Personal Information") != null) {
				try {
					Map<String, Object> po = m.convertValue(raw.get("Personal Information"), Map.class);
					raw.putAll(po);
				} catch (Exception e) {
				}
			} else if (raw.get("Personal details") != null) {
				try {
					Map<String, Object> po = m.convertValue(raw.get("Personal details"), Map.class);
					raw.putAll(po);
				} catch (Exception e) {
				}
			}
			// logger.info(raw.toString());
			try {
				ret.setPhoto(raw.get("image").toString());
			} catch (Exception e) {
			}
			try {
				ret.setDateOfBirth(parseDate(raw.get("Born").toString()));
			} catch (Exception e) {
			}
			try {
				ret.setAge(getAge(raw.get("Born").toString()));
			} catch (Exception e) {
			}
			try {
				if (ret.getAge() == -1) {
					ret.setAge(getAge(raw.get("Died").toString()));
					ret.setDateOfDeath(parseDate(raw.get("Died").toString()));
				}
			} catch (Exception e) {
			}
			ret.setName(info.getTitle());
			try {
				ret.setWebsite(raw.get("Website").toString());
			} catch (Exception e) {
			}

			try {
				ret.setParents(getList(raw, "Parents"));
			} catch (Exception e) {
			}
			try {
				ret.setChildren(getList(raw, "Children"));
			} catch (Exception e) {
			}
			try {
				ret.setSpouses(getList(raw, "Spouse(s)"));
			} catch (Exception e) {
			}
			try {
				ret.setKnownFor(getList(raw, "Known for"));
			} catch (Exception e) {
			}

		});
		return ret;
	}

	public Date parseDate(String d) {
		return new Parser().parse(d).get(0).getDates().get(0);
	}

	public List<String> getList(Map<String, Object> raw, String property) {
		return Arrays.asList(raw.get(property).toString().split("\n"));
	}

	public int getAge(String line) {
		Matcher m = REGEX_AGE.matcher(line);
		if (m.find()) {
			return Integer.parseInt(m.group(1));
		}
		return -1;
	}

}
