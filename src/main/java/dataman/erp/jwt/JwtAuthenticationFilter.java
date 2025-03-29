package dataman.erp.jwt;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;



import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dataman.erp.auth.service.UserMastService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserMastService userMastService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtTokenUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("JWT Token extraction failed: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // Return 403 if token is invalid
                response.getWriter().write("Invalid JWT token");
                return; // Stop further processing
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Fetch user details from CustomUserDetailsService
            UserDetails userDetails = userMastService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwt, userDetails.getUsername())) {
                // Optionally, store user-specific session or check if the token matches the user's session
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // Token is invalid
                response.getWriter().write("Invalid or expired JWT token");
                return;  // Stop further processing
            }
        }

        // Proceed with the next filter if the token is valid
        filterChain.doFilter(request, response);
    }
}
