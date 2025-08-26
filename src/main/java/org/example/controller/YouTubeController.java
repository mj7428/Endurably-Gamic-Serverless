package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.service.YouTubeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class YouTubeController {

    private final YouTubeService youTubeService;

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchVideos(@RequestParam String query) {
        // --- DEBUG START ---
        System.out.println("--- [CONTROLLER] Received request for /search with query: " + query + " ---");
        // --- DEBUG END ---

        List<String> videoIds = youTubeService.searchVideos(query, 3L);

        // --- DEBUG START ---
        System.out.println("--- [CONTROLLER] Found " + videoIds.size() + " videos for query '" + query + "'. Returning IDs: " + videoIds + " ---");
        // --- DEBUG END ---

        return ResponseEntity.ok(videoIds);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<String>> getRecentVideos() {
        // --- DEBUG START ---
        System.out.println("--- [CONTROLLER] Received request for /recent videos ---");
        // --- DEBUG END ---

        List<String> videoIds = youTubeService.getRecentVideos(3L);

        // --- DEBUG START ---
        System.out.println("--- [CONTROLLER] Found " + videoIds.size() + " recent videos. Returning IDs: " + videoIds + " ---");
        // --- DEBUG END ---

        return ResponseEntity.ok(videoIds);
    }
}
