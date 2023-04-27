package com.ziro.bullet.APIResources;


/**
 * This is Retrofit API Interface to call the API endpoints
 */

import com.ziro.bullet.data.SingleArticle;
import com.ziro.bullet.data.caption.CaptionResponse;
import com.ziro.bullet.data.dataclass.ContentLanguageResponse;
import com.ziro.bullet.data.dataclass.OnBoardingModel;
import com.ziro.bullet.data.dataclass.RegionResponse;
import com.ziro.bullet.data.dataclass.SaveOnBoardingModel;
import com.ziro.bullet.data.models.AuthorListResponse;
import com.ziro.bullet.data.models.BaseModel;
import com.ziro.bullet.data.models.CategorySequenceModel;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.data.models.author.AuthorSearchResponse;
import com.ziro.bullet.data.models.channels.UpdateChannelResponse;
import com.ziro.bullet.data.models.config.UserConfigModel;
import com.ziro.bullet.data.models.home.HomeModel;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.postarticle.LocationResponse;
import com.ziro.bullet.data.models.postarticle.TagsResponse;
import com.ziro.bullet.data.models.push.PushResponse;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.data.models.search.SearchSource;
import com.ziro.bullet.data.models.search.SearchTopic;
import com.ziro.bullet.data.models.sources.Categories;
import com.ziro.bullet.data.models.sources.CategoryPaginationModel;
import com.ziro.bullet.data.models.sources.Info;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.suggestions.SuggestionsModel;
import com.ziro.bullet.data.models.topics.TopicsModel;
import com.ziro.bullet.data.models.userInfo.UserInfoModel;
import com.ziro.bullet.model.AuthorResponse;
import com.ziro.bullet.model.CategorizedChannels;
import com.ziro.bullet.model.ChannelDetails;
import com.ziro.bullet.model.Edition.ResponseEdition;
import com.ziro.bullet.model.FollowResponse;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.model.NewDiscoverPage.NewDiscoverResponse;
import com.ziro.bullet.model.News.Category;
import com.ziro.bullet.model.News.NewsResponse;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.model.Report.ReportResponse;
import com.ziro.bullet.model.ShareBottomSheetResponse;
import com.ziro.bullet.model.TCP.TCPResponse;
import com.ziro.bullet.model.UserChannels;
import com.ziro.bullet.model.articlenew.ArticleBase;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.articles.ForYou;
import com.ziro.bullet.model.comment.CommentCounterResponse;
import com.ziro.bullet.model.comment.CommentResponse;
import com.ziro.bullet.model.discoverNew.DiscoverDetailsResponse;
import com.ziro.bullet.model.discoverNew.DiscoverNewResponse;
import com.ziro.bullet.model.discoverNew.liveScore.LiveScoreApiResponse;
import com.ziro.bullet.model.discoverNew.scorecard.ScorecardResponse;
import com.ziro.bullet.model.discoverNew.sportsteam.SportTeam;
import com.ziro.bullet.model.discoverNew.spotsinfo.SportsInfoResponse;
import com.ziro.bullet.model.discoverNew.tabletest.SportTablenew;
import com.ziro.bullet.model.discoverNew.trading.CryptoForexApiResponse;
import com.ziro.bullet.model.discoverNew.trading.icons.TradingIconsResponse;
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse;
import com.ziro.bullet.model.followers.FollowersListResponse;
import com.ziro.bullet.model.language.LanguageResponse;
import com.ziro.bullet.model.language.region.RegionApiResponse;
import com.ziro.bullet.model.notification.GeneralNotificationResponse;
import com.ziro.bullet.model.notification.NewsNotificationResponse;
import com.ziro.bullet.model.places.PlacesOrderBase;
import com.ziro.bullet.model.searchhistory.SearchHistoryBase;
import com.ziro.bullet.model.searchhistorydelete.DeleteHistory;
import com.ziro.bullet.model.searchresultnew.SearchresultdataBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {


    /* ============== */
    /*      APIs      */
    /* ============== */

    @GET("content")
    Call<NewsResponse> getNews();

    @GET("news/articles")
    Call<ArticleResponse> getPaginatedNews(@Header("Authorization") String token,
                                           @Query("reader_mode") boolean reader_mode,
                                           @Query("page") String page,
                                           @Query("context") String contextId);

    @GET("news/reels/{id}/captions")
    Call<CaptionResponse> getReelCaptions(@Header("Authorization") String token,
                                          @Path("id") String id);

    @GET("news/feeds")
    Call<HomeResponse> getUpdatedArticles(
            @Header("Authorization") String token,
            @Query("reader_mode") boolean reader_mode,
            @Query("page") String page,
            @Query("context") String contextId,
            @Query("reels") String getReels);

    @GET("news/articles/archive")
    Call<ArticleResponse> getArchiveArticles(@Header("Authorization") String token,
                                             @Query("page") String page);

    @GET("news/topics")
    Call<CategoryResponse> getCategory(@Header("Authorization") String token, @Query("page") String page);

    @GET("news/topics")
    Call<CategoryResponse> searchTopics(@Header("Authorization") String token,
                                        @Query("query") String query,
                                        @Query("page") String page);

    @GET("contents/search")
    Call<Category> search(@Query("data") String data, @Query("page") int page);

    @GET("/news/reels")
    Call<ReelResponse> newsReel(
            @Header("Authorization") String token,
            @Query("context") String context,
            @Query("tag") String hashtag,
            @Query("page") String page,
            @Query("type") String type,
            @Query("debug") Boolean debug
    );

    @GET("/news/reels/archive")
    Call<ReelResponse> newsReelArchive(@Header("Authorization") String token, @Query("page") String page);

    @POST("/analytics/reels/{id}")
    Call<ResponseBody> reelViewEvent(@Header("Authorization") String token, @Path("id") String id);

    @POST("/analytics/duration/{id}")
    Call<ResponseBody> reelDurationEvent(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body Map<String, String> duration
    );
    @POST("analytics/articleview/{articleId}")
    Call<ResponseBody> articleViewCount(@Header("Authorization") String token, @Path("articleId") String articleId);

    @POST("/analytics/custom_event/{article_id}/{event_name}")
    Call<ResponseBody> event(
            @Header("Authorization") String token,
            @Path("article_id") String article_id,
            @Path("event_name") String event_name,
            @Query("duration") String duration
    );

    @FormUrlEncoded
    @POST("/notification/token")
    Call<ResponseBody> sendTokenToServer(@Header("Authorization") String token, @Field("token") String fcmToken);

    @FormUrlEncoded
    @POST("/contact/help/new")
    Call<ResponseBody> contactUs(@Header("Authorization") String token,
                                 @Field("email") String email,
                                 @Field("name") String name,
                                 @Field("message") String message
    );

    @GET("/social/comments/articles/{article_id}")
    Call<CommentResponse> comments(@Header("Authorization") String token,
                                   @Path("article_id") String article_id,
                                   @Query("parent_id") String parent_id,
                                   @Query("page") String page
    );

    @FormUrlEncoded
    @POST("/social/comments/create")
    Call<CommentResponse> createComment(@Header("Authorization") String token,
                                        @Field("ArticleID") String article_id,
                                        @Field("ParentID") String parent_id,
                                        @Field("Comment") String comment
    );

    @FormUrlEncoded
    @POST("/social/likes/article/{id}")
    Call<ResponseBody> likeUnlikeArticle(@Header("Authorization") String token,
                                         @Path("id") String article_id,
                                         @Field("like") boolean like);

    //login user
    @FormUrlEncoded
    @POST("/auth/login")
    Call<ResponseBody> login();

    @POST("/news/articles/{id}/suggest/more")
    Call<ShareBottomSheetResponse> suggestMore(@Header("Authorization") String token, @Path("id") String id);

    @POST("/news/articles/{id}/suggest/less")
    Call<ShareBottomSheetResponse> suggestLess(@Header("Authorization") String token, @Path("id") String id);

    @FormUrlEncoded
    @POST("/news/{type}/{id}/report/concern")
    Call<ShareBottomSheetResponse> report(@Header("Authorization") String token, @Path("type") String type, @Path("id") String id, @Field("message") String message);

    @FormUrlEncoded
    @POST("/news/sources/block")
    Call<ShareBottomSheetResponse> blockSource(@Header("Authorization") String token, @Field("sources") String source);

    @FormUrlEncoded
    @POST("/news/sources/unblock")
    Call<ShareBottomSheetResponse> unblock(@Header("Authorization") String token, @Field("sources") String source);

    @FormUrlEncoded
    @POST("/news/authors/block")
    Call<ShareBottomSheetResponse> blockAuthor(@Header("Authorization") String token, @Field("authors") String author);

    @FormUrlEncoded
    @POST("/news/authors/unblock")
    Call<ShareBottomSheetResponse> unblockAuthor(@Header("Authorization") String token, @Field("authors") String author);

    @FormUrlEncoded
    @POST("/news/sources/follow")
    Call<ShareBottomSheetResponse> follow(@Header("Authorization") String token, @Field("sources") String sources);

    @FormUrlEncoded
    @POST("/news/sources/unfollow")
    Call<ShareBottomSheetResponse> unfollowSource(@Header("Authorization") String token, @Field("sources") String sources);

    @FormUrlEncoded
    @POST("/auth/logout")
    Call<ResponseBody> logout(@Header("Authorization") String token, @Field("token") String refreshToken);

    @GET("/news/sources/categorized")
    Call<Categories> getAllSources(@Header("Authorization") String token,
                                   @Query("page") int page
    );

    @GET("/news/articles/{id}/share/info")
    Call<ResponseBody> share_msg(@Header("Authorization") String token,
                                 @Path("id") String id
    );

    @FormUrlEncoded
    @POST("/news/articles/{id}/archive")
    Call<ResponseBody> archive(@Header("Authorization") String token,
                               @Path("id") String id, @Field("archive") boolean archive);

    @GET("/news/sources/categorized/international")
    Call<CategoryPaginationModel> sourcesInternational(@Header("Authorization") String token,
                                                       @Query("page") int page);

    @GET("/news/sources/categorized/local")
    Call<CategoryPaginationModel> sourcesLocal(@Header("Authorization") String token,
                                               @Query("page") int page);

    @GET("/news/sources")
    Call<SourceModel> searchSources(@Header("Authorization") String token,
                                    @Query("query") String query);

    @GET("/news/sources/followed")
    Call<SourceModel> getFollowingSources(@Header("Authorization") String token, @Query("page") String page);

    @GET("/news/sources/blocked")
    Call<SourceModel> getBlockedSources(@Header("Authorization") String token);

    @GET("/news/authors/blocked")
    Call<AuthorListResponse> getBlockedAuthors(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("/news/sources/follow")
    Call<ResponseBody> followSource(@Header("Authorization") String token, @Field("sources") String sources);

    @FormUrlEncoded
    @POST("/news/sources/follow")
    Call<ResponseBody> followSource(@Header("Authorization") String token, @Field("sources") ArrayList<String> sources);

    @FormUrlEncoded
    @POST("/news/sources/unblock")
    Call<ResponseBody> unblockSource(@Header("Authorization") String token, @Field("sources") String sources);

    @GET("/news/search/all")
    Call<SuggestionsModel> searchSuggestion(@Header("Authorization") String token);

    @GET("/news/{type}")
    Call<TCPResponse> getDataTCP(@Header("Authorization") String token, @Path("type") String type, @Query("context") String context, @Query("page") String page);

    @GET("/news/topics/followed")
    Call<TopicsModel> getFollowingTopics(@Header("Authorization") String token, @Query("page") String page);

    @POST("news/home/sequence")
    Call<BaseModel> updateArticleCatSequence(@Header("Authorization") String token, @Body CategorySequenceModel categorySequenceModel);

    @GET("/news/topics/blocked")
    Call<TopicsModel> getBlockedTopics(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("/news/topics/follow")
    Call<ResponseBody> addTopic(@Header("Authorization") String token, @Field("topics") String topics);

    @FormUrlEncoded
    @POST("/news/topics/follow")
    Call<FollowResponse> addTopicnew(@Header("Authorization") String token, @Field("topics") String topics);


    @FormUrlEncoded
    @POST("/news/sources/follow")
    Call<FollowResponse> followSources(@Header("Authorization") String token, @Field("sources") String sources);

    @FormUrlEncoded
    @POST("/news/sources/unfollow")
    Call<FollowResponse> unfollowSourcesNew(@Header("Authorization") String token, @Field("sources") String sources);

    @GET("news/reels")
    Call<ReelResponse> searchReelsContext(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("context") String context,
            @Query("page") String page
    );

    @FormUrlEncoded
    @POST("/news/topics/unfollow")
    Call<FollowResponse> unfollowTopicNew(@Header("Authorization") String token, @Field("topics") String topics);

    //Add Topics
    @FormUrlEncoded
    @POST("/news/topics/follow")
    Call<OnBoardingModel> addTopics(@Header("Authorization") String token,
                                    @Field("topics") ArrayList<String> topics);

    @FormUrlEncoded
    @POST("news/topics/unfollow")
    Call<ResponseBody> unfollowTopic(@Header("Authorization") String token, @Field("topics") String topics);

    //Authors
    @FormUrlEncoded
    @POST("news/authors/follow")
    Call<ResponseBody> followAuthor(@Header("Authorization") String token, @Field("authors") String author);

    @FormUrlEncoded
    @POST("news/authors/unfollow")
    Call<ResponseBody> unFollowAuthor(@Header("Authorization") String token, @Field("authors") String author);

    @FormUrlEncoded
    @POST("/news/topics/unblock")
    Call<ResponseBody> unblockTopic(@Header("Authorization") String token, @Field("topics") String topics);

    @FormUrlEncoded
    @POST("/news/topics/block")
    Call<ResponseBody> blockTopic(@Header("Authorization") String token, @Field("topics") String topics);

    @FormUrlEncoded
    @POST("/user/config/view")
    Call<ResponseBody> updateConfig(@Header("Authorization") String token
            , @Field("view_mode") String view_mode
            , @Field("narration_enabled") boolean narration_enabled
            , @Field("narration_mode") String narration_mode
            , @Field("reading_speed") float reading_speed
            , @Field("auto_scroll") boolean auto_scroll
            , @Field("bullets_autoplay") boolean bullets_autoplay
            , @Field("reader_mode") boolean reader_mode
            , @Field("videos_autoplay") boolean videos_autoplay
            , @Field("reels_autoplay") boolean reels_autoplay
    );

    @GET("/news/search/all")
    Call<ResponseBody> searchTopic(@Header("Authorization") String token, @Query("search") String keyword);

    @GET("/news/search")
    Call<SearchresultdataBase> getSearchResults(@Header("Authorization") String token, @Query("query") String query);

    @GET("/news/topics/related/{id}")
    Call<TopicsModel> relatedToTopics(@Header("Authorization") String token, @Path("id") String id);

//    @GET("news/sources/headlines/{id}")
//    Call<HeadlineModel> relatedToSources(@Header("Authorization") String token, @Path("id") String id);

    @GET("news/sources/info/{id}")
    Call<Info> relatedToSources(@Header("Authorization") String token, @Path("id") String id);

    @GET("news/topics")
    Call<TopicsModel> searchTopic(@Header("Authorization") String token,
                                  @Query("query") String query,
                                  @Query("page") String page);

    @GET("news/sources")
    Call<SourceModel> searchSource(@Header("Authorization") String token,
                                   @Query("query") String query,
                                   @Query("page") String page);

    @GET("news/sources")
    Call<SourceModel> getSources(@Header("Authorization") String token,
                                 @Query("page") String page);

    @FormUrlEncoded
    @POST("/news/sources/unfollow")
    Call<ResponseBody> unfollowSources(@Header("Authorization") String token, @Field("sources") String sources);

    @GET("/news/articles/{id}")
    Call<ResponseBody> viewArticle(@Header("Authorization") String token,
                                   @Path("id") String id);

    @GET("/news/articles/{id}/related")
    Call<ArticleResponse> relatedArticles(@Header("Authorization") String token,
                                          @Path("id") String id,
                                          @Query("reader_mode") boolean reader_mode,
                                          @Query("page") String page);

    @GET("/news/articles/{id}/related/place")
    Call<ArticleResponse> relateLocation(@Header("Authorization") String token,
                                         @Path("id") String id,
                                         @Query("reader_mode") boolean reader_mode,
                                         @Query("page") String page);

    @GET("/user/config")
    Call<UserConfigModel> userConfig(@Header("Authorization") String token);

    @GET("/user/info")
    Call<UserInfoModel> userInfo(@Header("Authorization") String token);

    @GET("/user/config/push")
    Call<PushResponse> pushConfig(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("/user/config/push")
    Call<PushResponse> push(@Header("Authorization") String token,
                            @Field("breaking") boolean breaking,
                            @Field("personalized") boolean personalized,
                            @Field("frequency") String frequency,
                            @Field("start_time") String startTime,
                            @Field("end_time") String endTime);

    @FormUrlEncoded
    @POST("/user/config/push")
    Call<PushResponse> onBoardingPush(@Header("Authorization") String token,
                                      @Field("breaking") boolean breaking,
                                      @Field("personalized") boolean personalized,
                                      @Field("frequency") String frequency);


    @POST("/user/terms/community-guidelines")
    Call<ResponseBody> acceptGuidelines(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("/user/config/view")
    Call<ResponseBody> updateConfig(@Header("Authorization") String token, @Field("tutorial_done") boolean tutorial_done);

    @GET("/news/search/topic")
    Call<SearchTopic> getTopic(@Header("Authorization") String token, @Query("query") String keyword);

    @GET("/news/search/source")
    Call<SearchSource> getSource(@Header("Authorization") String token, @Query("query") String keyword);

    @GET("/news/discover")
    Call<NewDiscoverResponse> getDiscover(@Header("Authorization") String token, @Query("theme") String theme, @Query("page") String page);

    @GET("/news/languages")
    Call<LanguageResponse> getLanguages(@Header("Authorization") String token, @Query("query") String query, @Query("page") String page);

    @FormUrlEncoded
    @PATCH("/news/languages/selected")
    Call<LanguageResponse> selectLanguages(@Header("Authorization") String token, @Field("id") String id);

    @GET("/news/editions")
    Call<ResponseEdition> getEditions(@Header("Authorization") String token, @Query("parent") String parent);

    @GET("/news/editions")
    Call<ResponseEdition> getEditions(@Header("Authorization") String token, @Query("query") String query, @Query("page") String page);

    @FormUrlEncoded
    @POST("/news/editions/unfollow")
    Call<ResponseBody> unfollowEdition(@Header("Authorization") String token, @Field("editions") String editions);

    @FormUrlEncoded
    @POST("/news/editions/follow")
    Call<ResponseBody> followEdition(@Header("Authorization") String token, @Field("editions") String editions);

    @FormUrlEncoded
    @PATCH("/news/sources/followed")
    Call<ResponseBody> updateSources(@Header("Authorization") String token, @Field("follow") ArrayList<String> followed, @Field("unfollow") ArrayList<String> unfollowed);

    @FormUrlEncoded
    @PATCH("/news/topics/followed")
    Call<ResponseBody> updateTopics(@Header("Authorization") String token, @Field("follow") ArrayList<String> followed, @Field("unfollow") ArrayList<String> unfollowed);

    @GET("news/home")
    Call<HomeModel> getHomeData(@Header("Authorization") String token, @Query("type") String type, @Query("reader_mode") boolean reader_mode);

    @GET("news/locations")
    Call<LocationModel> getLocations(@Header("Authorization") String token,
                                     @Query("query") String query, @Query("page") String page);

    @FormUrlEncoded
    @PATCH("/news/locations/followed")
    Call<ResponseBody> updateLocations(@Header("Authorization") String token, @Field("follow") ArrayList<String> followed, @Field("unfollow") ArrayList<String> unfollowed);

    @FormUrlEncoded
    @POST("/news/{type}/{id}/report/concern")
    Call<ResponseBody> sendReport(@Header("Authorization") String token, @Path("type") String type, @Path("id") String article_id, @Field("message") ArrayList<String> concern);

    @Multipart
    @POST("/contact/suggestion/new")
    Call<ResponseBody> sendSuggestion(@Header("Authorization") String token, @Part("email") RequestBody email, @Part("message") RequestBody message, @Part ArrayList<MultipartBody.Part> files);

    @Multipart
    @POST("/contact/suggestion/new")
    Call<ResponseBody> sendSuggestion(@Header("Authorization") String token, @Part("email") RequestBody email, @Part("message") RequestBody message);

    @GET("/news/{type}/{id}/report/concern")
    Call<ReportResponse> getConcerns(@Header("Authorization") String token, @Path("type") String type, @Path("id") String article_id);

    @GET("/news/locations/followed")
    Call<LocationModel> getFollowedLocation(@Header("Authorization") String token, @Query("page") String page);

    @FormUrlEncoded
    @POST("/news/locations/unfollow")
    Call<ResponseBody> unfollowLocation(@Header("Authorization") String token, @Field("locations") String locations);

    @FormUrlEncoded
    @POST("/news/locations/unfollow")
    Call<FollowResponse> unfollowLocationNew(@Header("Authorization") String token, @Field("locations") String locations);

    @FormUrlEncoded
    @POST("/news/locations/follow")
    Call<ResponseBody> followLocation(@Header("Authorization") String token, @Field("locations") String locations);


    @FormUrlEncoded
    @POST("/news/locations/follow")
    Call<FollowResponse> followLocationNew(@Header("Authorization") String token, @Field("locations") String locations);

    @Multipart
    @POST("/contact/article/new")
    Call<ResponseBody> createArticle(@Header("Authorization") String token,
                                     @Part("title") RequestBody title,
                                     @Part("source") RequestBody source,
                                     @Part("link") RequestBody link,
                                     @Part("publisheddate") RequestBody publisheddate,
                                     @Part MultipartBody.Part image,
                                     @Part("bullets") ArrayList<String> bullets
    );

    @GET("/news/topics/suggested")
    Call<TopicsModel> getSuggestedTopics(@Header("Authorization") String token, @Query("has_reels") boolean has_reels);

    @GET("/news/sources/suggested")
    Call<SourceModel> getSuggestedChannels(
            @Header("Authorization") String token,
            @Query("has_reels") boolean has_reels
    );

    @GET("/news/locations/suggested")
    Call<LocationModel> getSuggestedLocations(@Header("Authorization") String token);


    @GET("news/topics")
    Call<TopicsModel> searchPageTopics(@Header("Authorization") String token,
                                       @Query("query") String query,
                                       @Query("page") String page);

    @GET("/news/sources")
    Call<SourceModel> searchPageSources(@Header("Authorization") String token,
                                        @Query("query") String query,
                                        @Query("page") String page);

    @GET("news/locations")
    Call<LocationModel> searchLocations(@Header("Authorization") String token,
                                        @Query("query") String query, @Query("page") String page);

    @GET("news/authors")
    Call<AuthorSearchResponse> searchAuthors(@Header("Authorization") String token,
                                             @Query("query") String query, @Query("page") String page);

    @POST("/alert/view/{id}")
    Call<ResponseBody> dismissAlert(@Header("Authorization") String token, @Path("id") String id);

    @FormUrlEncoded
    @PATCH("/news/editions/followed")
    Call<ResponseBody> updateEditions(@Header("Authorization") String token, @Field("follow") ArrayList<String> followed,
                                      @Field("unfollow") ArrayList<String> unfollowed, @Field("force") boolean force);


    @GET("/news/editions/followed")
    Call<ResponseEdition> getSelectedEditions(@Header("Authorization") String token);


    @GET("news/articles")
    Call<ArticleResponse> searchArticles(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("reader_mode") boolean reader_mode,
            @Query("page") String page
    );


    @GET("news/articles")
    Call<ArticleBase> searchArticlesContext(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("context") String context,
            @Query("reader_mode") boolean reader_mode,
            @Query("page") String page
    );

    @FormUrlEncoded
    @POST("/auth/public/register/device")
    Call<ResponseBody> skipLogin(@Header("Authorization") String token, @Field("device") String deviceId);

    @GET("/news/articles/for_you")
    Call<ForYou> getForYouArticles(@Header("Authorization") String token,
                                   @Query("reader_mode") boolean reader_mode,
                                   @Query("page") String page);

    @GET("/studio/{articleId}/tags/suggestion")
    Call<TagsResponse> getSuggestedTags(@Header("Authorization") String token,
                                        @Path("articleId") String articleId,
                                        @Query("query") String query
    );

    @GET("/studio/{article_id}/locations/suggestion")
    Call<LocationResponse> getArticleSuggestedLocation(@Header("Authorization") String token,
                                                       @Path("article_id") String articleId,
                                                       @Query("query") String query
    );

    @FormUrlEncoded
    @POST("/studio/{articleId}/tags")
    Call<TagsResponse> addPostTags(@Header("Authorization") String token,
                                   @Path("articleId") String articleId,
                                   @Field("tag") String tag
    );

    @FormUrlEncoded
    @POST("/studio/{articleId}/locations")
    Call<LocationResponse> addPostLocation(@Header("Authorization") String token,
                                           @Path("articleId") String articleId,
                                           @Field("location") String location
    );

    @FormUrlEncoded
    @PATCH("/studio/articles/{articleId}/language")
    Call<ResponseBody> updateArticleLanguage(@Header("Authorization") String token,
                                             @Path("articleId") String articleId,
                                             @Field("language") String languageCode
    );

    @POST("/studio/articles/{api}")
    Call<SingleArticle> createArticle(@Header("Authorization") String token,
                                      @Path("api") String api,
                                      @Body RequestBody body);

    @POST("/studio/reels")
    Call<SingleArticle> createReels(@Header("Authorization") String token,
                                    @Body RequestBody body);

    @Multipart
    @POST("/media/{api}")
    Call<ResponseBody> uploadImageVideo(@Header("Authorization") String token,
                                        @Path("api") String api,
                                        @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("/media/validate")
    Call<ResponseBody> mediaValidate(@Header("Authorization") String token,
                                     @Field("size") long size,
                                     @Field("duration") float duration,
                                     @Field("type") String type,
                                     @Field("media") String media);

    @FormUrlEncoded
    @PATCH("/studio/articles/{id}/status")
    Call<ResponseBody> publishArticle(@Header("Authorization") String token, @Path("id") String id, @Field("status") String status);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/studio/{articleId}/tags", hasBody = true)
    Call<TagsResponse> removePostTags(@Header("Authorization") String token,
                                      @Path("articleId") String articleId,
                                      @Field("id") String tagId
    );

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/studio/{articleId}/locations", hasBody = true)
    Call<LocationResponse> removePostLocation(@Header("Authorization") String token,
                                              @Path("articleId") String articleId,
                                              @Field("id") String tagId
    );

    @GET("/studio/{articleId}/tags")
    Call<TagsResponse> getTagList(@Header("Authorization") String token,
                                  @Path("articleId") String articleId
    );

    @GET("/studio/{articleId}/locations")
    Call<LocationResponse> getLocationList(@Header("Authorization") String token,
                                           @Path("articleId") String articleId
    );


    @GET("/studio/articles")
    Call<ArticleResponse> getMyArticles(
            @Header("Authorization") String token,
            @Query("source") String source,
            @Query("status") String status,
            @Query("page") String page
    );

//    @GET("/news/authors/{id}/articles")
//    Call<ArticleResponse> getAuthorArticles(
//            @Header("Authorization") String token,
//            @Path("id") String id,
//            @Query("page") String page
//    );
//
//    @GET("/news/authors/{id}/reels")
//    Call<ReelResponse> getAuthorReels(
//            @Header("Authorization") String token,
//            @Path("id") String id,
//            @Query("page") String page
//    );

    @GET("/studio/reels")
    Call<ReelResponse> getMyReels(
            @Header("Authorization") String token,
            @Query("source") String sourceId,
            @Query("page") String page
    );

//    @GET("/news/reels")
//    Call<ReelResponse> searchReels(
//            @Header("Authorization") String token,
//            @Query("query") String query,
//            @Query("page") String page
//    );


    @GET("/news/reels")
    Call<ReelResponse> searchReels(
            @Header("Authorization") String token,
            @Query("context") String context,
            @Query("tag") String hashtag,
            @Query("query") String query,
            @Query("page") String page,
            @Query("type") String type,
            @Query("debug") Boolean debug
    );

    @GET("/news/discover/relevant")
    Call<RelevantResponse> getRelevant(@Header("Authorization") String token,
                                       @Query("query") String query,
                                       @Query("reader_mode") boolean reader_mode
    );

    @GET("/news/community")
    Call<ArticleResponse> communityFeed(@Header("Authorization") String token, @Query("page") String page);

    @GET("/news/authors/{id}")
    Call<AuthorResponse> getAuthor(@Header("Authorization") String token, @Path("id") String id);

    @GET("/news/authors/followed")
    Call<AuthorSearchResponse> getFollowingAuthors(@Header("Authorization") String token, @Query("page") String page);

    @GET("/news/articles/{id}/social")
    Call<CommentCounterResponse> commentCounter(@Header("Authorization") String token, @Path("id") String id);

    @GET("/studio/channels/exists")
    Call<ResponseBody> checkChannelNameExist(@Header("Authorization") String token, @Query("name") String name);

    @GET("/news/authors/suggested")
    Call<AuthorSearchResponse> getSuggestedAuthors(
            @Header("Authorization") String token,
            @Query("has_reels") boolean hasReels
    );
//
//    @GET("/news/reels/suggested")
//    Call<ReelResponse> getSuggestedReels(@Header("Authorization") String token);

    @GET("/studio/channels")
    Call<UserChannels> getChannels(
            @Header("Authorization") String token,
            @Query("page") String page
    );

    @GET("/studio/channels/categorized")
    Call<CategorizedChannels> getChannelsCategorized(@Header("Authorization") String token);

    @GET("/news/sources/data/{id}")
    Call<ChannelDetails> getChannelDetails(@Header("Authorization") String token, @Path("id") String id);

    @FormUrlEncoded
    @POST("/studio/channels")
    Call<ResponseBody> createChannel(@Header("Authorization") String token,
                                     @Field("name") String name,
                                     @Field("link") String link,
                                     @Field("description") String description,
                                     @Field("icon") String icon,
                                     @Field("cover") String cover
    );

    @FormUrlEncoded
    @PATCH("/studio/channels/{channelId}")
    Call<UpdateChannelResponse> updateChannelProfile(
            @Header("Authorization") String token,
            @Path("channelId") String channelId,
            @Field("description") String description,
            @Field("icon") String icon,
            @Field("cover") String cover
    );

    @FormUrlEncoded
    @PATCH("/studio/channels/{channelId}")
    Call<UpdateChannelResponse> updateChannelProfile2(
            @Header("Authorization") String token,
            @Path("channelId") String channelId,
            @Field("icon") String icon,
            @Field("cover") String cover,
            @Field("portrait") String portrait
    );

    @FormUrlEncoded
    @PATCH("/studio/channels/{id}")
    Call<ResponseBody> updateChannel(@Header("Authorization") String token,
                                     @Path("id") String id,
                                     @Field("icon") String icon,
                                     @Field("cover") String cover
    );

    @GET("/studio/scheduled")
    Call<ArticleResponse> getScheduledPosts(
            @Header("Authorization") String token,
            @Query("source") String source,
            @Query("page") String page
    );

    @GET("/studio/draft")
    Call<ArticleResponse> getDraftedPosts(
            @Header("Authorization") String token,
            @Query("source") String source,
            @Query("page") String page
    );

    @FormUrlEncoded
    @PATCH("/studio/articles/{id}/source")
    Call<ResponseBody> updateChannelForArticle(@Header("Authorization") String token,
                                               @Path("id") String id,
                                               @Field("source") String source
    );

    @GET("/studio/followers")
    Call<FollowersListResponse> getFollowers(
            @Header("Authorization") String token,
            @Query("source") String source,
            @Query("query") String query,
            @Query("page") String page
    );


    @GET("/notification/list/general")
    Call<GeneralNotificationResponse> getGeneralNotifications(
            @Header("Authorization") String token,
            @Query("page") String page
    );

    @GET("/notification/list/news")
    Call<NewsNotificationResponse> getNewsNotifications(
            @Header("Authorization") String token,
            @Query("page") String page
    );

    @GET("/news/reels/info")
    Call<ResponseBody> getFollowersWithReels(
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("/notification/read")
    Call<ResponseBody> readNotification(@Header("Authorization") String token, @Field("ids") String id);

    @GET("/news/onboarding")
    Call<OnBoardingModel> getOnboarding(
            @Header("Authorization") String token
    );

    @POST("/news/onboarding")
    Call<OnBoardingModel> saveOnboarding(
            @Header("Authorization") String token,
            @Body SaveOnBoardingModel saveOnBoardingModel);

    @GET("/news/languages")
    Call<ContentLanguageResponse> getContentLanguages(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("page") String page
    );

    @GET("/news/locations")
    Call<RegionResponse> getRegions(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("page") String page
    );

    @GET("/news/topics")
    Call<TopicsModel> getTopics(
            @Header("Authorization") String token,
            @Query("query") String query,
            @Query("page") String page
    );

    @FormUrlEncoded
    @PATCH("/news/languages/followed")
    Call<ResponseBody> updateContentLanguages(@Header("Authorization") String token,
                                              @Field("force") boolean force,
                                              @Field("follow") List<String> followed);

    @GET("/news/discover/list")
    Call<DiscoverNewResponse> getNewDiscoverTopics(
            @Header("Authorization") String token
    );

    @GET("/news/search/list")
    Call<PlacesOrderBase> getPlacesOrder(
            @Header("Authorization") String token
    );

    @GET("/news/discover/detail")
    Call<DiscoverDetailsResponse> getNewDiscoverDetails(
            @Header("Authorization") String token,
            @Query("context") String context
    );

    @GET("/news/discover/detail")
    Call<DiscoverDetailsResponse> getNewDiscoverDetailsee(
            @Header("Authorization") String token,
//            @Header("api-version") String apiVersion,
            @Query("context") String context
    );

    @GET("/news/search/history")
    Call<SearchHistoryBase> getSearchHistory(
            @Header("Authorization") String token
    );

    @DELETE("/news/search/history")
    Call<FollowResponse> clearHistory(
            @Header("Authorization") String token
    );

    @DELETE("/news/search/history/{id}")
    Call<DeleteHistory> deleteHistory(
            @Header("Authorization") String token,
            @Path("id") String id
    );

    @GET
    Call<WeatherForecastResponse> getWeatherForecast(
            @Url String url,
            @Query("key") String key,
            @Query("q") String location,
            @Query("days") String days,
            @Query("aqi") String airQualityIndex
    );

    @GET
    Call<CryptoForexApiResponse> getCryptoPrices(
            @Url String url,
            @Header("Authorization") String token
    );

    @GET
    Call<CryptoForexApiResponse> getForexPrices(
            @Url String url,
            @Header("Authorization") String token
    );

    @GET
    Call<LiveScoreApiResponse> getLiveScore(
            @Url String url,
            @Header("X-RapidAPI-Key") String key,
            @Header("X-RapidAPI-Host") String host,
            @Query("Category") String category,
            @Query("Date") String date,
            @Query("Timezone") String Timezone
    );

    @GET
    Call<SportsInfoResponse> getSportsInfo(
            @Url String url,
            @Header("X-RapidAPI-Key") String key,
            @Header("X-RapidAPI-Host") String host,
            @Query("Category") String category,
            @Query("Eid") String eid
    );

    @GET
    Call<SportTeam> getSportTeamsApi(
            @Url String url,
            @Header("X-RapidAPI-Key") String key,
            @Header("X-RapidAPI-Host") String host,
            @Query("Category") String category,
            @Query("Eid") String eid
    );

    @GET
    Call<ScorecardResponse> getSportScorecard(
            @Url String url,
            @Header("X-RapidAPI-Key") String key,
            @Header("X-RapidAPI-Host") String host,
            @Query("Category") String category,
            @Query("Eid") String eid
    );

    @GET
    Call<SportTablenew> getSportTableApi(
            @Url String url,
            @Header("X-RapidAPI-Key") String key,
            @Header("X-RapidAPI-Host") String host,
            @Query("Category") String category,
            @Query("Eid") String eid
    );

    @GET("news/discover/finance")
    Call<TradingIconsResponse> getTradingItemsList(
            @Header("Authorization") String token
    );

    @GET("news/regions")
    Call<RegionApiResponse> getRegions(
            @Header("Authorization") String token
    );

    @GET("news/languages")
    Call<LanguageResponse> getLanguageWithRegion(
            @Header("Authorization") String token,
            @Query("region") String regionId
    );

    @GET("news/languages")
    Call<LanguageResponse> getLanguageWithoutRegion(
            @Header("Authorization") String token
    );

    @GET("news/public/regions")
    Call<RegionApiResponse> getPublicRegions();

    @GET("news/public/languages")
    Call<LanguageResponse> getPublicLanguageWithRegion(
            @Query("region") String regionId
    );

    @GET("news/languages")
    Call<LanguageResponse> getSecondaryLanguages();

    @FormUrlEncoded
    @PATCH("news/regions")
    Call<BaseModel> updateUserRegion(
            @Header("Authorization") String token,
            @Field("region") String regionId
    );

    @FormUrlEncoded
    @PATCH("news/languages")
    Call<BaseModel> updateUserLanguage(
            @Header("Authorization") String token,
            @Field("language") String language,
            @Field("tag") String tag
    );
}