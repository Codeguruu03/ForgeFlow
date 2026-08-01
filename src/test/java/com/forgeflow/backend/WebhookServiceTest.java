package com.forgeflow.backend;

import com.forgeflow.backend.webhook.WebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WebhookServiceTest {

    @Autowired
    private WebhookService webhookService;

    @Test
    void testGithubWebhookHandling() {
        Map<String, Object> payload = Map.of(
                "ref", "refs/heads/main",
                "repository", Map.of("name", "ForgeFlow")
        );

        Map<String, Object> response = webhookService.handleGithubWebhook("push", payload);
        assertNotNull(response);
        assertEquals("push", response.get("event"));
    }
}
