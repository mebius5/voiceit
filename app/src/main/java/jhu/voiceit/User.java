package jhu.voiceit;

/**
 * Created by GradyXiao on 4/22/16.
 */
public class User {
    private String userId;
    private String username;
    private String profilePicName;
    private String email;

    /***
     * Default public constructor
     * Firebase requries an empty public constructor for
     * object representation
     */
    public User(){

    }

    public User(String userId, String username, String profilePicName, String email){
        this.userId = userId;
        this.username = username;
        this.profilePicName = profilePicName;
        this.email = email;
    }

    public String getUserId(){
        return this.userId;
    }

    public String getUsername() { return this.username;}

    public String getProfilePicName() { return this.profilePicName; }

    public void setUsername(String username){
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePicName(String profilePicName){
        this.profilePicName = profilePicName;
    }
}
