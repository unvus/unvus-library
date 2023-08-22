package com.unvus.web.util;

import com.unvus.config.UnvusConstants;
import com.unvus.domain.LabelValue;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by guava on 7/30/16.
 */
@Slf4j
public class WebUtil {
    public static final int SESSION_SCOPE_COOKIE = -1;

    /**
     * Gets the query map.
     *
     * @return the query map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getQueryMap() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> condMap = new HashMap();

        Map<String, LabelValue> dynamicMap = new HashMap();

        for (String key : parameterMap.keySet()) {
            if(key.startsWith(UnvusConstants.CONDITION_PARAM_KEY)) {
                String[] values = parameterMap.get(key);
                // remove 'q.' prefix
                String conditionKey = key.substring(UnvusConstants.CONDITION_PARAM_KEY.length());

                // cond.dynamic.key.1 or cond.dynamic.value.1
                if(StringUtils.startsWith(conditionKey, "dynamic")) {
                    String[] parsedData = StringUtils.split(conditionKey, '.');
                    if(StringUtils.equals(parsedData[1], "key")) {
                        LabelValue lv = dynamicMap.get(parsedData[2]);
                        if(lv == null) {
                            lv = new LabelValue();
                            dynamicMap.put(parsedData[2], lv);
                        }

                        lv.setLabel(values[0]);
                    }else {
                        LabelValue lv = dynamicMap.get(parsedData[2]);
                        if(lv == null) {
                            lv = new LabelValue();
                            dynamicMap.put(parsedData[2], lv);
                        }

                        lv.setValue(values[0]);
                    }
                }else {
                    if(values.length > 1){
                        List<String> valueChk = new ArrayList();
                        for(String value : values){
                            if(StringUtils.isNotBlank(value)) {
                                valueChk.add(value);
                            }
                        }
                        condMap.put(conditionKey, valueChk.toArray(new String[valueChk.size()]));
                    }else{
                        if(StringUtils.isNotBlank(values[0])) {
                            condMap.put(conditionKey, values[0]);
                        }
                    }
                }
            }
        }

        if(!dynamicMap.isEmpty()) {
            dynamicMap.values().stream()
                .filter(lv -> StringUtils.isNotBlank(lv.getValue()))
                .forEach(lv -> condMap.put(lv.getLabel(), lv.getValue()));
        }

        if(condMap.isEmpty()) {
            condMap.put("EMPTYCOND", "EMPTYCOND");
        }

        return condMap;
    }

    private static int CACHE_PERIOD_UNIT = Calendar.MONTH;
    private static int CACHE_PERIOD_VALUE = 1;

    public static boolean needFreshResponse(HttpServletRequest request, SimpleDateFormat dateFormat) {
        boolean needFresh = true;
        String modifiedSince = request.getHeader("if-modified-since");
        if(modifiedSince == null) {
            Enumeration<String> sinceHeaders = request.getHeaders("if-modified-since");
            if(sinceHeaders.hasMoreElements()) {
                modifiedSince = sinceHeaders.nextElement();
            }
        }

        if(modifiedSince != null) {
            try {
                Date lastAccess = dateFormat.parse(modifiedSince);

                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                cal.add(CACHE_PERIOD_UNIT, CACHE_PERIOD_VALUE * -1);
                if(cal.getTime().compareTo(lastAccess) < 0) {
                    needFresh = false;
                }
            } catch (Exception ignore) {}
        }

        return needFresh;

    }

    public static void setCacheHeader(HttpServletResponse response, SimpleDateFormat dateFormat) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        response.setHeader("Last-Modified", dateFormat.format(cal.getTime()));

        cal.add(CACHE_PERIOD_UNIT, CACHE_PERIOD_VALUE);

        String maxAgeDirective = "max-age=" + (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000L;

        response.setHeader("Cache-Control",  maxAgeDirective);
        response.setHeader("Expires", dateFormat.format(cal.getTime()));
    }

    public static boolean isRequestAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    public static boolean isRequestAcceptJson(HttpServletRequest request) {
        return StringUtils.contains(request.getHeader("Accept"), "application/json");
    }

    public static boolean isRequestFromApp(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return StringUtils.startsWithAny(userAgent, "android", "ios");
    }


    public static String getSiteUri(String scheme)
    {
    	ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath();
        builder.scheme(scheme);
        return builder.build().toUri().toString();
    }


    public static void writeInputStream(HttpServletResponse response, InputStream in) throws IOException {
    	OutputStream out = null;
        try{
           out = response.getOutputStream();
           IOUtils.copy(in, out);
        }catch(Exception e){
            throw e;
        }finally{
           IOUtils.closeQuietly(out);
        }
    }


    public static void writeFile(HttpServletResponse response, File f) throws IOException {
        FileInputStream fin = null;
        FileChannel inputChannel = null;
        WritableByteChannel outputChannel = null;
        try{
            fin = new FileInputStream(f);
            inputChannel = fin.getChannel();
            outputChannel = Channels.newChannel(response.getOutputStream());

            inputChannel.transferTo(0, fin.available(), outputChannel);
        }catch(Exception e){
            throw e;
        }finally{
            IOUtils.closeQuietly(fin);
            IOUtils.closeQuietly(inputChannel);
            IOUtils.closeQuietly(outputChannel);
        }
    }


    public static void writeFileWithWatermark(File f, BufferedImage watermarkImage, File output, String fileType) throws IOException {
        try{
            writeWatermarkImage(f, watermarkImage, output, fileType);
        }catch(Exception e){
            throw e;
        }
    }



    private static void writeWatermarkImage(File sourceImageFile, BufferedImage watermarkImage, File output, String fileType) throws IOException {
        BufferedImage sourceImage = ImageIO.read(sourceImageFile);

        // initializes necessary graphic properties
        Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
        g2d.setComposite(alphaChannel);

        // calculates the coordinate where the image is painted
        int topLeftX = (sourceImage.getWidth() - watermarkImage.getWidth()) / 2;
        int topLeftY = (sourceImage.getHeight() - watermarkImage.getHeight()) / 2;

        // paints the image watermark
        g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);

        ImageIO.write(sourceImage, fileType, output);
        g2d.dispose();
    }

    public static void setCookie(HttpServletResponse response, String name,
                                 String value) {
        setCookie(response, name, value, "/");
    }

    /**
     * Convenience method to set a cookie
     *
     * @param response the current response
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param path the path to set it on
     */
    public static void setCookie(HttpServletResponse response, String name,
                                 String value, String path) {
        setCookie(response, name, value, path, false);
    }

