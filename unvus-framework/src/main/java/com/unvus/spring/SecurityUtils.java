package com.unvus.spring;

import org.springframework.context.annotation.Role;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility class for Spring Security.
 */
public class SecurityUtils {

    public static boolean hasAuthority(String authority) {
        return hasRole(authority);
    }

    public static boolean hasAnyAuthority(String... authorities) {
        return hasAnyRole(authorities);
    }

    public static boolean hasRole(String role) {
        return getAuthorities().contains(role);
    }

    public static boolean hasRole(Role role) {
        return getAuthorities().contains(role.toString());
    }

    public static boolean hasAnyRole(String... roles) {
        Set<String> authorities = getAuthorities();
        for (String role : roles) {
            if (authorities.contains(role)) {
                return true;
            }
        }
        // No roles matches
        return false;
    }

    public static boolean hasAnyRole(Role... roles) {
        Set<String> authorities = getAuthorities();
        for (Role role : roles) {
            if (authorities.contains(role.toString())) {
                return true;
            }
        }
        // No roles matches
        return false;
    }

    public static boolean isAnonymous() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass());
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return !isAnonymous();
    }

    public static boolean isFullyAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return !isAnonymous() && !isRememberMe();
    }

    public static boolean isRememberMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return RememberMeAuthenticationToken.class.isAssignableFrom(authentication.getClass());
    }

    public static boolean isSwitchedUser() {
        return hasRole("ROLE_PREVIOUS_ADMINISTRATOR");
    }

    private static Set<String> getAuthorities() {
        return AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities());
    }

    /**
     * If the current member has a specific authority (security role).
     *
     * <p>The name of this method comes from the isUserInRole() method in the Servlet API</p>
     *
     * @param authority the authorithy to check
     * @return true if the current member has the authority, false otherwise
     */
    public static boolean isCurrentUserInRole(String authority) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                return springSecurityUser.getAuthorities().stream().anyMatch(auth -> authority.equals(auth.getAuthority()));
            }
        }
        return false;
    }


    /**
     * Get the login of the current member.
     *
     * @return the login of the current member
     */
    public static UserDetails getCurrentUser() {
        if(isAuthenticated()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null) {
                return null;
            }
            return (UserDetails)authentication.getPrincipal();
        }
        return null;
    }


    /**
     * Programmatically signs in the member with the given the member ID.
     */
    public static void signin(UserDetails detail, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(detail, null, detail.getAuthorities());
        token.setDetails(new WebAuthenticationDetails(request));

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public static void addRole(String role, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(auth.getAuthorities());
        authorities.add(new SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(auth.getPrincipal(),auth.getCredentials(),authorities);
        token.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(token);

        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
    }
}
