package com.pangbohao.server.db;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URL;

public class BeanProvider {
	private static BeanProvider INSTANCE;
	private ClassPathXmlApplicationContext context;

	private BeanProvider() {
		URL url = this.getClass().getClassLoader()
				.getResource("SpringContext.xml");
		context = new ClassPathXmlApplicationContext(url.toString());

	}

	public static void Init() {
		INSTANCE = new BeanProvider();
	}

	public static BeanProvider instance() {
		return INSTANCE;
	}

	public Object getRegistrationBean(String beanName) {
		return context.getBean(beanName);
	}
}
