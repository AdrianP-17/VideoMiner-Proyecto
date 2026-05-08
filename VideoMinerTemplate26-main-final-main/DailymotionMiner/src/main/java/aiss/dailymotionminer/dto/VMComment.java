package aiss.dailymotionminer.dto;
public class VMComment {
    private String id; 
    private String text;
    private String createdOn;

    public String getId() { 
        return id; 
    } 
    public void setId(String id) { 
        this.id = id; 
    }
    public String getText() { 
        return text; 
    } 
    public void setText(String text) { 
        this.text = text; 
    }
    public String getCreatedOn() { 
        return createdOn; 
    } public void setCreatedOn(String createdOn) { 
        this.createdOn = createdOn; 
    }
}