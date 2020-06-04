package com.squarenet.smartq;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import java.util.ArrayList;
import static android.content.Context.MODE_PRIVATE;

public class SmartQSession {

    private static SmartQSession smartQSession = null;
    public static final String TAG = "SmartQSession";
    public static final String PREFIX = "HYCU";
    public static final String SMARTQID = "smartqid";
    public static Context context = null;
    public static SmartQUser currentSmartQUser = null;
    public static ArrayList<SmartQUser> smartQUserList = new ArrayList<SmartQUser>();

    public static SmartQSession getInstance() {
        if (smartQSession == null) {
            smartQSession = new SmartQSession();
        }
        return smartQSession;
    }
    //SINGLETERN

    public static void init(Context Context) {
        if(context == null )
            context = Context;
    }

    //Public
    public static void setUserData(int smartQID, String forKey, String value) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFIX+smartQID, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(forKey, value);
        editor.commit();
    }

    //Public
    public static String getUserData(int smartQID, String forKey) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFIX+smartQID, MODE_PRIVATE);
        String value = mPrefs.getString(forKey, null); //default 0
        return value;
    }

    //Private
    private static void setSmartQID(int smartQID) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFIX+SMARTQID, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(SMARTQID, smartQID);
        editor.commit();
    }

    private static int getSmartQID() {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFIX+SMARTQID, MODE_PRIVATE);
        int smartQID = mPrefs.getInt(SMARTQID, 0); //default 0
        return smartQID;
    }

    public static void loadAllSmartQUser() {
        int maxSmartQID = getSmartQID();

        smartQUserList = new ArrayList<SmartQUser>();

        for (int i = 0; i < maxSmartQID; i++) {
            //loadSmartQUser()
            SmartQUser smartQUser = loadSmartQUser(i+1);
            if(smartQUser.id !=0)
                smartQUserList.add(smartQUser);
        }
    }
    //public
    public static void setSmartQUser(SmartQUser smartQUser) {

        int maxSmartQID = getSmartQID();

        SmartQUser smartQUserForSet = null;

        for (int i = 0; i < maxSmartQID; i++) {
            //loadSmartQUser()
            SmartQUser nowSmartQUser = loadSmartQUser(i+1);

            if(nowSmartQUser.id != 0) {
                if (nowSmartQUser.userId.equals(smartQUser.userId)) {
                    smartQUser.id = nowSmartQUser.id;
                    smartQUserForSet = smartQUser;
                }
            }
        }

        if (smartQUserForSet != null) {
            //update
            int smartQID = updateSmartQUser(smartQUserForSet);
            smartQUserForSet = smartQUser;
            smartQUserForSet.id = smartQID;

        }else {
            int smartQID = insertSmartQUser(smartQUser); //save file
            smartQUserForSet = smartQUser;
            smartQUserForSet.id = smartQID;

            //addSmartQUser(smartQUser); //save memory
        }
        currentSmartQUser = smartQUserForSet; // save now user
        loadAllSmartQUser(); //refresh
    }

    public static String getSmartQAllUserToString() {

        Gson gson = new Gson();
        String smartUserListString = gson.toJson(smartQUserList);

        return smartUserListString;
    }

    private static int insertSmartQUser(SmartQUser smartQUser) {

        int smartQID = getSmartQID();
        smartQID += 1; //Incremental +1
        setSmartQID(smartQID);

        SharedPreferences mPrefs = context.getSharedPreferences(PREFIX+smartQID, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(SmartQUser.ID, smartQID);
        editor.putString(SmartQUser.USERID, smartQUser.userId);
        editor.putString(SmartQUser.PASSWORD, smartQUser.password);
        editor.commit();

        return smartQID;
    }

    private static int updateSmartQUser(SmartQUser smartQUser) {

        int smartQID = smartQUser.id;

        SharedPreferences mPrefs = context.getSharedPreferences(PREFIX+smartQID, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(SmartQUser.ID, smartQID);
        editor.putString(SmartQUser.USERID, smartQUser.userId);
        editor.putString(SmartQUser.PASSWORD, smartQUser.password);
        editor.commit();

        return smartQID;
    }

    public static void deleteSmartQUser(int smartQID) {

        SharedPreferences mPrefs = context.getSharedPreferences(PREFIX+smartQID, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        editor.commit();
        //return smartQID;
        if (smartQID == currentSmartQUser.id) {
            currentSmartQUser = null;
        }
    }

    public static void  deleteAllSmartQUser() {

        int smartQID = getSmartQID();

        for (int i = 0; i < smartQID; i++) {
            //loadSmartQUser()
            SmartQUser nowSmartQUser = loadSmartQUser(i+1);

            if(nowSmartQUser.id !=0){
                deleteSmartQUser(nowSmartQUser.id);
            }
        }
        loadAllSmartQUser();
        setSmartQID(0);
    }

    public static void deleteSmartQUserByID(SmartQUser smartQUser) {

        int smartQID = getSmartQID();

        for (int i = 0; i < smartQID; i++) {
            //loadSmartQUser()
            SmartQUser nowSmartQUser = loadSmartQUser(i+1);

            if(nowSmartQUser.id !=0){
                if (nowSmartQUser.userId.equals(smartQUser.userId)) {
                    deleteSmartQUser(nowSmartQUser.id);
                }
            }
        }
        loadAllSmartQUser();
    }
    private static SmartQUser loadSmartQUser(int smartQID) {

            SmartQUser smartQUser = new SmartQUser();

            SharedPreferences mPrefs = context.getSharedPreferences(PREFIX+smartQID, MODE_PRIVATE);

            int isExist = mPrefs.getInt(smartQUser.ID, 0);

            if (isExist > 0) {
                smartQUser.id = mPrefs.getInt(smartQUser.ID, 0);
                smartQUser.userId = mPrefs.getString(SmartQUser.USERID, null);
                smartQUser.password = mPrefs.getString(SmartQUser.PASSWORD, null);
            }
            return smartQUser;
    }

    static public class SmartQUser {
        public static String ID = "id";
        public static String USERID = "userId";
        public static String PASSWORD = "password";
        int id = 0;
        String userId = null;
        String password = null;
    }
}
