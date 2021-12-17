package com.yunqiic.cocojob.server.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import com.yunqiic.cocojob.server.common.CocoJobServerConfigKey;
import com.yunqiic.cocojob.server.remote.server.ServerInfoService;

import javax.annotation.Resource;

import static springfox.documentation.builders.PathSelectors.any;

/**
 * Configuration class for Swagger UI.
 *
 * @author zhangchunsheng
 * @author Jiang Jining
 * @since 2021-12-02
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(name = CocoJobServerConfigKey.SWAGGER_UI_ENABLE, havingValue = "true")
public class SwaggerConfig {
    
    @Resource
    private ServerInfoService serverInfoService;
    
    @Bean
    public Docket createRestApi() {

        // apiInfo()用来创建该Api的基本信息（这些基本信息会展现在文档页面中
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("CocoJob")
                .description("Distributed scheduling and computing framework.")
                .license("Apache Licence 2")
                .termsOfServiceUrl("https://github.com/coco-gene/CocoJob")
                .version(serverInfoService.getServerVersion())
                .build();
        
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                // select()函数返回一个ApiSelectorBuilder实例
                .select()
                // 决定了暴露哪些接口给 Swagger
                .paths(any())
                .build();
    }
    
}
