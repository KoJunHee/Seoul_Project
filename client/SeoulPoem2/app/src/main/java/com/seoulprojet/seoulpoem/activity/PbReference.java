package com.seoulprojet.seoulpoem.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by lynn on 2017-10-24.
 */

// 자동 로그인 위한 sharedpreference 사용
public class PbReference {

    public final String pref_name = "Login";

    static Context mContext;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public PbReference(Context context){
        mContext = context;
    }

    public void put(String key, String value){
        pref = mContext.getSharedPreferences(pref_name, Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void put(String key, boolean value){
        pref = mContext.getSharedPreferences(pref_name, Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void put(String key, int value){
        pref = mContext.getSharedPreferences(pref_name, Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public String getValue(String key, String dftValue){
        pref = mContext.getSharedPreferences(pref_name, Activity.MODE_PRIVATE);
        try{
            return pref.getString(key, dftValue);
        } catch(Exception e){
            return dftValue;
        }
    }

    public int getValue(String key, int dftValue){
        pref = mContext.getSharedPreferences(pref_name, Activity.MODE_PRIVATE);
        try{
            return pref.getInt(key, dftValue);
        } catch(Exception e){
            return dftValue;
        }
    }

    public boolean getValue(String key, boolean dftValue){
        pref = mContext.getSharedPreferences(pref_name, Activity.MODE_PRIVATE);
        try{
            return pref.getBoolean(key, dftValue);
        } catch(Exception e){
            return dftValue;
        }
    }

    public void removeAll(){

        pref = mContext.getSharedPreferences(pref_name, Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.clear();
        editor.commit();

    }

}
