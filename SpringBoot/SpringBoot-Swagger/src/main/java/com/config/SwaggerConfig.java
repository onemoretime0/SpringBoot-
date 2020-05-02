package com.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.print.Doc;
import java.util.ArrayList;

import static springfox.documentation.service.ApiInfo.DEFAULT_CONTACT;

@Configuration
@EnableSwagger2     //开启Swagger2
public class SwaggerConfig {

    @Bean
    public Docket docketHnl(){
        return  new Docket(DocumentationType.SWAGGER_2).groupName("hnl");
    }
    @Bean
    public Docket docketZhangSan(){
        return new Docket(DocumentationType.SWAGGER_2).groupName("Zhangsan");
    }

    //配置了Swagger2的Docket的bean实例
    @Bean
    public Docket docket(Environment environment){
        //设置要显示的Swagger环境，dev或test则Swagger开启，其他则不开启
        Profiles profiles=Profiles.of("dev","test");
        //通过environment.acceptsProfiles(profiles)判断是否处在自己设置的环境当中
        boolean flag = environment.acceptsProfiles(profiles);

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("Hello")//Api文档分组
                .enable(flag) //配置Swagger是否启动
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .build()
                ;
    }

    //配置Swagger文档信息===>ApiInfo
    public ApiInfo apiInfo(){
        //作者信息
        Contact contact = new Contact("hnl","","huprivatmail@gmail.com");
        return new ApiInfo(
                "hnl-Swagger-API文档",  //对应的就是Swagger-UI页面中的标题
                "Swagger学习文档",  //描述
                "1.0",  //版本
                "urn:tos",  //
                contact,
                "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<VendorExtension>());
    }


}
