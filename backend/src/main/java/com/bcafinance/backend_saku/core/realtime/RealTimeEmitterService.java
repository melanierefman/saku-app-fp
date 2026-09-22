package com.bcafinance.backend_saku.core.realtime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class RealTimeEmitterService {

    // Emitter timeout: 60 minutes. Browser EventSource will auto-reconnect if it times out.
    private static final Long EMITTER_TIMEOUT = 60 * 60 * 1000L;

    private static class ClientSubscription {
        final SseEmitter emitter;
        final String username;
        final Set<String> roles;

        ClientSubscription(SseEmitter emitter, String username, Set<String> roles) {
            this.emitter = emitter;
            this.username = username;
            this.roles = roles;
        }
    }

    private final List<ClientSubscription> clients = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe(Authentication authentication) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT);

        String username = (authentication != null && authentication.getName() != null)
                ? authentication.getName()
                : "anonymous";

        Set<String> roles = new HashSet<>();
        if (authentication != null && authentication.getAuthorities() != null) {
            for (GrantedAuthority ga : authentication.getAuthorities()) {
                roles.add(ga.getAuthority().toUpperCase());
            }
        }

        ClientSubscription subscription = new ClientSubscription(emitter, username, roles);
        clients.add(subscription);
        log.info("SSE Client subscribed: user={}, roles={}, activeClients={}", username, roles, clients.size());

        emitter.onCompletion(() -> {
            clients.remove(subscription);
            log.debug("SSE Client completed: user={}, activeClients={}", username, clients.size());
        });

        emitter.onTimeout(() -> {
            emitter.complete();
            clients.remove(subscription);
            log.debug("SSE Client timeout: user={}, activeClients={}", username, clients.size());
        });

        emitter.onError(e -> {
            clients.remove(subscription);
            log.debug("SSE Client error: user={}, err={}", username, e.getMessage());
        });

        // Send initial connected heartbeat event
        try {
            RealTimeEventDto initialEvent = RealTimeEventDto.builder()
                    .eventType("CONNECTED")
                    .title("Real-Time Connected")
                    .message("Connected to Saku Real-Time Stream")
                    .timestamp(LocalDateTime.now())
                    .build();
            emitter.send(SseEmitter.event().name("init").data(initialEvent));
        } catch (IOException e) {
            clients.remove(subscription);
        }

        return emitter;
    }

    public void broadcast(RealTimeEventDto event) {
        if (event == null) return;
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }

        List<ClientSubscription> deadClients = new ArrayList<>();
        List<String> targetRoles = event.getTargetRoles();

        for (ClientSubscription client : clients) {
            // Superadmin always receives all real-time events for monitoring
            boolean isSuperadmin = client.roles.contains("ROLE_SUPERADMIN");

            boolean isTarget = isSuperadmin;
            if (!isTarget && (targetRoles == null || targetRoles.isEmpty())) {
                isTarget = true;
            } else if (!isTarget) {
                for (String tr : targetRoles) {
                    if (client.roles.contains(tr.toUpperCase())) {
                        isTarget = true;
                        break;
                    }
                }
            }

            if (!isTarget) {
                continue;
            }

            try {
                client.emitter.send(SseEmitter.event()
                        .name(event.getEventType())
                        .data(event));
            } catch (Exception e) {
                log.warn("Failed sending SSE event to user {}: {}", client.username, e.getMessage());
                deadClients.add(client);
            }
        }

        if (!deadClients.isEmpty()) {
            clients.removeAll(deadClients);
        }
    }
}
