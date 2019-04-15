package com.xc.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.ignite.Ignition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.xc.exception.GlobalExceptionHandler;
import com.xc.service.user.UserService;
import com.xc.util.LoginUserHolder;

@EnableDubbo
//@EnableAutoConfiguration
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients({ "com.xc.admin.feign" })
@EnableDiscoveryClient
@Configuration
public class XcAdminBootstrap implements WebMvcConfigurer,ErrorPageRegistrar   {


	
	
	public static void main(String[] args) {
//		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//		context.register(XcServiceBootstrap.class);
//		context.refresh();
//		  new SpringApplicationBuilder(XcServiceBootstrap.class).run(args);
//		 SpringApplication springApplication = new SpringApplication(XcAdminBootstrap.class);
//	        springApplication.addListeners(new LoginUserHolder());
//	        springApplication.run(args);
		Ignition.start("applicationContext-ignite.xml");
		ConfigurableApplicationContext context = SpringApplication.run(XcAdminBootstrap.class, args);
	/*	
		 String[] beans = context.getBeanDefinitionNames();

	        for (String bean : beans)

	        {

	            System.out.println(bean + " of Type :: " + context.getBean(bean).getClass());

	        }
	        */
		System.out.println("XcAdminBootstrap provider is starting...");
	}

	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters() {
		// 1.需要定义一个convert转换消息的对象;
		FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
		// 2.添加fastJson的配置信息，比如：是否要格式化返回的json数据;
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
				SerializerFeature.DisableCircularReferenceDetect);
		fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
		// 3处理中文乱码问题
		List<MediaType> fastMediaTypes = new ArrayList<>();
		fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		// 4.在convert中添加配置信息.
		fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
		fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
		HttpMessageConverter<?> converter = fastJsonHttpMessageConverter;
		return new HttpMessageConverters(converter);
	}

	@Bean
	public GlobalExceptionHandler getGlobalExceptionHandler() {
		return new GlobalExceptionHandler();
	}

	@Override
	public void registerErrorPages(ErrorPageRegistry registry) {
		registry.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR,"/commomError"),new ErrorPage(HttpStatus.NOT_FOUND,"/404"));
	}

}
