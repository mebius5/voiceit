package jhu.voiceit;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by GradyXiao on 4/22/16.
 */
public class Post {
    private User owner;
    private String filename;
    private Calendar createDate;

    public Post(User owner, String filename) {
        this.owner = owner;
        this.filename = filename;
        this.createDate = Calendar.getInstance();
    }

    public User getOwner() { return owner;}
    public String getFilename() { return filename;}
    public Calendar getCreateDate() { return createDate;}

}
