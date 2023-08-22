package com.unvus.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

@Configuration
@ConditionalOnWebApplication
public class LocaleConfiguration {

    @Value("${unvus.i18n.locale:ko-KR}")
    private String locale;

    /**
     * * @return default Locale set by the user
     */
    @Bean(name = "unvusLocaleResolver")
    public LocaleResolver localeResolver() {
        Locale defaultLocale = Locale.forLanguageTag(locale);
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(defaultLocale);
        return resolver;
    }


}
