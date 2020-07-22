package com.example.lastminute.Settings;

public class NotificationSettingsDetails {
    private boolean enabled;
    private int hour;
    private int minute;

    public NotificationSettingsDetails(boolean enabled, int hour, int minute) {
        this.enabled = enabled;
        this.hour = hour;
        this.minute = minute;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @Override
    public String toString() {
        return "NotificationSettingsDetails{" +
                "enabled=" + enabled +
                ", hour=" + hour +
                ", minute=" + minute +
                '}';
    }
}
