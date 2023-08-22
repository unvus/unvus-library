package com.unvus.web.filter.security;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.web.filter.OncePerRequestFilter;

public class XssFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        Enumeration enumeration = request.getParameterNames();

        while (enumeration.hasMoreElements()) {
            String key = (String)enumeration.nextElement();
            String[] values = request.getParameterValues(key);
            for(int i = 0 ; i < values.length; i++) {
                values[i] = Jsoup.clean(values[i], Whitelist.relaxed());
            }
        }

        super.doFilter(request, response, filterChain);
    }

}

