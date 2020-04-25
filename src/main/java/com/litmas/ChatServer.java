package com.litmas;

import com.google.gson.Gson;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint(value = "/websocket/chat")
public class ChatServer {

    static List<ChatServer> clients = new CopyOnWriteArrayList<ChatServer>();
    Session session;

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig){
        this.session = session;
        clients.add(this);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason){
        System.out.println("socket closed: "+ reason.getReasonPhrase());
        clients.remove(this);
    }

    @OnMessage
    public void onMessage(String message){
        message = message.substring(1,message.length()-1).replace("\\","");
        broadcast(message);
    }

    private void broadcast(String message){
        for(ChatServer client: clients){
            try {
                client.session.getBasicRemote().sendText(message);
            } catch (IOException e){
                e.printStackTrace();
                clients.remove(this);
            }
        }
    }

    public static void main(){

    }
}

class JsonMessage{
    private String userName;
    private String message;

    public JsonMessage(String userName, String message) {
        this.userName = userName;
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
