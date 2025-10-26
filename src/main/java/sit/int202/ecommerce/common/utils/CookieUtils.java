package sit.int202.ecommerce.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieUtils {

    private static String domain;
    private static String env;

    private CookieUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void setCookie(HttpServletResponse response, String name, String value, Duration maxAge) {
        boolean secure = !"local".equalsIgnoreCase(env);

        ResponseCookie cookie = ResponseCookie.from(name, value)
                .domain(domain)
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .maxAge(maxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void deleteCookie(HttpServletResponse response, String name) {
        boolean secure = !"local".equalsIgnoreCase(env);

        ResponseCookie cookie = ResponseCookie.from(name, "")
                .domain(domain)
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    @Value("${app.backend-url}")
    public void setDomain(String domain) {
        CookieUtils.domain = domain;
    }

    @Value("${app.env}")
    public void setEnv(String env) {
        CookieUtils.env = env;
    }
}
