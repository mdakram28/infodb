package com.mdakram28.infodb;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication(scanBasePackages = { "com.mdakram28.infodb" })
@EnableAsync
public class InfodbApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfodbApplication.class, args);
	}
}
