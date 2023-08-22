package com.unvus.web.util;

import java.io.IOException;
import java.io.InputStream;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@ConditionalOnProperty(prefix="unvus.xss", name = "policy-resource")
@Component
public class XssUtil {
    private static AntiSamy antiSamy = new AntiSamy();
    private static Policy policy;


    private static String policyResource;

    @Value("${unvus.xss.policy-resource:#{null}}")
    public void setPolicyResource(String policyResource) {
        XssUtil.policyResource = policyResource;
        init();
    }

    private static void init() {
        if(policyResource == null) {
            return;
        }

        try {
            InputStream resource = new ClassPathResource(policyResource).getInputStream();
            XssUtil.policy = Policy.getInstance(resource);
        } catch (PolicyException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param potentiallyDirtyParameter
     *            string to be cleaned
     * @return a clean version of the same string
     */
    public static String clean(String potentiallyDirtyParameter) {
        if (policy == null) {
            log.error("antisamy policy file not defined");
            return potentiallyDirtyParameter;
        }
        if (potentiallyDirtyParameter == null) {
            return null;
        }
        String result = potentiallyDirtyParameter;

        try {
            CleanResults cr = antiSamy.scan(potentiallyDirtyParameter, policy);
            if (cr.getNumberOfErrors() > 0) {
                log.warn("antisamy encountered problem with input: "
                    + cr.getErrorMessages());
            }
            result = cr.getCleanHTML();
        } catch (Exception e) {
            e.printStackTrace();
            // throw new IllegalStateException(e.getMessage(), e);
        }
        return result;
    }
}
