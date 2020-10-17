package tech.wetech.transacation;

import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.wetech.transacation.integration.feign.TransactionFeignInterceptor;
import tech.wetech.transacation.integration.http.TransactionPropagationInterceptor;

/**
 * Auto bean add for spring context if in springboot env.
 *
 * @author cjbi
 */
@Configuration
@ConditionalOnWebApplication
public class GlobalTransactionHttpAutoConfiguration implements WebMvcConfigurer {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new TransactionFeignInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TransactionPropagationInterceptor());
    }
}
