package se.liu.ida.josfa969.tddd80.help_classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Josef on 28/07/14.
 *
 * A class containing strings used as keys
 * when adding extras to intents
 */
public class Constants {
    // Key strings used between activities
    public static final String USER_NAME_KEY = "se.liu.ida.josfa969.activities.USER_NAME";
    public static final String E_MAIL_KEY = "se.liu.ida.josfa969.activities.E_MAIL";
    public static final String PASSWORD_KEY = "se.liu.ida.josfa969.activities.PASSWORD";
    public static final String COUNTRY_KEY = "se.liu.ida.josfa969.activities.COUNTRY";
    public static final String CITY_KEY = "se.liu.ida.josfa969.activities.CITY";
    public static final String FOLLOWERS_KEY = "se.liu.ida.josfa969.activities.FOLLOWERS";
    public static final String LOCATION_KEY = "se.liu.ida.josfa969.activities.LOCATION";
    public static final String ORIGINAL_USER_KEY = "se.liu.ida.josfa969.activities.ORIGINAL_USER";
    public static final String ORIGINAL_E_MAIL_KEY = "se.liu.ida.josfa969.activities.ORIGINAL_E_MAIL";
    public static final String TARGET_USER_KEY = "se.liu.ida.josfa969.activities.TARGET_USER";
    public static final String TAG_STRING_KEY = "se.liu.ida.josfa969.activities.TAG_STRING";
    public static final String IDENTIFIER_STRING_KEY = "se.liu.ida.josfa969.activities.IDENTIFIER_STRING";
    public static final String IDEAS_KEY = "se.liu.ida.josfa969.activities.IDEAS";
    public static final String PEOPLE_KEY = "se.liu.ida.josfa969.activities.PEOPLE";
    public static final String IDEA_ID_KEY = "se.liu.ida.josfa969.activities.IDEA_ID";
    public static final String IDEA_TEXT_KEY = "se.liu.ida.josfa969.activities.IDEA_TEXT";
    public static final String SENDER_KEY = "se.liu.ida.josfa969.activities.SENDER";
    public static final String RECEIVER_KEY = "se.liu.ida.josfa969.activities.RECEIVER";
    public static final String MESSAGE_TEXT_KEY = "se.liu.ida.josfa969.activities.MESSAGE_TEXT";
    public static final String MESSAGES_KEY = "se.liu.ida.josfa969.activities.MESSAGES";
    public static final String COMMENTS_KEY = "se.liu.ida.josfa969.activities.COMMENTS";
    public static final String COMMENT_TEXT_KEY = "se.liu.ida.josfa969.activities.COMMENT_TEXT";
    public static final String POSTER_KEY = "se.liu.ida.josfa969.activities.POSTER";
    public static final String APPROVAL_NUM_KEY = "se.liu.ida.josfa969.activities.APPROVAL_NUM";
    public static final String USER_DATA_KEY = "se.liu.ida.josfa969.activities.USER_DATA";
    public static final String IS_APPROVING_KEY = "se.liu.ida.josfa969.activities.IS_APPROVING";
    public static final String IS_FOLLOWING_KEY = "se.liu.ida.josfa969.activities.IS_FOLLOWING";
    public static final String RESPONSE_KEY = "se.liu.ida.josfa969.activities.RESPONSE";
    public static final String FOLLOW_TOAST_MSG_KEY = "se.liu.ida.josfa969.background_services.FOLLOW_TOAST_MSG";
    public static final String APPROVING_TOAST_MSG_KEY = "se.liu.ida.josfa969.background_services.APPROVE_TOAST_MSG";
    public static final String USER_DATA_UPDATE_MSG_KEY = "se.liu.ida.josfa969.background_services.UPDATE_DATA_MSG";

    // Broadcast actions
    public static final String ADD_FOLLOWER_RESP = "se.liu.ida.josfa969.background_services.action.FOLLOWER_ADDED";
    public static final String REMOVE_FOLLOWER_RESP = "se.liu.ida.josfa969.background_services.action.FOLLOWER_REMOVED";
    public static final String SEARCH_IDEAS_RESP = "se.liu.ida.josfa969.background_services.action.IDEAS_FOUND";
    public static final String SEARCH_PEOPLE_RESP = "se.liu.ida.josfa969.background_services.action.PEOPLE_FOUND";
    public static final String UPDATE_USER_DATA_RESP = "se.liu.ida.josfa969.background_services.action.DATA_UPDATED";
    public static final String ADD_APPROVING_RESP = "se.liu.ida.josfa969.background_services.action.ADD_APPROVING";
    public static final String REMOVE_APPROVING_RESP = "se.liu.ida.josfa969.background_services.action.REMOVE_APPROVING";
    public static final String GET_FOLLOWING_RESP = "se.liu.ida.josfa969.background_services.action.GET_FOLLOWING";
    public static final String GET_APPROVING_RESP = "se.liu.ida.josfa969.background_services.action.GET_APPROVING";
    public static final String GET_IDEA_FEED_RESP = "se.liu.ida.josfa969.background_services.action.GET_IDEA_FEED";
    public static final String GET_MESSAGE_FEED_RESP = "se.liu.ida.josfa969.background_services.action.GET_MESSAGE_FEED";
    public static final String POST_IDEA_RESP = "se.liu.ida.josfa969.background_services.action.POST_IDEA";
    public static final String GET_CONVERSATION_RESP = "se.liu.ida.josfa969.background_services.action.GET_CONVERSATION";
    public static final String SEND_MESSAGE_RESP = "se.liu.ida.josfa969.background_services.action.SEND_MESSAGE";
    public static final String GET_COMMENTS_RESP = "se.liu.ida.josfa969.background_services.action.GET_COMMENTS";
    public static final String POST_COMMENT_RESP = "se.liu.ida.josfa969.background_services.action.POST_COMMENT";
    public static final String GET_USER_DATA_RESP = "se.liu.ida.josfa969.background_services.action.GET_USER_DATA";
    public static final String IS_APPROVING_RESP = "se.liu.ida.josfa969.background_services.action.IS_APPROVING";
    public static final String IS_FOLLOWING_RESP = "se.liu.ida.josfa969.background_services.action.IS_FOLLOWING";
    public static final String GET_OTHER_USER_IDEAS_RESP = "se.liu.ida.josfa969.background_services.action.GET_OTHER_USER_RECENT_IDEAS";
    public static final String LOGIN_USER_RESP = "se.liu.ida.josfa969.background_services.action.LOGIN_USER";
    public static final String REGISTER_USER_RESP = "se.liu.ida.josfa969.background_services.action.REGISTER_USER";
}
