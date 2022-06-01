package com.lemon.catacombs.objects.ui.cutscenes.opening;

public class TextEvent {
    final int x, y;
    final int triggerTime;
    final String text;
    final int duration;

    public TextEvent(int x, int y, int triggerTime, String text, int duration) {
        this.x = x;
        this.y = y;
        this.triggerTime = triggerTime;
        this.text = text;
        this.duration = duration;
    }
}
