package org.zuchini.spring;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class ScenarioScopeConfiguration {

    @Bean
    public static SpringThreadLocalScope getSpringThreadLocalScope() {
        return new SpringThreadLocalScope();
    }

    @Bean
    public static CustomScopeConfigurer getScenarioScopeConfigurer(SpringThreadLocalScope scope) {
        CustomScopeConfigurer customScopeConfigurer = new CustomScopeConfigurer();
        customScopeConfigurer.setScopes(Collections.<String, Object>singletonMap(ScenarioScoped.SCOPE_NAME, scope));
        return customScopeConfigurer;
    }
}
