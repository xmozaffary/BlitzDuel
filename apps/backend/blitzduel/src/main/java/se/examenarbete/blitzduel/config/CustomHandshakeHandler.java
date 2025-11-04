package se.examenarbete.blitzduel.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String userId = UUID.randomUUID().toString();
        return new StompPrincipal(userId);
    }

    private static class StompPrincipal implements Principal {
        private final String name;

        StompPrincipal(String name)  {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
