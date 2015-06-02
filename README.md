# Android Chat App: The Server side.

## Description
This java project is used to set a server, provide a specific port. The connection  
between the client and the server is based on the socket, on the server side,  
utilizing the threadpool provided by Android to deal with the clients' access.  
The information of all online clients is cached on the server, if a message sent  
by one client, then other clients will receive it by cyclic sending from the server.

![](https://raw.githubusercontent.com/insogin/AppChatServer/master/screenshot/Screen%20Shot%202015-06-02%20at%201.21.53%20AM.png)

## Outline of the code
The main function is in the `ServerWindow.java`, the server will be available when  
a port number is entered and click the button, and the execution jumps to `ChatServer.java`  
Once the server startup, it creates a server socket bound to the specified port, which  
waits for requests come from the network. These two files form the main logic of the  
program, the other java files could be seen as auxiliaries of them.
### ServerWindow.java  
The [`ServerWindow.java`](https://github.com/insogin/AppChatServer/blob/master/src/com/csu/client/ServerWindow.java) creates a Swing GUI shown as the following Fig, if a number  
e.g., 8888 is entered, then the corresponding port is provided for the chat app.

### ChatServer.java
The [`ChatServer.java`](https://github.com/insogin/AppChatServer/blob/master/src/com/csu/server/ChatServer.java) impements the activity of the server. The rule of creating and  
killing ServerSocket is defined. There are four basic requests from a user, the new  
client registering, the client login, the client exiting and the regular message send  
by online clients.

### User.java
The [`User.java`](https://github.com/insogin/AppChatServer/blob/master/src/com/csu/bean/User.java) defines a series of methods to manipulate the server ip and port, users' id,  
name, picture and status.

### ContentFlag.java
The [`ContentFlag.java`](https://github.com/insogin/AppChatServer/blob/master/src/com/csu/constant/ContentFlag.java) defines three flags to reflect the status of user, i.e., "online",  
"offline" and "register".

### FormatDate.java
The [`FormatData.java`](https://github.com/insogin/AppChatServer/blob/master/src/com/csu/tool/FormatDate.java) defines the time format, "MM-dd hh:mm".

### StreamTool.java
THe [`StreamTool.java`](https://github.com/insogin/AppChatServer/blob/master/src/com/csu/tool/StreamTool.java) deals with the input and output data streams.

### XmlParser.java
The [`XmlParser.java`](https://github.com/insogin/AppChatServer/blob/master/src/com/csu/tool/XmlParser.java) plays the role as a data base, some important information of users  
is saved, the picture of the user for example. it can create and save the unique id for  
each user, thus the user can be searched through this id.

## Acknowledgement
I would like to give special thanks to Dr. Andy Li, Dr. Kaikai Liu and Dr. Ze Yu (In alphabetical  
order). Specifically, Dr. Li inspired me  interesting of java & Android programming and  
also always tries to help me to form a good habit of learning and programming. Dr. Liu  
and Dr. Yu are always available and willing to give kind help with regarding to my problems  
in the process of my learning.

## Origins
* [Ying Xu. The Chat App based on Android. 2013.](http://download.csdn.net/detail/jiangliloveyou/6457969)

## References
* Schildt, Herbert. Java: A Beginner's Guide Sixth Edition. McGraw-Hill, Inc., 2014.
* [Adam Porter. Programming Mobile Applications for Android Handheld Systems: Part 1](https://class.coursera.org/androidpart1-004)
* [Adam Porter. Programming Mobile Applications for Android Handheld Systems: Part 2](https://class.coursera.org/androidpart2-003)

### Contact me
yangwenjincshn@gmail.com
