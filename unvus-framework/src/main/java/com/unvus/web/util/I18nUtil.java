package com.unvus.web.util;

import java.util.Locale;

import javax.inject.Inject;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

@Component
public class I18nUtil {


    private static MessageSourceAccessor messages;

    @Inject
    public void setMessages(MessageSource messageSource) {
        messages = new MessageSourceAccessor(messageSource);
    }


    /**
     * Convenience method for getting a i18n key's value.  Calling
     * getMessageSourceAccessor() is used because the RequestContext variable
     * is not set in unit tests b/c there's no DispatchServlet Request.
     *
     * @param msgKey
     * @param locale the current locale
     * @return
     */
    public static String getText(String msgKey, Locale locale) {
        return messages.getMessage(msgKey, locale);
    }

    /**
     * Convenient method for getting a i18n key's value with a single
     * string argument.
     *
     * @param msgKey
     * @param arg
     * @param locale the current locale
     * @return
     */
    public static String getText(String msgKey, String arg, Locale locale) {
        return getText(msgKey, new Object[] { arg }, locale);
    }

    /**
     * Convenience method for getting a i18n key's value with arguments.
     *
     * @param msgKey
     * @param args
     * @param locale the current locale
     * @return
     */
    public static String getText(String msgKey, Object[] args, Locale locale) {
        return messages.getMessage(msgKey, args, locale);
    }


}
