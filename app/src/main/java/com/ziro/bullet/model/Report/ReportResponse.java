
package com.ziro.bullet.model.Report;

import java.util.ArrayList;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ReportResponse {

    @SerializedName("concerns")
    private ArrayList<String> mConcerns;

    public ArrayList<String> getConcerns() {
        return mConcerns;
    }

    public void setConcerns(ArrayList<String> concerns) {
        mConcerns = concerns;
    }

}
