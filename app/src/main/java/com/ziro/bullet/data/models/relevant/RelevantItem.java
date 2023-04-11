package com.ziro.bullet.data.models.relevant;

public class RelevantItem {

    private int type;
    private String title;
    private Object object;
    private int pos;
    private float offset;

    public RelevantItem(int type, String title, Object object) {
        this.type = type;
        this.title = title;
        this.object = object;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }
}
