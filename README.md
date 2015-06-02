# AppChatServer

## Description
This java project is used to set a server, provide a specific port. The connection  
between the client and the server is based on the socket, on the server side,  
utilizing the threadpool provided by Android to deal with the clients' access.  
The information of all online clients is cached on the server, if a message sent  
by one client, then other clients will receive it by cyclic sending from the server.

![](https://raw.githubusercontent.com/insogin/AppChatServer/master/screenshot/Screen%20Shot%202015-06-02%20at%201.21.53%20AM.png)

## 
Specifically, the `ServerWindow.java`  
creates a Swing GUI shown as the following Fig, if a number e.g., 8888 is entered, then the  
corresponding port is provided for the chat app. 
