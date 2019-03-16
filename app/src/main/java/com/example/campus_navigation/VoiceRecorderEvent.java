package com.example.campus_navigation;

public class VoiceRecorderEvent{
    private boolean isClick;

    public  VoiceRecorderEvent(boolean isClick){
        this.isClick=isClick;
    }

    public boolean getMessage() {
        return isClick;
    }

    public void setMessage(boolean isClick) {
        this.isClick = isClick;
    }
}
