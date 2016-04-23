package jhu.voiceit;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by GradyXiao on 4/22/16.
 */
public class Post {
    private User owner;
    private String audioFilename;
    private String description;
    private String createDate;
    private int likes;

    public Post(){

    }

    public Post(User owner, String audioFilename, String description) {
        this.owner = owner;
        this.audioFilename = audioFilename;
        this.description = description;
        this.createDate = (new Date()).toString();
        this.likes = 0;
    }

    public Post(User owner, String audioFilename, String description, String createDate, int likes){
        this.owner = owner;
        this.audioFilename = audioFilename;
        this.description = description;
        this.createDate = createDate;
        this.likes = likes;
    }

    public User getOwner() { return owner;}
    public String getAudioFilename() { return audioFilename;}
    public String getDescription(){ return this.description;}
    public String getCreateDate() { return createDate;}
    public int getLikes() { return likes;}

    public void setDescription(String description) {
        this.description = description;
    }

    public String getElapsedTime(){
        Date before = new Date(this.createDate);
        Date now = new Date();

        long diff = before.getTime() - now.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) ;
        long diffDays = (int) ((diff)/ (24 * 60 * 60 * 1000));

        if(diffDays>=1){
            return diffDays+"d";
        } else if (diffHours>=1){
            return diffHours+"h";
        } else if (diffMinutes >=1){
            return diffMinutes+"m";
        } else if (diffSeconds >=1){
            return diffSeconds+"s";
        } else{
            return "now";
        }
    }

}
