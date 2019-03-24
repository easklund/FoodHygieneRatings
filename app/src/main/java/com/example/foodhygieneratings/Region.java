package com.example.foodhygieneratings;

public class Region {
    private int id;
    private String name;
    private String nameKey;

    public Region(int id, String name, String nameKey) {
        this.id = id;
        this.name = name;
        this.nameKey = nameKey;
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

    public String getNameKey() {
        return nameKey;
    }

    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }

    @Override
    public String toString() {
        return name;
    }
}
