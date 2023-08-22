package com.unvus.web.filter.security;

import com.unvus.web.filter.security.antisamy.WildcardPattern;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.filter.OncePerRequestFilter;

public class AntiSamyFilter extends OncePerRequestFilter {
    private Log log = LogFactory.getLog(AntiSamyFilter.class);

    private static final String NO_POLICY_FILE = "A policy file is required. Please set the init parameter ({0}) in your web.xml or call the setter";
    private static final String POLICY_FILE_PARAM = "antisamy-policy-file";
    private static final String OUTPUT_ENCODING_PARAM = "antisamy-output-encoding";
    private static final String INPUT_ENCODING_PARAM = "antisamy-input-encoding";

    private static final String SCAN_METHOD_PARAM = "antisamy-method";

    private static final String EXCLUDE_PATTERN = "excludes";
    private static final String INCLUDE_PATTERN = "includes";

    private AntiSamy antiSamy;

    private Policy policy;
    private String inputEncoding = "UTF-8";
    private String outputEncoding = "UTF-8";

    private List<String> methods = new ArrayList<>();

    private List<WildcardPattern> excludePatterns = new ArrayList<>();

    private List<WildcardPattern> includePatterns = new ArrayList<>();

    public AntiSamyFilter() {
        antiSamy = new AntiSamy();
    }

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();

        FilterConfig filterConfig = getFilterConfig();

        String policyResource = filterConfig.getInitParameter(POLICY_FILE_PARAM);
        if (isBlank(policyResource)) {
            throw new IllegalStateException(MessageFormat.format(NO_POLICY_FILE, POLICY_FILE_PARAM));
        }

        try {
            InputStream resource = new ClassPathResource(policyResource).getInputStream();
            this.policy = Policy.getInstance(resource);
        } catch (PolicyException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String output = filterConfig.getInitParameter(OUTPUT_ENCODING_PARAM);
        if (!isBlank(output)) {
            outputEncoding = output;
        }

        String input = filterConfig.getInitParameter(INPUT_ENCODING_PARAM);
        if (!isBlank(input)) {
            inputEncoding = input;
        }

        String methodParam = filterConfig.getInitParameter(SCAN_METHOD_PARAM);
        if(!isBlank(methodParam)) {
            String[] methodsArr = StringUtils.split(methodParam, ",");
            for (String m : methodsArr) {
                methods.add(m);
            }
        }

//        antiSamy.setInputEncoding(inputEncoding);
//        antiSamy.setOutputEncoding(outputEncoding);

        addPattern(filterConfig.getInitParameter(EXCLUDE_PATTERN), excludePatterns);

        addPattern(filterConfig.getInitParameter(INCLUDE_PATTERN), includePatterns);

    }

    private void addPattern(String patterns, List<WildcardPattern> excludePatterns) {
        if (!isBlank(patterns)) {
            String[] patternArr = StringUtils.split(patterns, ",");
            for (String p : patternArr) {
                excludePatterns.add(WildcardPattern.create(p));
            }
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest,
                                    HttpServletResponse servletResponse, FilterChain filterChain)
        throws ServletException, IOException {
        HttpServletRequest request = servletRequest;
        String path = request.getRequestURI().substring(request.getContextPath().length());

        boolean doCleanByMethod = true;

        boolean doCleanByUri = true;

        if (CollectionUtils.isNotEmpty(methods)) {
            doCleanByMethod = false;
        }

        if (CollectionUtils.isNotEmpty(includePatterns)) {
            doCleanByUri = false;
        }

        for (String m : methods) {
            if (StringUtils.equalsIgnoreCase(m, request.getMethod())) {
                doCleanByMethod = true;
                break;
            }
        }

        for (WildcardPattern pattern : includePatterns) {
            if (pattern.match(path)) {
                doCleanByUri = true;
                break;
            }
        }

        for (WildcardPattern pattern : excludePatterns) {
            if (pattern.match(path)) {
                doCleanByUri = false;
                break;
            }
        }


        if (doCleanByMethod && doCleanByUri) {

            if (request instanceof HttpServletRequest) {
                CleanServletRequest cleanRequest = new CleanServletRequest(request, antiSamy, this.policy);
                filterChain.doFilter(cleanRequest, servletResponse);
            } else {
                filterChain.doFilter(request, servletResponse);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public void destroy() {

    }

    private boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }


    /**
     * Wrapper for a {@link HttpServletRequest} that returns 'safe' parameter
     * values by passing the raw request parameters through the anti-samy
     * filter. Should be private
     */
    public static class CleanServletRequest extends HttpServletRequestWrapper {
        private Log log = LogFactory.getLog(CleanServletRequest.class);

        private final AntiSamy antiSamy;
        private final Policy policy;

        private CleanServletRequest(HttpServletRequest request,
                                    AntiSamy antiSamy, Policy policy) {
            super(request);
            this.antiSamy = antiSamy;
            this.policy = policy;
        }

        /**
         * overriding getParameter functions in {@link ServletRequestWrapper}
         */
        @Override
        public String[] getParameterValues(String name) {
            String[] originalValues = super.getParameterValues(name);
            if (originalValues == null) {
                return null;
            }
            List<String> newValues = new ArrayList<>(originalValues.length);
            for (String value : originalValues) {
                newValues.add(filterString(value));
            }
            return newValues.toArray(new String[newValues.size()]);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Map getParameterMap() {
            Map<String, String[]> originalMap = super.getParameterMap();
            Map<String, String[]> filteredMap = new ConcurrentHashMap<String, String[]>(
                originalMap.size());
            for (String name : originalMap.keySet()) {
                filteredMap.put(name, getParameterValues(name));
            }
            return Collections.unmodifiableMap(filteredMap);
        }

        @Override
        public String getParameter(String name) {
            String potentiallyDirtyParameter = super.getParameter(name);
            return filterString(potentiallyDirtyParameter);
        }

        /**
         * This is only here so we can see what the original parameters were,
         * you should delete this method!
         *
         * @return original unwrapped request
         */
        @Deprecated
        public HttpServletRequest getOriginalRequest() {
            return (HttpServletRequest) super.getRequest();
        }

        /**
         * @param potentiallyDirtyParameter
         *            string to be cleaned
         * @return a clean version of the same string
         */
        private String filterString(String potentiallyDirtyParameter) {
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
}
