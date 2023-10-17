# VadBot 3
VadBot is an ongoing project, having been in development since February 2021. It is a Discord Bot specifically tailored to me and my friends' Discord server, gif spam central.

VadBot began as a discord.js project, coded entirely in JavaScript via Node.js. I did not have a thorough understanding of programming and efficiency though, so a lot of it was inefficient and built without understanding more advanced concepts like asynchronous functions and process control. This VadBot was VadBot 1. I attempted to rebuild VadBot in discord.js from scratch once I had learned more about the language through development of VadBot 1, but it did not get very far-- this was VadBot 2.

VadBot 3 was a result of me returning from my first semester of college with a very thorough and comprehensive understanding of Java, including inheritance, generics, data structures, and nested classes. I rebuilt VadBot from the ground up in Java, basing it off of VadBot 1's functionality, but creating improvements in efficiency along the way. As such, VadBot 3 is a much more polished embodiment of VadBot. 

Here are some highlights of VadBot 3's features.

## Remote Database
VadBot 3 uses a remote database to store information. In development of this database, I learned the basics of SQL and wrote an entire class that abstracts SQL queries for accessing the database, [Database.java](src/main/java/vadbot/Database.java).

The database is used to store every member's current nickname, roles, and experience (discussed in the next section). This way, the bot can save the information between periods of being offline, but if a member leaves the server and comes back, their roles and nickname can be automatically added back as soon as they return. The database updates in real time with any change that is made to a member's roles or nickname. 

## Rank System
VadBot 3 maintains a running experience system to incentivize participation in the server. Every time a person sends a message or uses a command, they are given a small amount of "experience points". The system also maintains a level system, where members "level up" once they reach a certain amount of experience. The distance between levels is quadratically increasing, so higher levels are harder and harder to achieve. The system also ranks members based on their experience; the higher the experience total, the higher their rank among others. The rank system does not have any rewards other than bragging rights, but just serves as a fun system for people to engage with within the server.

## Music Player Capabilities
VadBot 3 is able to play music in a Discord voice channel based on a query. Members can provide VadBot with a search query or YouTube link, and VadBot will search YouTube and play the audio of the requested song or video. It maintains a running queue of songs to play, which can be shuffled or cleared with commands. It has functionality to pause, play, skip, and loop tracks via commands, and can display information about the currently playing track as well.

## Birthday System
A recent feature, VadBot 3 has the ability to store member's birthdays (if they choose to provide them), and then on their birthday, announce a "Happy Birthday!" to them at midnight for everyone to see. On top of this, there are a few commands in place to interact with the birthday system; there is an algorithm that will return who has the closest upcoming birthday, a command to get a provided member's birthday, and a command to set and delete your own birthday from the database. 

## Commands
VadBot 3 has a large array of commands that people can use in the server. Some of these include:
- **Fun Commands**: VadBot can simulate coin flips, generate random quotes from VALORANT characters, choose a random thing from a provided list of things, and more
- **Utility Commands**: VadBot can return a full image of a provided user's avatar, generate a summary profile of a provided user, and for moderators, can kick or ban members if needed.
- **Rank System Commands**: VadBot has commands for interacting with the rank system, one for seeing your own rank and distance to the next level, and one to see the entire ranked list of server members.
- **Music Commands**: VadBot has commands for working with the music bot as described above.
- **Command List**: VadBot maintains a categorized command list for anyone who wants to familiarize themselves with VadBot's commands. 

VadBot also maintains a very efficient and clean command registry for making new commands on the back end. For me to implement a new command, all I have to do is write out the basic design of the command: the title, description, and parameters. The system I designed will automatically do the rest of the work for me-- structuring the command and registering the command with Discord and generating an entry in the command list. Additionally, each command is implemented via its own class, and each class implements a common interface to handle the general listening for and execution of commands, to abstract away the need to reimplement the event handlers every time. 

## Currently in Development
My biggest undertaking yet for this bot, I am implementing interaction with the Spotify and Last.fm APIs. Currently, you can authenticate the bot with both Spotify and Last.fm to give VadBot access to your music listening data. The Spotify API can build playlists and generate music recommendations, and I intend to develop some sort of functionality with that in the future. 

I have been mainly working with the Last.fm API to analyze music data. The current function I'm working on is a command that will take a member's currently playing artist or track, then look at their entire music history and filter the data down to just that artist or track, then generate a chart showing how many times that person has listened to that artist or track each month over the course of their history, so they can see the trends of their listening patterns for that artist or track. Since accessing the API data is slow, the bot will store all music data on the first time it is accessed and use the data in future analyses. 

Entirely just through this development, I have learned about HTTP requests (because I needed to use them for authentication with the APIs), query parameters, redirect URIs, listening for incoming requests, and general API security measures as a whole, and successfully and securely designed the authentication functions.
