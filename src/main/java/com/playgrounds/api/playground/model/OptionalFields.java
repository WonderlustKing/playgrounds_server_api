package com.playgrounds.api.playground.model;


public class OptionalFields {

    private String id;
    private long phone;
    private String website;

    public OptionalFields() {
    }

    public OptionalFields(String id, long phone, String website) {
        this.id = id;
        this.phone = phone;
        this.website = website;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
