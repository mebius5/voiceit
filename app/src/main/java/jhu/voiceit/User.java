package jhu.voiceit;

/**
 * Created by GradyXiao on 4/22/16.
 */
public class User {
    private String userId;
    private String username;
    private String profilePicName;

    /***
     * Default public constructor
     * Firebase requries an empty public constructor for
     * object representation
     */
    public User(){

    }

    public User(String userId, String username, String profilePicName){
        this.userId = userId;
        this.username = username;
        this.profilePicName = profilePicName;
    }

    public String getUserId(){
        return this.userId;
    }

    public String getUsername() { return this.username;}

    public String getProfilePicName() { return this.profilePicName;}
}
