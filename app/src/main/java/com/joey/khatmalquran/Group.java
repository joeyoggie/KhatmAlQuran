package com.joey.khatmalquran;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joey on 11/25/2017.
 */

public class Group {
    long groupID, deadline;
    String name;
    List<Part> parts;
    long groupAdminID;
    List<Long> members;
    /*Map<String, Long> partsAssociations; //part number, user ID
    Map<String, Integer> partsStates; //part number, state
    Map<String, Long> partsTimestamps; //part number, timestamp*/

    public Group(){

    }

    public Group(long id, String name, List<Part> parts, long deadline, long groupAdminID, List<Long> members/*, Map<String, Long> partsAssociations, Map<String, Integer> partsStates, Map<String, Long> partsTimestamps*/){
        this.groupID = id;
        this.name = name;
        this.parts = parts;
        this.deadline = deadline;
        this.groupAdminID = groupAdminID;
        this.members = members;
        /*this.partsAssociations = partsAssociations;
        this.partsStates = partsStates;
        this.partsTimestamps = partsTimestamps;*/
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getGroupID() {
        return this.groupID;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public long getGroupAdminID() {
        return groupAdminID;
    }

    public void setGroupAdminID(long groupAdminID) {
        this.groupAdminID = groupAdminID;
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

    /*public Map<String, Long> getPartsAssociations() {
        return partsAssociations;
    }

    public void setPartsAssociations(Map<String, Long> partsAssociations) {
        this.partsAssociations = partsAssociations;
    }

    public Map<String, Long> getPartsTimestamps() {
        return partsTimestamps;
    }

    public void setPartsTimestamps(Map<String, Long> patsTimestamps) {
        this.partsTimestamps = patsTimestamps;
    }

    public Map<String, Integer> getPartsStates() {
        return partsStates;
    }

    public void setPartsStates(Map<String, Integer> partsStates) {
        this.partsStates = partsStates;
    }*/

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
        result.put("groupID", this.groupID);
        result.put("name", this.name);
        result.put("parts", this.parts);
        /*result.put("partsAssociations", this.partsAssociations);
        result.put("partsStates", this.partsStates);
        result.put("partsTimestamps", this.partsTimestamps);*/
        result.put("deadline", this.deadline);
        result.put("groupAdminID", this.groupAdminID);
        result.put("members", this.members);
        return result;
    }
}
