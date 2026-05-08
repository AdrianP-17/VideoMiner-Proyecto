package aiss.peertubeiner.dto;
import java.util.List;
public class VMVideo {
    private String id; 
    private String name; 
    private String description; 
    private String releaseTime;
    private VMUser user; 
    private List<VMComment> comments; 
    private List<VMCaption> captions;

    public String getId() { 
        return id; 
    } 
    public void setId(String id) { 
        this.id = id; 
    }
    public String getName() { 
        return name; 
    } public void setName(String name) { 
        this.name = name; 
    }
    public String getDescription() { 
        return description; 
    } 
    public void setDescription(String description) { 
        this.description = description; 
    }
    public String getReleaseTime() { 
        return releaseTime; 
    } 
    public void setReleaseTime(String releaseTime) { 
        this.releaseTime = releaseTime; 
    }
    public VMUser getUser() { 
        return user; 
    } 
    public void setUser(VMUser user) { 
        this.user = user; 
    }
    public List<VMComment> getComments() { 
        return comments; 
    } 
    public void setComments(List<VMComment> comments) { 
        this.comments = comments; 
    }
    public List<VMCaption> getCaptions() { 
        return captions; 
    } 
    public void setCaptions(List<VMCaption> captions) { 
        this.captions = captions; 
    }
}