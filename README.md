# AppChatServer

## Description
This java project is used to set a server, provide a specific port. The connection  
between the client and the server is based on the socket, on the server side,  
utilizing the threadpool provided by Android to deal with the clients' access.  
The information of all online clients is cached on the server, if a message sent  
by one client, then other clients will receive it by cyclic sending from the server.

![](https://raw.githubusercontent.com/insogin/AppChatServer/master/screenshot/Screen%20Shot%202015-06-02%20at%201.21.53%20AM.png)

## Outline of the code

### ServerWindow.java  
The `ServerWindow.java` creates a Swing GUI shown as the following Fig, if a number  
e.g., 8888 is entered, then the corresponding port is provided for the chat app.

### ChatServer.java
The `ChatServer.java` impements the activity of the server. The rule of creating and  
killing ServerSocket is defined. There are four basic requests from a user, the new  
client registering, the client login, the client exiting and the regular message send  
by online clients.

### User.java
The `User.java` defines a series of methods to manipulate the server ip and port, users' 
id, name, picture and status.

### ContentFlag.java
The `ContentFlag.java` defines three flags to reflect the status of user, i.e., "online",  
"offline" and "register".

### FormatDate.java
The `FormatData.java` defines the time format, "MM-dd hh:mm".

### StreamTool.java
THe `StreamTool.java` deals with the input and output data streams.

### XmlParser.java
The `XmlParser.java` plays the role as a data base, some important information of users  
is saved, the picture of the user for example. it can create and save the unique id for  
each user, thus the user can be searched through this id.

