package com.ziro.bullet.data.models.search;

import com.google.gson.annotations.SerializedName;

public class Search {
    @SerializedName("id")
    private String id;


    @SerializedName("name")
    private String name;

    @SerializedName("icon")
    private String icon;

    @SerializedName("image")
    private String image;

    @SerializedName("color")
    private String color;

    @SerializedName("link")
    private String link;

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @SerializedName("favorite")
    private boolean favorite;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Search) {
            Search search = (Search) other;
            return id.equals(search.getId()) &&
                    name != null && name.equals(search.getName()) &&
                    icon != null && icon.equals(search.getIcon()) &&
                    image != null && image.equals(search.getImage()) &&
                    color != null && color.equals(search.getColor()) &&
                    link != null && link.equals(search.getLink()) &&
                    type != null && type.equals(search.getType()) &&
                    favorite == search.isFavorite();
        } else {
            return false;
        }
    }
}
