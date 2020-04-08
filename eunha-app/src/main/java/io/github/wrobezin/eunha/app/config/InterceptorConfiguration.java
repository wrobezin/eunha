package io.github.wrobezin.eunha.app.config;

import io.github.wrobezin.eunha.app.iterceptor.CrosInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/8 20:22
 */
@Configuration
public class InterceptorConfiguration extends WebMvcConfigurationSupport {

    private final CrosInterceptor crosInterceptor;

    public InterceptorConfiguration(CrosInterceptor crosInterceptor) {
        this.crosInterceptor = crosInterceptor;
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(crosInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
