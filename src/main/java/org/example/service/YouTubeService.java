package org.example.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YouTubeService {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.channel.id}")
    private String channelId;

    private YouTube youtube;

    public YouTubeService() {
    }

    // This method will be called after all @Value fields are populated
    @PostConstruct
    public void init() {
        this.youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {})
                .setApplicationName(applicationName)
                .build();
    }

    /**
     * Searches for videos on a specific channel that match a query.
     * @param query The search term (e.g., "TH14 Attack").
     * @param maxResults The maximum number of videos to return.
     * @return A list of video IDs.
     */
    @Cacheable("youtube-search-results")
    public List<String> searchVideos(String query, long maxResults) {
        // --- DEBUG START ---

        // --- DEBUG END ---
        try {
            YouTube.Search.List searchRequest = youtube.search().list("id");

            searchRequest.setKey(apiKey);
            searchRequest.setChannelId(channelId);
            searchRequest.setQ(query);
            searchRequest.setType("video");
            searchRequest.setMaxResults(maxResults);
            searchRequest.setOrder("relevance");

            List<SearchResult> searchResults = searchRequest.execute().getItems();

            // --- DEBUG START ---
            // --- DEBUG END ---

            return searchResults.stream()
                    .map(item -> item.getId().getVideoId())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            // --- DEBUG START ---
            e.printStackTrace();
            // --- DEBUG END ---
            return Collections.emptyList();
        }
    }

    @Cacheable("youtube-recent-videos")
    public List<String> getRecentVideos(long maxResults) {
        // --- DEBUG START ---

        // --- DEBUG END ---
        try {
            YouTube.Search.List searchRequest = youtube.search().list("id");

            searchRequest.setKey(apiKey);
            searchRequest.setChannelId(channelId);
            searchRequest.setType("video");
            searchRequest.setMaxResults(maxResults);
            searchRequest.setOrder("date");

            List<SearchResult> searchResults = searchRequest.execute().getItems();

            // --- DEBUG START ---
            // --- DEBUG END ---

            return searchResults.stream()
                    .map(item -> item.getId().getVideoId())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            // --- DEBUG START ---
            e.printStackTrace();
            // --- DEBUG END ---
            return Collections.emptyList();
        }
    }
}
