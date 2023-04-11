package com.ziro.bullet.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ziro.bullet.R;

public class ShareBottomSheetView {

//    private RecyclerView list;
//    private ImageView img;
//    private TextView title;
//    private TextView source;

//    private RelativeLayout save;
//    private ImageView saveIcon;
//    private ProgressBar saveProgress;

    private TextView source1;
    private TextView source2;
    private TextView source3;

    private RelativeLayout more;
    private ImageView moreIcon;
    private ProgressBar moreProgress;

    private RelativeLayout less;
    private ImageView lessIcon;
    private ProgressBar lessProgress;

    private RelativeLayout gotoChannel;
    private ImageView gotoIcon;
    private ProgressBar gotoProgress;

    private RelativeLayout block;
    private ImageView blockIcon;
    private ProgressBar blockProgress;

    private RelativeLayout follow;
    private ImageView followIcon;
    private ProgressBar followProgress;

    private RelativeLayout share;
    private ImageView shareIcon;
    private ProgressBar shareProgress;

    private RelativeLayout report;
    private ImageView reportIcon;
    private ProgressBar reportProgress;

    private RelativeLayout archive;
    private ImageView archiveIcon;
    private ProgressBar archiveProgress;
    private TextView archiveText;

    private RelativeLayout cancel;
    private RelativeLayout copy;
    private RelativeLayout caption;
    private TextView captionText;

    public ShareBottomSheetView(View dialogView) {

//        list = dialogView.findViewById(R.id.list);
//        img = dialogView.findViewById(R.id.img);
//        title = dialogView.findViewById(R.id.title);
//        source = dialogView.findViewById(R.id.source);

        copy = dialogView.findViewById(R.id.copy);
        cancel = dialogView.findViewById(R.id.cancel);
        share = dialogView.findViewById(R.id.share);
        shareIcon = dialogView.findViewById(R.id.shareIcon);
        shareProgress = dialogView.findViewById(R.id.shareProgress);

//        save = dialogView.findViewById(R.id.save);
//        saveIcon = dialogView.findViewById(R.id.saveIcon);
//        saveProgress = dialogView.findViewById(R.id.saveProgress);

        more = dialogView.findViewById(R.id.more);
        moreIcon = dialogView.findViewById(R.id.moreIcon);
        moreProgress = dialogView.findViewById(R.id.moreProgress);

        less = dialogView.findViewById(R.id.less);
        lessIcon = dialogView.findViewById(R.id.lessIcon);
        lessProgress = dialogView.findViewById(R.id.lessProgress);

        gotoChannel = dialogView.findViewById(R.id.gotoChannel);
        gotoIcon = dialogView.findViewById(R.id.gotoIcon);
        gotoProgress = dialogView.findViewById(R.id.gotoProgress);

        block = dialogView.findViewById(R.id.block);
        blockIcon = dialogView.findViewById(R.id.blockIcon);
        blockProgress = dialogView.findViewById(R.id.blockProgress);

        follow = dialogView.findViewById(R.id.follow);
        followIcon = dialogView.findViewById(R.id.followIcon);
        followProgress = dialogView.findViewById(R.id.followProgress);

        report = dialogView.findViewById(R.id.report);
        reportIcon = dialogView.findViewById(R.id.reportIcon);
        reportProgress = dialogView.findViewById(R.id.reportProgress);

        archive = dialogView.findViewById(R.id.archive);
        archiveIcon = dialogView.findViewById(R.id.archiveIcon);
        archiveProgress = dialogView.findViewById(R.id.archiveProgress);
        archiveText = dialogView.findViewById(R.id.archiveText);

        source1 = dialogView.findViewById(R.id.source1);
        source2 = dialogView.findViewById(R.id.source2);
        source3 = dialogView.findViewById(R.id.source3);

        caption = dialogView.findViewById(R.id.caption);
        captionText = dialogView.findViewById(R.id.captionText);

    }

    public TextView getSource1() {
        return source1;
    }

    public TextView getSource2() {
        return source2;
    }

    public TextView getSource3() {
        return source3;
    }

    public TextView getArchiveText() {
        return archiveText;
    }

//    public RecyclerView getList() {
//        return list;
//    }
//
//    public ImageView getImg() {
//        return img;
//    }
//
//    public TextView getTitle() {
//        return title;
//    }
//
//    public TextView getSource() {
//        return source;
//    }

//    public RelativeLayout getSave() {
//        return save;
//    }
//
//    public ImageView getSaveIcon() {
//        return saveIcon;
//    }
//
//    public ProgressBar getSaveProgress() {
//        return saveProgress;
//    }

    public RelativeLayout getMore() {
        return more;
    }

    public ImageView getMoreIcon() {
        return moreIcon;
    }

    public ProgressBar getMoreProgress() {
        return moreProgress;
    }

    public RelativeLayout getLess() {
        return less;
    }

    public ImageView getLessIcon() {
        return lessIcon;
    }

    public ProgressBar getLessProgress() {
        return lessProgress;
    }

    public RelativeLayout getGotoChannel() {
        return gotoChannel;
    }

    public ImageView getGotoIcon() {
        return gotoIcon;
    }

    public ProgressBar getGotoProgress() {
        return gotoProgress;
    }

    public RelativeLayout getBlock() {
        return block;
    }

    public ImageView getBlockIcon() {
        return blockIcon;
    }

    public ProgressBar getBlockProgress() {
        return blockProgress;
    }

    public RelativeLayout getFollow() {
        return follow;
    }

    public ImageView getFollowIcon() {
        return followIcon;
    }

    public ProgressBar getFollowProgress() {
        return followProgress;
    }

    public RelativeLayout getReport() {
        return report;
    }

    public ImageView getReportIcon() {
        return reportIcon;
    }

    public ProgressBar getReportProgress() {
        return reportProgress;
    }

    public RelativeLayout getShare() {
        return share;
    }

    public ImageView getShareIcon() {
        return shareIcon;
    }

    public ProgressBar getShareProgress() {
        return shareProgress;
    }

    public RelativeLayout getArchive() {
        return archive;
    }

    public ImageView getArchiveIcon() {
        return archiveIcon;
    }

    public ProgressBar getArchiveProgress() {
        return archiveProgress;
    }

    public RelativeLayout getCaption() {
        return caption;
    }

    public TextView getCaptionText() {
        return captionText;
    }

    public RelativeLayout getCancel() {
        return cancel;
    }

    public RelativeLayout getCopy() {
        return copy;
    }
}
