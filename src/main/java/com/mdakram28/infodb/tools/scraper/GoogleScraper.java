package com.mdakram28.infodb.tools.scraper;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.mdakram28.infodb.Info;
import com.mdakram28.infodb.InfoList;
import com.mdakram28.infodb.datamanager.IDataSource;

import edu.emory.mathcs.backport.java.util.Arrays;

@Component
@Order(value = 3)
public class GoogleScraper implements IDataSource {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

	@Override
	public String getType() {
		return TYPE.SCRAPER;
	}

	@Override
	public List<Info> fetchInfo(String key) throws IOException, ScrapingException {
		URL local = new URL("http://localhost:9515");
		
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--log-level=1");
		chromeOptions.addArguments("--silent");
		
		WebDriver driver = new RemoteWebDriver(local, chromeOptions);
		Info info = new Info(this);
		// open the browser and go to open google.com
		try {
			driver.get("https://www.google.co.in");
			WebElement element = driver.findElement(By.name("q"));
			element.sendKeys(key);
			element.submit();
			 WebElement myDynamicElement = (new WebDriverWait(driver, 10))
		              .until(ExpectedConditions.presenceOfElementLocated(By.id("resultStats")));

			info.setSource(driver.getCurrentUrl());
			String title = driver.findElement(By.cssSelector(".kno-ecr-pt.kno-fb-ctx")).getText();
			info.setTitle(title);
			Map<String, Object> raw = new HashMap<>();
			scrapePage(driver, raw);
			if (raw.size() == 0)
				throw new ScrapingException("Empty raw data");
			info.setRaw(raw);
		} catch (Exception e) {
			driver.quit();
			throw e;
		}
		driver.quit();

		return Arrays.asList(new Info[]{info});
	}

	public void scrapePage(WebDriver driver, Map<String, Object> info) {
		String summary = driver.findElement(By.cssSelector(".kno-rdesc span")).getText();
		info.put("summary", summary);
		try {
			driver.findElements(By.cssSelector(".zloOqf.kno-fb-ctx")).forEach(element -> {
				try {
					String key = element.findElement(By.cssSelector(".w8qArf .fl")).getText();
					String value = element.findElement(By.cssSelector(".LrzXr.kno-fv")).getText();
					info.put(key, value);
				} catch (Exception e) {
				}
			});
		} catch (Exception e) {
		}
	}

	@Override
	public String getName() {
		return "Google Scraper";
	}

}
