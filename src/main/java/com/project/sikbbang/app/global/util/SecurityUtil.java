package com.project.sikbbang.app.global.util;

import com.project.sikbbang.app.global.code.status.ErrorStatus;
import com.project.sikbbang.app.global.exception.GeneralException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityUtil {

    public static Long getCurrentAdminId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().toString().isEmpty()) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        return Long.valueOf(authentication.getPrincipal().toString());
    }

    public static String getCurrentAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().toString().isEmpty()) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities.isEmpty()) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
        String role = ((GrantedAuthority) authorities.toArray()[0]).getAuthority();
        return role.replace("ROLE_", "");
    }
}
