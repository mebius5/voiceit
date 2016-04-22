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

    public Post(User owner, String filename, String description) {
        this.owner = owner;
        this.filename = filename;
        this.description = description;
        this.createDate = Calendar.getInstance();
    }

    public User getOwner() { return owner;}
    public String getFilename() { return filename;}
    public String getDescription(){ return this.description;}
    public Calendar getCreateDate() { return createDate;}

    public void setDescription(String description) {
        this.description = description;
    }
}
