package com.ziro.bullet.utills;

import com.ziro.bullet.R;
import com.ziro.bullet.model.Tabs.DataItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    //    public static final String ADMOB_AD_UNIT_ID = "ca-app-pub-2352871460867862/6641942154";
//    sample id
    public static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";
    //    test for video ads
//    public static final String ADMOB_AD_UNIT_ID = "ca-app-pub-2352871460867862/4254593132";
    public static final String MENU_EXTENDED_VIEW = "Extended";
    public static final String INTENT_ACTION_ARTICLE = "article-action-local-broadcast";
    public final static int BOTTOM_TAB_HOME = 0;
    public final static int BOTTOM_TAB_TOPICS = 1;
    public final static int BOTTOM_TAB_SOURCES = 2;
    public final static int BOTTOM_TAB_SEARCH = 3;
    public final static int BOTTOM_TAB_FOLLOWING = 7;
    public final static int BOTTOM_TAB_ACCOUNT = 4;
    public final static int BOTTOM_TAB_OTHER = 5;
    public final static int BOTTOM_TAB_VIDEO = 6;
    public final static int BOTTOM_TAB_COMMUNITY_FEED = 7;
    public final static Long DISCOVER_REFRESH_DELAY = 5000 * 60L;
    public final static String ACTION_UPDATE_EVENT = "ACTION_UPDATE_EVENT";
    public final static String ACTION_UPLOAD_SERVICE_EVENT = "ACTION_UPLOAD_SERVICE_EVENT";
    public static final String CreateChannel = "Create";
    public static final String MoreChannel = "more";
    public static final String AUTO = "Auto";
    public static final String DARK = "Dark";
    public static final String LIGHT = "Light";
    public static final String PRIMARY_LANGUAGE = "primary";
    public static final String SECONDARY_LANGUAGE = "secondary";
    public static final String REGION = "region";
    public static final int RTL_LANGUAGE = 0;
    public static final int LTR_LANGUAGE = 1;
    public static final int MENU_CHANNELS = 0;
    public static final int POST_TO_CHANNELS = 1;
    public static final String ARTICLE_DRAFT = "DRAFT";
    public static final String ARTICLE_SCHEDULED = "SCHEDULED";
    public static final String ARTICLE_PROCESSING = "PROCESSING";
    public static final String ARTICLE_PUBLISHED = "PUBLISHED";
    public static final String ARTICLE_UNPUBLISHED = "UNPUBLISHED";
    public static final String REELS_FOR_YOU = "FOR_YOU";
    public static final String REELS_FOLLOWING = "FOLLOWING";
    public static final String REELS_COMMUNITY = "COMMUNITY";
    public static final String CAT_TYPE_REELS = "reels";
    public static final String CAT_TYPE_ARTICLES = "articles";
    public static final String FROM_HOME = "from_home";
    /* Dont Change Any Value */
    public static String EXTENDED = "EXTENDED";
    public static String LIST = "LIST";
    public static String HEADLINES_AND_BULLETS = "HEADLINES_AND_BULLETS";
    /* Dont Change Any Value */
    public static String HEADLINES_ONLY = "HEADLINES_ONLY";
    public static boolean firstTime = true;
    public static boolean auto_scroll = true;
    public static float reading_speed = 1.0f;
    public static String file_format = "wav";
    public static boolean isHeader = false;
    public static int collapse_size = 1700;
    public static int expand_size = 1000;
    public static double standard_device_height = 2900.0;
    public static boolean isApiCalling = false;
    public static int ProgressBarDrawable = R.drawable.bullet_selector;
    public static int ProgressBarBackDrawable = R.drawable.bullet_selector_2;
    public static boolean isTopicDataChange = false;
    public static boolean isSourceDataChange = false;
    public static String comingFromFragment = "topic";
    public static int HomeSelectedFragment = BOTTOM_TAB_HOME;
    public static boolean muted = true;
    public static boolean mute_disable = false;
    public static boolean canAudioPlay = false;
    public static String fragTag = "";
    public static String articleId = "";
    public static String speech = "";
    public static String url = "";
    public static int bullet_position = -1;
    public static long bullet_duration = 0;
    public static int visiblePageHome = 0;
    public static int visiblePageHomeDetails = 0;
    public static int visiblePage = 0;
    //String is FileName, Integer is StreamId of loaded File
    public static ArrayList<HashMap<String, Integer>> loadedFiles = new ArrayList<>();
    public static Map<Float, Float> READING_SPEED_RATES = new HashMap<Float, Float>() {{
        put(0.50f, 0.8f);
        put(0.75f, 0.9f);
        put(1.0f, 1.0f);
        put(1.5f, 1.1f);
        put(2.0f, 1.2f);
    }};
    public static int refreshTime = 2;
    public static int updatedvol;
    public static int targetWidth = 400;
    public static int targetHeight = 0;
    public static int rvm = 0;
    public static int mProgressCollapseSize = 13;
    public static int mProgressExpandSize = 100;
    public static int mProgressAnimationSpeed = 200;
    public static boolean isHomeClicked = false;
    public static boolean mViewRecycled = false;
    public static boolean ReelMute = false;
    public static boolean Volume = true;
    public static boolean ReelsVolume = true;
    public static boolean fromfragment = true;
    public static boolean viewsource = false;
    public static boolean reelfragment = false;
    public static boolean onResumeReels = true;
    public static boolean notishare = false;
    public static boolean rvmdailogopen = false;
    public static boolean foryousheet = false;
    public static boolean sharePgNotVisible = true;
    public static boolean homeDataUpdate = false;
    public static boolean menuDataUpdate = false;
    public static boolean reelDataUpdate = false;
    public static boolean onFollowingChanges = false;
    public static String saveLanguageId = "";
    public static int CommentsRequestCode = 313;
    public static int FollowRequestCode = 546;
    public static int VideoDurationRequestCode = 543;
    public static String locationStatusChanged = null;
    public static String topicsStatusChanged = null;
    public static String sourceStatusChanged = null;
    public static int itemPosition = -1;
    public static String followStatus = null;
    public static DataItem articleTabItem = null;
    public static boolean isFontSizeUpdated = false;
    public static boolean updateDiscover = false;

