package se.examenarbete.blitzduel.security;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import se.examenarbete.blitzduel.model.User;
import se.examenarbete.blitzduel.service.UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public OAuth2LoginSuccessHandler(UserService userService, JwtUtil jwtUtil){
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub");
        String picture = oAuth2User.getAttribute("picture");

        User user = userService.createOrUpdateUser(email, name, googleId, picture);

        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getName());

        String redirectUrl = String.format("http://localhost:5173/auth/callback?token=%s", token);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
