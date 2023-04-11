package com.ziro.bullet.data.models.search;

import com.google.gson.annotations.SerializedName;

public class Registration {
    @SerializedName("source")
    private Source source;

    @SerializedName("topic")
    private Topic topic;

    @SerializedName("edition")
    private Edition edition;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Edition getEdition() {
        return edition;
    }

    public void setEdition(Edition edition) {
        this.edition = edition;
    }

    public class Source {
        @SerializedName("done")
        private boolean done;

        @SerializedName("minimum")
        private int minimum;

        public boolean isDone() {
            return done;
        }

        public void setDone(boolean done) {
            this.done = done;
        }

        public int getMinimum() {
            return minimum;
        }

        public void setMinimum(int minimum) {
            this.minimum = minimum;
        }
    }

    public class Topic {
        @SerializedName("done")
        private boolean done;

        @SerializedName("minimum")
        private int minimum;

        public boolean isDone() {
            return done;
        }

        public void setDone(boolean done) {
            this.done = done;
        }

        public int getMinimum() {
            return minimum;
        }

        public void setMinimum(int minimum) {
            this.minimum = minimum;
        }
    }

    public class Edition {
        @SerializedName("done")
        private boolean done;

        @SerializedName("minimum")
        private int minimum;

        public boolean isDone() {
            return done;
        }

        public void setDone(boolean done) {
            this.done = done;
        }

        public int getMinimum() {
            return minimum;
        }

        public void setMinimum(int minimum) {
            this.minimum = minimum;
        }
    }
}
