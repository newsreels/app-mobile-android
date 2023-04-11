package com.ziro.bullet.data.models;

import com.google.gson.annotations.SerializedName;

public class BaseModel {

    @SerializedName("meta")
    public Meta meta;

    @SerializedName("message")
    public String message;
    @SerializedName("suggested_reels")
    private int suggested_reels = -1;
    @SerializedName("suggested_authors")
    private int suggested_authors = -1;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public int getSuggested_reels() {
        return suggested_reels;
    }

    public void setSuggested_reels(int suggested_reels) {
        this.suggested_reels = suggested_reels;
    }

    public int getSuggested_authors() {
        return suggested_authors;
    }

    public void setSuggested_authors(int suggested_authors) {
        this.suggested_authors = suggested_authors;
    }

    public static class Meta {
        @SerializedName("next")
        private String next;

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

//        @SerializedName("total_record")
//        private int totalRecord;
//
//        @SerializedName("total_page")
//        private int totalPage;
//
//        @SerializedName("offset")
//        private int offset;
//
//        @SerializedName("limit")
//        private int limit;
//
//        @SerializedName("page")
//        private int page;
//
//        @SerializedName("prev_page")
//        private int prevPage;
//
//        @SerializedName("next_page")
//        private int nextPage;
//
//        public int getTotalRecord() {
//            return totalRecord;
//        }
//
//        public int getTotalPage() {
//            return totalPage;
//        }
//
//        public int getOffset() {
//            return offset;
//        }
//
//        public int getLimit() {
//            return limit;
//        }
//
//        public int getPage() {
//            return page;
//        }
//
//        public int getPrevPage() {
//            return prevPage;
//        }
//
//        public int getNextPage() {
//            return nextPage;
//        }
    }
}
