package com.example.duan1bookapp.models;

public class ModelCategory2 {

    //make sure to use spellings for model variables as in firebase

    String id, category, uid;
    long timestamp;

    public ModelCategory2() {

    }
    //parametrized

    public ModelCategory2(String id, String category, String uid, long timestamp) {

        this.id = id;
        this.category = category;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    /*---Getter/Setter---*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
