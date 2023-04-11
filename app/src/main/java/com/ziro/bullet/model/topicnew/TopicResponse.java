
package com.ziro.bullet.model.topicnew;


import java.util.ArrayList;
import java.util.List;

import com.ziro.bullet.APIResources.Meta;
import com.ziro.bullet.data.models.topics.Topics;


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



