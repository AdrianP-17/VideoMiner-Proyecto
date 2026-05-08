package aiss.peertubeiner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PeerTubeVideo {
    
    @JsonProperty("uuid")
    private String uuid;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("publishedAt")
    private String publishedAt;
    
    @JsonProperty("account")
    private PeerTubeAccount account;
    
    // --- ESCUDO JACKSON ---
    // @JsonIgnore en la variable evita que Jackson intente leerlo y crashee
    @JsonIgnore
    private CaptionResponse captions;

    @JsonIgnore
    private List<PeerTubeCommentThreadResponse.CommentThread> comments;


    // ==========================================
    //       GETTERS Y SETTERS BÁSICOS
    // ==========================================
    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public PeerTubeAccount getAccount() { return account; }
    public void setAccount(PeerTubeAccount account) { this.account = account; }


    // ==========================================
    //    GETTERS (Visibles) Y SETTERS (Invisibles)
    // ==========================================
    // Swagger usará el Getter para mostrarlo al usuario
    @JsonProperty("captions")
    public CaptionResponse getCaptions() { return captions; }
    
    // Jackson ignorará el Setter al leer la API de PeerTube
    @JsonIgnore
    public void setCaptions(CaptionResponse captions) { this.captions = captions; }

    @JsonProperty("comments")
    public List<PeerTubeCommentThreadResponse.CommentThread> getComments() { return comments; }
    
    @JsonIgnore
    public void setComments(List<PeerTubeCommentThreadResponse.CommentThread> comments) { this.comments = comments; }


    // ==========================================
    //       CLASES ANIDADAS
    // ==========================================
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CaptionResponse {
        @JsonProperty("data")
        private List<Caption> data;

        public List<Caption> getData() { return data; }
        public void setData(List<Caption> data) { this.data = data; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Caption {
        @JsonProperty("fileUrl")
        private String fileUrl;
        
        @JsonProperty("language")
        private Language language;

        public String getFileUrl() { return fileUrl; }
        public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

        public Language getLanguage() { return language; }
        public void setLanguage(Language language) { this.language = language; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Language {
        @JsonProperty("id")
        private String id;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }
}