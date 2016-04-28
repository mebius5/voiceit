package jhu.voiceit;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by GradyXiao on 4/22/16.
 */
public class Post {
    private User owner;
    private String audioEncoded;
    private String description;
    private String createDate;
    private int likes;

    private HashSet<String> likerIds= new HashSet<String>();

    /***
     * Default public constructor
     * Firebase requries an empty public constructor for
     * object representation
     */
    public Post(){

    }

    public Post(User owner) {
        this.owner = owner;
        this.createDate = (new Date()).toString();
        this.likes = 0;
    }

    public Post(User owner, String audioEncoded, String description, String createDate, int likes){
        this.owner = owner;
        this.audioEncoded = audioEncoded;
        this.description = description;
        this.createDate = createDate;
        this.likes = likes;
    }

    public User getOwner() { return owner;}
    public String getAudioEncoded() { return audioEncoded;}
    public String getDescription(){ return this.description;}
    public String getCreateDate() { return createDate;}
    public int getLikes() { return likes;}

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAudioEncoded(String audioEncoded) {
        this.audioEncoded = audioEncoded;
    }

    public String calculateElapsedTime(){
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

    public HashSet<String> getLikerIds(){
        return this.likerIds;
    }

    public void addLiker(String likerUserId){
        this.likerIds.add(likerUserId);
        this.likes++;
    }

    public void removeLiker(String likerUserId) {
        this.likerIds.remove(likerUserId);
        this.likes--;
    }

    public void likePost(String likerUserId){
        if(this.likerIds.contains(likerUserId)){
            removeLiker(likerUserId);
        } else{
            addLiker(likerUserId);
        }
    }

}
