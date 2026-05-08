package aiss.dailymotionminer.dto;
import java.util.List;
public class VMChannel {
    private String id; 
    private String name; 
    private String description; 
    private String createdTime; 
    private List<VMVideo> videos;

    public String getId() { 
        return id; 
    } 
    public void setId(String id) { 
        this.id = id; 
    }
    public String getName() { 
        return name; 
    } 
    public void setName(String name) { 
        this.name = name; 
    }
    public String getDescription() { 
        return description; 
    } 
    public void setDescription(String description) { 
        this.description = description; 
    }
    public String getCreatedTime() { 
        return createdTime; 
    } 
    public void setCreatedTime(String createdTime) { 
        this.createdTime = createdTime; 
    }
    public List<VMVideo> getVideos() { 
        return videos; 
    } 
    public void setVideos(List<VMVideo> videos) { 
        this.videos = videos; 
    }
}