//    public static class Events {
//        public static final String ARTICLE_VIDEO_COMPLETE = "ARTICLE_VIDEO_COMPLETE";
//        public static final String REEL_VIDEO_COMPLETE = "REEL_COMPLETE";
//        public static final String ARTICLE_VIEW = "ARTICLE_VIEW";
//        public static final String ARTICLE_DETAIL_PAGE = "ARTICLE_DETAIL_PAGE";
//        public static final String ARTICLE_SWIPE = "ARTICLE_SWIPE";
//        public static final String REEL_VIEW = "REEL_VIEW";
//        public static final String MENU_PAGE_CLICK = "MENU_PAGE_CLICK";
//        public static final String ARTICLE_DURATION = "ARTICLE_DURATION";
//        public static final String REEL_DURATION = "REEL_DURATION";
//        public static final String UNFOLLOWED_LOCATION = "unfollowed_location";
//        public static final String FOLLOW_LOCATION = "follow_location";
//        public static final String MUTE = "mute";
//        public static final String UNMUTE = "unmute";
//        public static final String CHANGE_EMAIL = "change_email";
//        public static final String CHANGE_PASSWORD = "change_password";
//        public static final String SWIPE_NEXT = "swipe_next";
//        public static final String REG_SELECT_EDITION = "reg_select_edition";
//        public static final String FACEBOOK_SIGNUP = "facebook_signup";
//        public static final String GOOGLE_SIGNUP = "google_signup";
//        public static final String REG_SELECT_LANGUAGE = "reg_select_language";
//        public static final String HOME_PAGE_CLICK = "home_page_click";
//        public static final String SEARCH_PAGE_CLICK = "search_page_click";
//        public static final String NEWSREELS_PAGE_CLICK = "newsreels_page_click";
//        public static final String ABOUT_CLICK = "about_click";
//        public static final String REG_SELECT_SOURCE = "reg_select_source";
//        public static final String UNFOLLOWED_SOURCE = "unfollowed_source";
//        public static final String FOLLOW_SOURCE = "follow_source";
//        public static final String APP_OPEN = "app_open";
//        public static final String DEEPLINK_OPEN = "deeplink_open";
//        public static final String NOTIFICATION_OPEN = "notification_open";
//        public static final String CF_REPORT_CLICK = "cf_report_click";
//        public static final String SHARE_CLICK = "share_click";
//        public static final String ARTICLE_OPEN = "article_open";
//        public static final String ARTICLE_SWIPE_RIGHT = "article_swipe_right";
//        public static final String ARTICLE_SWIPE_LEFT = "article_swipe_left";
//        public static final String UPLOAD_YOUTUBE_LINK_CLICK = "upload_youtube_link_click";
//        public static final String UPLOAD_NEWSREELS_CLICK = "upload_newsreels_click";
//        public static final String UPLOAD_MEDIA_CLICK = "upload_media_click";
//        public static final String ARCHIVE_CLICK = "archive_click";
//        public static final String MORE_LIKE_THIS_CLICK = "more_like_this_click";
//        public static final String LESS_LIKE_THIS_CLICK = "less_like_this_click";
//        public static final String BLOCK_SOURCE = "block_source";
//        public static final String REPORT_CLICK = "report_click";
//        public static final String AUTO_MODE = "auto_mode";
//        public static final String LIGHT_MODE = "light_mode";
//        public static final String DARK_MODE = "dark_mode";
//        public static final String LOGOUT = "logout";
//        public static final String SIGNIN_CLICK = "signin_click";
//        public static final String SIGNUP_CLICK = "signup_click";
//        public static final String UNBLOCK_TOPIC = "unblock_topic";
//        public static final String UNFOLLOWED_TOPIC = "unfollowed_topic";
//        public static final String FOLLOW_TOPIC = "follow_topic";
//        public static final String BLOCK_TOPIC = "block_topic";
//        public static final String LIST_NEXT_AUTO = "list_next_auto";
//        public static final String LIST_NEXT_SCROLL = "list_next_scroll";
//        public static final String REEL_SHARE_CLICK = "reel_share_click";
//        public static final String READING_SPEED_CLICK = "reading_speed_click";
//        public static final String HEADLINES_ONLY = "headlines_only";
//        public static final String HELP_CENTER = "help_center";
//        public static final String TUTORIAL_FINISH = "tutorial_finish";
//        public static final String REG_SELECT_TOPIC = "reg_select_topic";
//        public static final String UNBLOCK_SOURCE = "unblock_source";
//        public static final String TERMS_CLICK = "terms_click";
//        public static final String POLICY_CLICK = "policy_click";
//        public static final String UNBLOCK_AUTHOR = "unblock_author";
//        public static final String HEADLINES_BULLETS = "headlines_bullets";
//        public static final String TRENDING_OPEN = "trending_open";
//        public static final String SOURCE_OPEN = "source_open";
//        public static final String PUSH_CLICKS = "push_clicks";
//        public static final String TOPIC_OPEN = "topic_open";
//        public static final String FEED_OPEN = "feed_open";
//        public static final String LOCATION_OPEN = "location_open";
//        public static final String SAVED_ARTICLE = "saved_article";
//        public static final String WIDGET_OPEN = "widget_open";
//        public static final String WIDGET_MORE_LIKE_THIS = "widget_more_like_this";
//        public static final String UNFOLLOW_TOPIC = "unfollow_topic";
//    }

    public static class SOCIAL_LINKS {
        public static final String FB = "https://www.facebook.com/newsreels.india";
        public static final String FB_PAGE = "fb://page/100980738491568";
        public static final String IG = "https://www.instagram.com/newsreels.india/";
        public static final String YOUTUBE = "https://www.youtube.com/channel/UCqrv55WJ0UP7jSwBI8gt13w";
        public static final String TWITTER = "https://twitter.com/newsreelsindia";
        public static final String TIKTOK = "https://www.tiktok.com/@newsreels.india?lang=en";
        public static final String LINKEDIN = "https://www.linkedin.com/company/69500680/admin/";
    }
}
