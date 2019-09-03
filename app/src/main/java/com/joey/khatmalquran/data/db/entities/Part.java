package com.joey.khatmalquran.data.db.entities;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joey on 11/26/2017.
 */

public class Part {
    public static final int PART_STATE_UNTAKEN = 0;
    public static final int PART_STATE_TAKEN = 1;
    public static final int PART_STATE_COMPLETED = 2;

    private String partName, associatedPerson;
    private int partID, state;
    private long lastActionTimestamp, groupID, associatedPersonID;

    public Part(){

    }

    public Part(int id, String partName, long groupID, String associatedPerson, int state, long lastActionTimestamp){
        this.partID = id;
        this.partName = partName;
        this.groupID = groupID;
        this.associatedPerson = associatedPerson;
        this.state = state;
        this.lastActionTimestamp = lastActionTimestamp;
    }

    public int getPartID() {
        return partID;
    }

    public void setPartID(int id) {
        this.partID = id;
    }

    public long getGroupID() {
        return groupID;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public long getAssociatedPersonID() {
        return associatedPersonID;
    }

    public void setAssociatedPersonID(long associatedPersonID) {
        this.associatedPersonID = associatedPersonID;
    }

    public long getLastActionTimestamp() {
        return lastActionTimestamp;
    }

    public void setLastActionTimestamp(long lastActionTimestamp) {
        this.lastActionTimestamp = lastActionTimestamp;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getAssociatedPerson() {
        return associatedPerson;
    }

    public void setAssociatedPerson(String associatedPerson) {
        this.associatedPerson = associatedPerson;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("partID", this.partID);
        result.put("partName", this.partName);
        result.put("state", this.state);
        result.put("groupID", this.groupID);
        result.put("associatedPerson", this.associatedPerson);
        result.put("associatedPersonID", this.associatedPersonID);
        result.put("lastActionTimestamp", lastActionTimestamp);
        return result;
    }
}
