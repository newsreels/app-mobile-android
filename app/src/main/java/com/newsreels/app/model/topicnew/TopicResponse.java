
package com.newsreels.app.model.topicnew;


import java.util.ArrayList;
import java.util.List;

import com.newsreels.app.APIResources.Meta;
import com.newsreels.app.data.models.topics.Topics;


public class TopicResponse {
    private Meta meta;
    private List<Topics> topics = new ArrayList<Topics>();
    public Meta getMeta() {
        return meta;
    }
    public void setMeta(Meta meta) {
        this.meta = meta;
    }
    public List<Topics> getTopics() {
        return topics;
    }
    public void setTopics(List<Topics> topics) {
        this.topics = topics;
    }
}



