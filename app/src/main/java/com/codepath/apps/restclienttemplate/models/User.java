package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {
    @ColumnInfo
    @PrimaryKey
    public long id;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String location;

    @ColumnInfo
    public String followers_count;

    @ColumnInfo
    public String friends_count;

    @ColumnInfo
    public String favourites_count;

    @ColumnInfo
    public String screenName;

    @ColumnInfo
    public String profileImageUrl;

    // empty constructor needed by the Parceler library
    public User() {}

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.id = jsonObject.getLong("id");
        user.name = jsonObject.getString("name");
        user.location = jsonObject.getString("location");
        user.followers_count = jsonObject.getString("followers_count");
        user.favourites_count = jsonObject.getString("favourites_count");
        user.friends_count = jsonObject.getString("friends_count");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        return user;

    }

    public static List<User> fromJsonTweetArray(List<Tweet> tweetsFromNetwork) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < tweetsFromNetwork.size(); i++) {
            users.add(tweetsFromNetwork.get(i).user);
        }
        return users;
    }
}

