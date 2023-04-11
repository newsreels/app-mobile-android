
package com.ziro.bullet.model.articles;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Meta {

    @SerializedName("limit")
    private int mLimit;
    @SerializedName("next_page")
    private int mNextPage;
    @SerializedName("offset")
    private int mOffset;
    @SerializedName("page")
    private int mPage;
    @SerializedName("prev_page")
    private int mPrevPage;
    @SerializedName("total_page")
    private int mTotalPage;
    @SerializedName("total_record")
    private int mTotalRecord;

    public int getLimit() {
        return mLimit;
    }

    public void setLimit(int limit) {
        mLimit = limit;
    }

    public int getNextPage() {
        return mNextPage;
    }

    public void setNextPage(int nextPage) {
        mNextPage = nextPage;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setOffset(int offset) {
        mOffset = offset;
    }

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        mPage = page;
    }

    public int getPrevPage() {
        return mPrevPage;
    }

    public void setPrevPage(int prevPage) {
        mPrevPage = prevPage;
    }

    public int getTotalPage() {
        return mTotalPage;
    }

    public void setTotalPage(int totalPage) {
        mTotalPage = totalPage;
    }

    public int getTotalRecord() {
        return mTotalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        mTotalRecord = totalRecord;
    }

}
