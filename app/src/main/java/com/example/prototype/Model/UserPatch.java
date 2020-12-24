package com.example.prototype.Model;

public class UserPatch {
    private String id;
    private String value;

    public UserPatch(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public UserPatch() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}