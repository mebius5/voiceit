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

    /**
     * Constructor of post with only one argument
     * @param owner
     */
    public Post(User owner) {
        this.owner = owner;
        this.createDate = (new Date()).toString();
        this.likes = 0;
    }

    /***
     * Constructor of post with all arguments
     * @param owner
     * @param audioEncoded
     * @param description
     * @param createDate
     * @param likes
     */
    public Post(User owner, String audioEncoded, String description, String createDate, int likes){
        this.owner = owner;
        this.audioEncoded = audioEncoded;
        this.description = description;
        this.createDate = createDate;
        this.likes = likes;
    }

    /***
     * Get the owner
     * Necessary for Firebase
     * @return
     */
    public User getOwner() { return owner;}

    /**
     * Get the encoded audio string
     * Necessary for Firebase
     * @return
     */
    public String getAudioEncoded() { return audioEncoded;}

    /**
     * Get the description of the post
     * Necessary for Firebase
     * @return
     */
    public String getDescription(){ return this.description;}

    /**
     * Get the create date of the post
     * Necessary for firebase although not used
     * @return
     */
    public String getCreateDate() { return createDate;}

    /***
     * Get the number of likes
     * Necessary for Firebase
     * @return
     */
    public int getLikes() { return likes;}

    /***
     * Gets the likerIds
     * Necessary for Firebase
     * @return
     */
    public HashSet<String> getLikerIds(){
        return this.likerIds;
    }

    /****
     * Sets the description of the post
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /***
     * Sets the encoded audio file of the post
     * @param audioEncoded
     */
    public void setAudioEncoded(String audioEncoded) {
        this.audioEncoded = audioEncoded;
    }

    /***
     * Calculates the time difference between the post time and now
     * @return the time difference description in string format
     */
    public String calculateElapsedTime(){
        Date before = new Date(this.createDate);
        Date now = new Date();

        long diff = now.getTime() - before.getTime();
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

    /**
     * Add liker id and increment like count
     * @param likerUserId
     */
    public void addLiker(String likerUserId){
        this.likerIds.add(likerUserId);
        this.likes++;
    }

    /***
     * Remove liker id and decrement like count
     * @param likerUserId
     */
    public void removeLiker(String likerUserId) {
        this.likerIds.remove(likerUserId);
        this.likes--;
    }

    /**
     * If not already liked, add liker. Else, remove liker
     * @param likerUserId
     */
    public boolean likePost(String likerUserId){
        if(this.likerIds.contains(likerUserId)){
            removeLiker(likerUserId);
            return false;
        } else{
            addLiker(likerUserId);
            return true;
        }
    }

}
