package com.ziro.bullet.data.models.config;

import com.google.gson.annotations.SerializedName;

public class Narration {

    @SerializedName("muted")
    private boolean muted = false;

    @SerializedName("speed")
    private String readingSpeed;

    @SerializedName("mode")
    private String mode;

    @SerializedName("speed_rate")
    private SpeedRate speedRate;

    public Narration() {
    }

    public Narration(String mode) {
        this.mode = mode;
    }

    public Narration(boolean muted) {
        this.muted = muted;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public String getReadingSpeed() {
        return readingSpeed;
    }

    public void setReadingSpeed(String readingSpeed) {
        this.readingSpeed = readingSpeed;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public SpeedRate getSpeedRate() {
        return speedRate;
    }

    public void setSpeedRate(SpeedRate speedRate) {
        this.speedRate = speedRate;
    }

    public class SpeedRate {
        @SerializedName("1.0x")
        private float speed1x;

        @SerializedName("0.5x")
        private float speed_point5x;

        @SerializedName("0.75x")
        private float speed_point75x;

        @SerializedName("1.5x")
        private float speed1_point5x;

        @SerializedName("2.0x")
        private float speed2x;

        public float getSpeed1x() {
            return speed1x;
        }

        public void setSpeed1x(float speed1x) {
            this.speed1x = speed1x;
        }

        public float getSpeed_point5x() {
            return speed_point5x;
        }

        public void setSpeed_point5x(float speed_point5x) {
            this.speed_point5x = speed_point5x;
        }

        public float getSpeed_point75x() {
            return speed_point75x;
        }

        public void setSpeed_point75x(float speed_point75x) {
            this.speed_point75x = speed_point75x;
        }

        public float getSpeed1_point5x() {
            return speed1_point5x;
        }

        public void setSpeed1_point5x(float speed1_point5x) {
            this.speed1_point5x = speed1_point5x;
        }

        public float getSpeed2x() {
            return speed2x;
        }

        public void setSpeed2x(float speed2x) {
            this.speed2x = speed2x;
        }
    }
}
