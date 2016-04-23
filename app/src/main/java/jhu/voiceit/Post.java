package jhu.voiceit;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by GradyXiao on 4/22/16.
 */
public class Post {
    private User owner;
    private String filename;
    private String description;
    private Calendar createDate;
    private int likes;

    public Post(User owner, String filename, String description) {
        this.owner = owner;
        this.filename = filename;
        this.description = description;
        this.createDate = Calendar.getInstance();
        this.likes = 0;
    }

    public User getOwner() { return owner;}
    public String getFilename() { return filename;}
    public String getDescription(){ return this.description;}
    public Calendar getCreateDate() { return createDate;}
    public int getLikes() { return likes;}

    public void setDescription(String description) {
        this.description = description;
    }
}
