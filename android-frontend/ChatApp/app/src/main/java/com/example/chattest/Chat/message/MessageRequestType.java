package com.example.chattest.Chat.message;

public class MessageRequestType {
    public String text;
    public MessageRequestType(){

    }
    public MessageRequestType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