    /**
     * Convenience method to set a cookie
     *
     * @param response the current response
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param path the path to set it on
     */
    public static void setCookie(HttpServletResponse response, String name,
                                 String value, String path, boolean secure) {
        if (log.isDebugEnabled()) {
            log.debug("Setting cookie '" + name + "' on path '" + path + "'");
        }

        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(secure);
        cookie.setPath(path);
        cookie.setMaxAge(3600 * 24 * 30); // 30 days

        response.addCookie(cookie);
    }

    /**
     * Convenience method to set a cookie
     *
     * @param response the current response
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param path the path to set it on
     */
    public static void setCookie(HttpServletResponse response, String name,
                                 String value, String path, boolean secure, int age) {
        if (log.isDebugEnabled()) {
            log.debug("Setting cookie '" + name + "' on path '" + path + "'");
        }

        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(secure);
        cookie.setPath(path);
        cookie.setMaxAge(age);

        response.addCookie(cookie);
    }


    /**
     * Convenience method to get a cookie by name
     *
     * @param request the current request
     * @param name the name of the cookie to find
     *
     * @return the cookie (if found), null if not found
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        Cookie returnCookie = null;

        if (cookies == null) {
            return returnCookie;
        }

        for (final Cookie thisCookie : cookies) {
            if (thisCookie.getName().equals(name) && !"".equals(thisCookie.getValue())) {
                returnCookie = thisCookie;
                break;
            }
        }

        return returnCookie;
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        return (cookie == null)?null:cookie.getValue();
    }

    /**
     * Convenience method for deleting a cookie by name
     *
     * @param response the current web response
     * @param cookie the cookie to delete
     * @param path the path on which the cookie was set (i.e. /appfuse)
     */
    public static void deleteCookie(HttpServletResponse response,
                                    Cookie cookie, String path) {
        if (cookie != null) {
            // Delete the cookie by setting its maximum age to zero
            cookie.setMaxAge(0);
            cookie.setPath(path);
            response.addCookie(cookie);
        }
    }

