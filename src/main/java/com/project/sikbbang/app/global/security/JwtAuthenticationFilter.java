package com.project.sikbbang.app.global.security;

import com.project.sikbbang.app.domain.admin.model.Admin;
import com.project.sikbbang.app.domain.admin.model.AdminRole;
import com.project.sikbbang.app.domain.admin.repository.AdminRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AdminRepository adminRepository;

    public JwtAuthenticationFilter(JwtTokenProvider provider, AdminRepository adminRepository) {
        this.jwtTokenProvider = provider;
        this.adminRepository = adminRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String id = jwtTokenProvider.getAdminIdFromToken(token);
                String role = jwtTokenProvider.getRoleFromToken(token);

                if (role.equals(AdminRole.BOOTH_OPERATOR.name())) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    id, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    Optional<Admin> adminOpt = adminRepository.findById(Long.valueOf(id));
                    if (adminOpt.isPresent() && adminOpt.get().getRole().name().equals(role) && adminOpt.get().getRole() != AdminRole.NOT_ACTIVATE_ADMIN) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        id, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
