##VoiceIt: A Music social media application.

VoiceIt is an Android application that allows user to record, discover, and share 30-second sound clips. 

VoiceIt was built by Leonardo Fontes, Seung Jae Paik, Sarah Sukardi, and Grady Xiao for Johns Hopkins' Spring 2016 User Interfaces & Mobile Applications class.

Features:

There are only three activities for this application:
Login Activity
Register Activity
Main Activity

Main Activity contains the toolbar, action overflow bar, and the button tabs. <br>
These elements can be effectively reused by the fragments contained within MainActivity.

Fragments (w. Neat Features):

HomeFeedFragment: (Shows the posts of all users)
<ul>
  <li> Uses the Firebase Recycler View to automatically update the list whenever a post is changed, added, or deleted
  <li> Calculates the timestamp of the post automatically every single time you pull up homefeed or when a child is added
    <ul>
      <li>The timestamp is users relative time lapse from the creation of the post till now.
      <li>It is divided into category of day (d), hour (h), minute (m), and second (s).
      So if the post is 1 day and 80 minutes old, the timestamp will show up as 1d only.
    </ul>
  <li> Each post allows user to play the post, like the opst, or delete your own post.
    <ul>
      <li> When the user plays the post, the play button automatically changes to pause button.
      Upon completion of the playback, the pause button resets to a play button. 
      <li> When the user likes a post, the heart button turns yellow, and the like increments. <br>
      A user can also undo his likes by tapping the like button again. Then the color resets and the count decrements. <br>
      Notice that a user can only like a post once. <br>
      <li> 
    </ul>
</ul>