    public static boolean hasCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        for (final Cookie thisCookie : cookies) {
            if (thisCookie.getName().equals(name)) return true;
        }

        return false;
    }

    public static void updateCookie(String cookieName, String addValue, HttpServletRequest request, HttpServletResponse response) {
        updateCookie(cookieName, addValue, request, response, "/");
    }

    public static void updateCookie(String cookieName, String addValue, HttpServletRequest request, HttpServletResponse response, String separator) {
        Cookie cookie = getCookie(request, cookieName);

        if (cookie != null) {
            List<String> separatedValues = Arrays.asList(cookie.getValue().split(separator));
            StringBuilder valueBuilder = new StringBuilder();

            if (separatedValues.size() > 20) {
                List<String> copiedValues = new ArrayList<>();
                int start = separatedValues.size() - 20;

                for (int i = start; i < separatedValues.size(); i++) {
                    copiedValues.add(separatedValues.get(i));
                }
                separatedValues = copiedValues;
            }

            for (String val : separatedValues) {
                valueBuilder.append(val).append(separator);
            }
            valueBuilder.append(addValue);

            setCookie(response, cookieName, valueBuilder.toString());
        }
    }


    public static String extractPostRequestBody(HttpServletRequest request) throws IOException {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
        return "";
    }

    private final static List<String> IP_HEADERS = new ArrayList<>();
    static {
        IP_HEADERS.add("X-FORWARDED-FOR");
        IP_HEADERS.add("X-REAL_IP");
        IP_HEADERS.add("Proxy-Client-IP");
        IP_HEADERS.add("WL-Proxy-Client-IP");
        IP_HEADERS.add("HTTP_CLIENT_IP");
        IP_HEADERS.add("HTTP_X_FORWARDED_FOR");
    }

    public static String getIp(HttpServletRequest request) {
        if(request == null) {
            return null;
        }
        String ip = request.getRemoteAddr();
        for (String headerName : IP_HEADERS) {
            String headerIp = request.getHeader(headerName);

            if (!isEmptyIp(headerIp)) {
                ip =  headerIp;
                break;
            }
        }

        // proxy 다중 ip 리턴 시 첫번째 ip가 클라이언트ip
        try {
            if (ip != null && !ip.isEmpty() && ip.contains(",")) {
                ip = ip.split(",")[0];
            }
        } catch (NullPointerException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if("0:0:0:0:0:0:0:1".equals(ip)) {
            return "127.0.0.1";
        }

        return ip;
    }
    public static String getIp() {
        return getIp(request());
    }

    private static boolean isEmptyIp(String ip) {

        return StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip);
    }

    public static HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    public static HttpServletResponse response() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
    }


    public static CookieBuilder ofCookie(String name, String value) {
        return new CookieBuilder(name, value);
    }

    public static class CookieBuilder {

        private Cookie cookie;

        public CookieBuilder(String name, String value) {
            this.cookie = new Cookie(name, value);
            this.cookie.setPath("/");
            this.cookie.setHttpOnly(true);
            this.cookie.setSecure(true);
        }

        public CookieBuilder path(String path) {
            cookie.setPath(path);
            return this;
        }

        public CookieBuilder version(int version) {
            cookie.setVersion(version);
            return this;
        }

        public CookieBuilder comment(String comment) {
            cookie.setComment(comment);
            return this;
        }

        public CookieBuilder maxAge(int expiry) {
            cookie.setMaxAge(expiry);
            return this;
        }

        public CookieBuilder ofDays(int expiry) {
            cookie.setMaxAge(expiry * 60 * 60 * 24);
            return this;
        }

        public CookieBuilder ofHours(int expiry) {
            cookie.setMaxAge(expiry * 60 * 60);
            return this;
        }

        public CookieBuilder ofMinutes(int expiry) {
            cookie.setMaxAge(expiry * 60);
            return this;
        }

        public CookieBuilder secure(boolean secure) {
            cookie.setSecure(secure);
            return this;
        }

        public CookieBuilder httpOnly(boolean httpOnly) {
            cookie.setHttpOnly(httpOnly);
            return this;
        }

        public Cookie apply(HttpServletResponse response) {

            response.addCookie(cookie);
            return cookie;
        }

    }


}
