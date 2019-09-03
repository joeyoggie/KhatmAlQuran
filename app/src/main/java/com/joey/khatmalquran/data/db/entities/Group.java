package com.joey.khatmalquran.data.db.entities;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joey on 11/25/2017.
 */

public class Group {
    private long id, adminID, deadline;
    private String name;
    private List<Part> parts;
    private List<Long> members;
    /*Map<String, Long> partsAssociations; //part number, user ID
    Map<String, Integer> partsStates; //part number, state
    Map<String, Long> partsTimestamps; //part number, timestamp*/

    public Group(){

    }

    public Group(long id, String name, List<Part> parts, long deadline, long groupAdminID, List<Long> members){
        this.id = id;
        this.name = name;
        this.parts = parts;
        this.deadline = deadline;
        this.adminID = groupAdminID;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getID() {
        return this.id;
    }

    public void setID(long groupID) {
        this.id = groupID;
    }

    public long getAdminID() {
        return adminID;
    }

    public void setAdminID(long adminID) {
        this.adminID = adminID;
    }

    public void setParts(List<Part> parts){
        this.parts = parts;
    }

    public List<Part> getParts(){ return this.parts; }

    public List<Long> getMembers() {
        return members;
    }

    public void setMembers(List<Long> members) {
        this.members = members;
    }

    public int getNumberOfPartsUntaken(){
        int number = 0;
        /*if(partsStates != null) {
            for (int state : partsStates.values()) {
                if (state == Part.PART_STATE_UNTAKEN) {
                    number++;
                }
            }
        }*/
        if(parts != null) {
            for (Part part : parts) {
                if (part.getState() == Part.PART_STATE_UNTAKEN) {
                    number++;
                }
            }
        }
        return number;
    }

    public int getNumberOfPartsTaken(){
        int number = 0;
        /*if(partsStates != null) {
            for (int state : partsStates.values()) {
                if (state == Part.PART_STATE_TAKEN) {
                    number++;
                }
            }
        }*/
        if(parts != null) {
            for (Part part : parts) {
                if (part.getState() == Part.PART_STATE_TAKEN) {
                    number++;
                }
            }
        }
        return number;
    }

    public int getNumberOfPartsCompleted(){
        int number = 0;
        /*if(partsStates != null) {
            for (int state : partsStates.values()) {
                if (state == Part.PART_STATE_COMPLETED) {
                    number++;
                }
            }
        }*/
        if(parts != null) {
            for (Part part : parts) {
                if (part.getState() == Part.PART_STATE_COMPLETED) {
                    number++;
                }
            }
        }
        return number;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("groupID", this.id);
        result.put("name", this.name);
        result.put("parts", this.parts);
        /*result.put("partsAssociations", this.partsAssociations);
        result.put("partsStates", this.partsStates);
        result.put("partsTimestamps", this.partsTimestamps);*/
        result.put("deadline", this.deadline);
        result.put("groupAdminID", this.adminID);
        result.put("members", this.members);
        return result;
    }
}
