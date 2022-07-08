package com.hgz.file.config.openapi;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author CunTouGou
 */

@Configuration
public class OpenApiConfig implements WebMvcConfigurer{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean(value = "openApi")
    public Docket openApi() {
        Contact contact = new Contact("CunTouGou", "www.cuntougou.cn", "1594872540@qq.com");
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("狗洞同学 RESTful APIs")
                        .description("个人毕设——基于SpringBoot框架开发的Web文件系统")
                        .contact(contact)
                        .version("1.0")
                        .build())
                //分组名称
                .groupName("前端接口分组")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.hgz.file.controller"))
                .paths(PathSelectors.any())
                .build();
    }

}
