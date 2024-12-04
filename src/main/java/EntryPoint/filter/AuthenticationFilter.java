package EntryPoint.filter;

import EntryPoint.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String authRefreshTokenHeader = request.getHeader("Refresh-Token");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            System.out.println("token in filter: " + token);
            if(authRefreshTokenHeader != null && authRefreshTokenHeader.startsWith("Bearer ")){
                String refreshToken = authRefreshTokenHeader.substring(7).trim();
                System.out.println("token in Refreshfilter: " + refreshToken);
                if (jwtUtil.validateToken(token) && jwtUtil.validateToken(refreshToken)) {
                    System.out.println("token in filter validation: " + refreshToken);
                    if(jwtUtil.isTokenInvalidated(refreshToken) || jwtUtil.isTokenInvalidated(token)){
                        System.out.println("token in error validation: " + token);
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token has been invalidated. Please login again.");
                        return;
                    }
                    String email = jwtUtil.extractEmail(token);
                    // Set Authentication in SecurityContext
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(email, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token!");
                    return;
                }
            } else {
                if (jwtUtil.validateToken(token)) {
                    System.out.println("token for validation normal: " + token);
                    if(jwtUtil.isTokenInvalidated(token)){
                        System.out.println("token for normal validation error: " + token);
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token has been invalidated. Please login again.");
                        return;
                    }
                    String email = jwtUtil.extractEmail(token);
                    // Set Authentication in SecurityContext
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(email, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token!");
                    return;
                }
            }
        } else if (!request.getServletPath().startsWith("/api/v1/auth")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header missing or invalid!");
            return;
        }

        // Proceed with the request
        filterChain.doFilter(request, response);
    }
}
