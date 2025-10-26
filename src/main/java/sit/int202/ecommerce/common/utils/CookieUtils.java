package sit.int202.ecommerce.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

public class CookieUtils {

    private static String domain;
    private static String env;

    @Value("${app.backend-url}")
    public void setDomain(String domain) {
        CookieUtils.domain = domain;
    }

    @Value("${app.env}")
    public void setEnv(String env) {
        CookieUtils.env = env;
    }

    public CookieUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void setCookie(HttpServletResponse response, String name, String value) {
        boolean secure = !"local".equalsIgnoreCase(env);

        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setDomain(domain);
        cookie.setSecure(secure);
        cookie.setPath("/");
        response.addCookie(cookie);

        // Optional SameSite header
        String sameSite = String.format(
                "s=%s; Path=/; HttpOnly; SameSite=Strict; %s",
                name,
                value,
                secure ? "Secure" : ""
        );
        response.addHeader("Set-Cookie", sameSite);
    }
}
