package com.example.foodhygieneratings;

import java.util.HashMap;

public class Business {
    private int id;
    private String name;
    private HashMap<String, String> links;

    public Business(int id, String name, HashMap<String, String> links) {
        this.id = id;
        this.name = name;
        this.links = links;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, String> getLinks() {
        return links;
    }

    public void setLinks(HashMap<String, String> links) {
        this.links = links;
    }

    public String toString(){
        return name;
    }
}
