package cn.edu.hit.project.ec.models.user;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class User {
    public int id;
    public String name;
    public String email;
    @SerializedName("api_token")
    public String apiToken;
    @SerializedName("api_token_expire")
    public long apiTokenExpire;

    public void save(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("userId", id);
        editor.putString("userName", name);
        editor.putString("userEmail", email);
        editor.putString("apiToken", apiToken);
        editor.putLong("apiTokenExpire", apiTokenExpire);
        editor.apply();
    }

    public static User load(SharedPreferences preferences) {
        User user = new User();
        user.id = preferences.getInt("userId", 0);
        user.name = preferences.getString("userName", null);
        user.email = preferences.getString("userEmail", null);
        user.apiToken = preferences.getString("apiToken", null);
        user.apiTokenExpire = preferences.getLong("apiTokenExpire", 0);
        if (user.name != null &&
                user.email != null &&
                user.apiToken != null &&
                user.apiTokenExpire > System.currentTimeMillis() / 1000) {
            return user;
        }
        return null;
    }

    public static void logout(SharedPreferences preferences) {
        preferences.edit().clear().apply();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
