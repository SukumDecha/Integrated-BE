package sit.int202.ecommerce.modules.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sit.int202.ecommerce.modules.security.model.UserPrincipal;
import sit.int202.ecommerce.modules.security.services.UserDetailsServiceImpl;

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

        String token = getTokenFromRequest(request);
        System.out.println("[JWT Filter] Token = " + token);

        try {
            if (token != null && tokenProvider.validateToken(token)) {
                String email = tokenProvider.getEmailFromToken(token);
                System.out.println("[JWT Filter] Email = " + email);

                // โหลดข้อมูล user จาก email
                UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(email);
                System.out.println("[JWT Filter] userDetails = " + userPrincipal);
                System.out.println("[JWT Filter] userDetails instanceof UserPrincipal = " + (userPrincipal instanceof UserPrincipal));

                // สร้าง Authentication จาก token
                Authentication authentication = tokenProvider.getAuthentication(token);

                // 🔧 แก้ตรงนี้: cast เพื่อใช้ setDetails ได้
                if (authentication instanceof UsernamePasswordAuthenticationToken authToken) {
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("[JWT Filter] SecurityContextHolder updated ✅");
                } else {
                    System.out.println("[JWT Filter] ⚠️ Authentication ไม่ใช่ UsernamePasswordAuthenticationToken");
                }
            }
        } catch (Exception e) {
            System.out.println("[JWT Filter] ⚠️ ERROR in filter: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}