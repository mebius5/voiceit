#VoiceIt: A Music social media application

##The Gist
VoiceIt is an Android application that allows user to record, discover, and share 30-second sound clips.

##The Developers
VoiceIt was built by <a href="https://github.com/leofontes">Leonardo Fontes</a>, <a href="https://github.com/sjp511">Seung Jae Paik</a>,
<a href="https://github.com/smsukardi"> Sarah Sukardi</a>, and <a href="https://github.com/mebius5">Grady Xiao</a> for Johns Hopkins' <a href="http://www.cs.jhu.edu/~joanne/cs250/">Spring 2016 User Interfaces & Mobile Applications</a> class.

##The Premise

Users create clips, recording directly through the device, which has a 30-second countdown timer that stops after 30 seconds have elapsed.
Their media posts then appear on a news feed where they can view them in reverse chronological order.
Users can then like others' media posts and delete only their own posts. They can also change their profile picture, their account name, as well as change and delete their password.

##Features

There are only three activities for this application:
<ul>
  <li> Login Activity
  <li> Register Activity
  <li> Main Activity
</ul>
##Activities

###MainActivity:
Main Activity contains the toolbar, action overflow bar, and the button tabs. <br>
These elements can be effectively reused by the fragments contained within MainActivity.
MainActivity provides the parent for many of the fragment screns used within the app.
###LoginActivity:
Login Activity provides the first screen a user sees upon installing the app: a screen where they can log in to an existing account
or create a new account.
###RegisterActivity:
If the user has not made an account, they can register for a new account through this screen.

##Fragments (w. Neat Features)

###HomeFeedFragment: (Shows the posts of all users)
<ul>
  <li> Uses the Firebase Recycler View to automatically update the list whenever a post is changed, added, or deleted. Notice that each post is displayed in reverse-chronological order.
  <li> Calculates the timestamp of the post automatically every single time you pull up homefeed or when a child is added
    <ul>
      <li>The timestamp is users relative time lapse from the creation of the post till now.
      <li>It is divided into category of day (d), hour (h), minute (m), and second (s).
      So if the post is 1 day and 80 minutes old, the timestamp will show up as 1d only.
    </ul>
  <li> Each post allows user to see other users profile page, play the post, like the opst, or delete your own post.
    <ul>
      <li> When the user hits on the profile picture of another user, he is redirected to the other user's profile page.
      <li> When the user plays the post, the play button automatically changes to pause button.
      Upon completion of the playback, the pause button resets to a play button. 
      <li> When the user likes a post, the heart button turns yellow, and the like increments. <br>
      A user can also undo his likes by tapping the like button again. Then the color resets and the count decrements. <br>
      Notice that a user can only like a post once. <br>
      <li> A user can only see the trash can icon if the post is his own post. Thus, preventing other users from deleting his posts.
    </ul>
</ul>

###RecordFragment: (Allows users to record and post)
<ul>
  <li> The record button allows user to record audio sound files (limit to 30 sec)
    <ul>
      <li> Due to the restriction of the MediaRecorder, user cannot hit record and stop too fast. Must be a minimum of 1 sec
      <li> The countdown of 30 seconds starts once user hits record
      <li> The user can either hit stop to end record or when the counter hits 0, it automatically stops the recording
    </ul>
  <li> User can select items on the list to play the corresponding recording
  <li> After selecting a recording, the user hits the next arrow to continue to the addition of description
  <li> The description allows user to add texts to describe the post (150 char limit)
  <li> A confirmation toast will pop up to confirm post and redirects user to the homefeed
</ul>

###ProfileFragment: (Sees the user's own post)
<ul>
  <li> Users can view their own posts in reverse chronological order
  <li> The user can like and delete their own posts from their profile
  <li> The user can see their name, as well as their statistics on their page.
</ul>