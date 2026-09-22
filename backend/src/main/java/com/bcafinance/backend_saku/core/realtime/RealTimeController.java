package com.bcafinance.backend_saku.core.realtime;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/realtime")
@RequiredArgsConstructor
@Tag(name = "Real-Time Controller", description = "Server-Sent Events (SSE) Stream untuk sinkronisasi data real-time Backoffice, Marketing, dan Branch Manager")
public class RealTimeController {

    private final RealTimeEmitterService realTimeEmitterService;

    @Operation(summary = "Stream SSE Real-Time", description = "Membuka stream Server-Sent Events untuk menerima event perubahan data KYC dan Pinjaman secara langsung")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(Authentication authentication) {
        return realTimeEmitterService.subscribe(authentication);
    }
}
