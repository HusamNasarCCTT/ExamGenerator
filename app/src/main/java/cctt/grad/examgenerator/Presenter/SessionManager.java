package cctt.grad.examgenerator.Presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import cctt.grad.examgenerator.View.LoginActivity;

/**
 * Created by Hussam Nasar on 08/05/2016.
 */
public class SessionManager {

    Context _context;
    public SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "ExamGeneratorPrefs";

    //Shared Preference inputs...
    private static final String KEY_LOGIN_STATE = "IsLoggedIn";
    public static final String KEY_ID = "ID";
    public static final String KEY_USERNAME = "Username";
    public static final String KEY_PASSWORD = "Password";

    public SessionManager(Context context) {
        this._context = context;
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }


    public void createLoginSession(int id, String userName, String passWord){

        editor.putBoolean(KEY_LOGIN_STATE, true);
        editor.putInt(KEY_ID, id);
        editor.putString(KEY_USERNAME, userName);
        editor.putString(KEY_PASSWORD, passWord);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){

        HashMap<String, String> userDetails = new HashMap<String, String>();
        userDetails.put(String.valueOf(KEY_ID), null);
        userDetails.put(KEY_USERNAME, null);
        userDetails.put(KEY_PASSWORD, null);
        return userDetails;
    }


    /*Checks login status and redirects to Login Activity
    If user isn't logged in...*/
    public void checkLogin(){

        if(! this.isLoggedIn()){

            //User is not logged in, start Login Activity...
            Intent i = new Intent(_context, LoginActivity.class);

            //Close all activities...
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //Add new flag to start new activity...
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //Start login Activity...
            _context.startActivity(i);
        }
    }

    public void logoutUser(){

        //Clear shared preferences...
        editor.clear();
        editor.commit();

        //After log out, redirect user to login activity...
        Intent i = new Intent(_context, LoginActivity.class);

        //Close all activities...
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Add new flag to start new activity...
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //Start login activity...
        _context.startActivity(i);
    }



    public boolean isLoggedIn(){

        return sharedPreferences.getBoolean(KEY_LOGIN_STATE, false);
    }

    
}
