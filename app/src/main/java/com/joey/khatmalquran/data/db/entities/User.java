package com.joey.khatmalquran.data.db.entities;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joey on 11/25/2017.
 */

public class User {
    private long id;
    private String name, fcmToken;
    private List<Integer> groups;

    public User(){

    }

    public User(long id, String name, List<Integer> groups, String fcmToken){
        this.id = id;
        this.name = name;
        this.groups = groups;
        this.fcmToken = fcmToken;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getGroups() {
        return groups;
    }

    public void setGroups(List<Integer> groups) {
        this.groups = groups;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userID", this.id);
        result.put("name", this.name);
        result.put("groups", this.groups);
        result.put("fcmToken", this.fcmToken);
        return result;
    }
}
