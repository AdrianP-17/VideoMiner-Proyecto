package aiss.peertubeiner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PeerTubeAccount {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("avatars")
    private List<Avatar> avatars;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Avatar> getAvatars() { return avatars; }
    public void setAvatars(List<Avatar> avatars) { this.avatars = avatars; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Avatar {
        // PeerTube usa 'path' en lugar de 'url'
        @JsonProperty("path")
        private String path;

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
    }
}
