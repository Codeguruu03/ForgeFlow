package com.forgeflow.backend.controller;

import com.forgeflow.backend.webhook.WebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/github")
    public ResponseEntity<Map<String, Object>> handleGithubWebhook(
            @RequestHeader(value = "X-GitHub-Event", defaultValue = "push") String eventHeader,
            @RequestBody Map<String, Object> payload) {
        Map<String, Object> res = webhookService.handleGithubWebhook(eventHeader, payload);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/slack/test")
    public ResponseEntity<Map<String, Boolean>> sendSlackTest(@RequestBody Map<String, String> request) {
        String url = request.get("webhookUrl");
        String msg = request.getOrDefault("message", "ForgeFlow Slack Gateway Test Alert");
        boolean ok = webhookService.sendSlackNotification(url, msg);
        return ResponseEntity.ok(Map.of("success", ok));
    }

    @PostMapping("/discord/test")
    public ResponseEntity<Map<String, Boolean>> sendDiscordTest(@RequestBody Map<String, String> request) {
        String url = request.get("webhookUrl");
        String msg = request.getOrDefault("message", "ForgeFlow Discord Gateway Test Alert");
        boolean ok = webhookService.sendDiscordNotification(url, msg);
        return ResponseEntity.ok(Map.of("success", ok));
    }
}
