package sit.int202.ecommerce.modules.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sit.int202.ecommerce.modules.security.constants.SecurityConstants;
import sit.int202.ecommerce.modules.auth.services.UserDetailsServiceImpl;
import sit.int202.ecommerce.modules.security.jwt.JwtTokenProvider;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);

            if (token != null && tokenProvider.validateAccessToken(token)) {
                Authentication authentication = tokenProvider.getAuthentication(token);

                if (authentication instanceof UsernamePasswordAuthenticationToken authToken) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("[JWT Filter] SecurityContextHolder updated ✅");
                } else {
                    System.out.println("[JWT Filter] ⚠️ Authentication ไม่ใช่ UsernamePasswordAuthenticationToken");
                }
            }

        } catch (Exception ex) {
            System.out.println("[JWT Filter] Error processing JWT token: " + ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(SecurityConstants.ACCESS_TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}