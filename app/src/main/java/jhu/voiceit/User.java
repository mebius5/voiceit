package jhu.voiceit;

/**
 * Created by GradyXiao on 4/22/16.
 */
public class User {
    private String userId;
    private String username;
    private String profilePicName;
    private String email;
    private long numPosts;


    /***
     * Default public constructor
     * Firebase requries an empty public constructor for
     * object representation
     */
    public User(){

    }

    public User(String userId, String username, String profilePicName, String email, long numPosts){
        this.userId = userId;
        this.username = username;
        this.profilePicName = profilePicName;
        this.email = email;
        this.numPosts = numPosts;
    }

    public String getUserId(){
        return this.userId;
    }

    public String getUsername() { return this.username;}

    public String getProfilePicName() { return this.profilePicName; }

    public void setUsername(String username){
        this.username = username;
    }

    public long getNumPosts() {
        return numPosts;
    }

    public void setNumPosts(long num) {
        this.numPosts = num;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
