package com.zerozone.vintage.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final CustomMetrics customMetrics;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MetricsInterceptor(customMetrics));
    }

    /*정적 리소스 경로를 수동으로 설정*/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploaded-profile-images/**로 시작하는 모든 요청을 uploaded-profile-images 폴더로 매핑
        registry.addResourceHandler("/uploaded-profile-images/**")
                .addResourceLocations("file:uploaded-profile-images/");
    }


}