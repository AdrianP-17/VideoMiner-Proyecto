package aiss.dailymotionminer.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DailymotionVideo {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("created_time")
    private Long createdTime;
    
    // --- VARIABLES PLANAS DEL OWNER ---
    @JsonProperty("owner.id")
    private String ownerId;
    
    @JsonProperty("owner.username")
    private String ownerUsername;
    
    @JsonProperty("owner.url")
    private String ownerUrl;
    
    @JsonProperty("owner.avatar_80_url")
    private String ownerAvatarUrl;

    @JsonProperty("subtitles_data")
    private List<Subtitle> subtitles;
    
    @JsonProperty("tags")
    private List<String> tags;


    // ==========================================
    //           GETTERS Y SETTERS
    // ==========================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    // Getters y Setters del Owner
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getOwnerUrl() {
        return ownerUrl;
    }

    public void setOwnerUrl(String ownerUrl) {
        this.ownerUrl = ownerUrl;
    }

    public String getOwnerAvatarUrl() {
        return ownerAvatarUrl;
    }

    public void setOwnerAvatarUrl(String ownerAvatarUrl) {
        this.ownerAvatarUrl = ownerAvatarUrl;
    }

    // Getters y Setters de Listas
    public List<Subtitle> getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(List<Subtitle> subtitles) {
        this.subtitles = subtitles;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }


    // ==========================================
    //      CLASE ANIDADA PARA SUBTÍTULOS
    // ==========================================
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Subtitle {
        
        @JsonProperty("url")
        private String url;
        
        @JsonProperty("language")
        private String language;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
}