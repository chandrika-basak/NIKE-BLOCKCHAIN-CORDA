package com.nike.wms;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This class is the entry point for Nike application 
 * @author Cognizant Blockchain Team
 * @version 1.0
 */

@SpringBootApplication
@EnableScheduling
@EnableCaching
@ComponentScan("com.nike.wms")
public class Application extends SpringBootServletInitializer {

	public static void main(final String[] args) {
		new Application()
		.configure(new SpringApplicationBuilder(Application.class))
		.run(args);
	}
}
