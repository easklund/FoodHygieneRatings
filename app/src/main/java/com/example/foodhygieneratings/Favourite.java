package com.example.foodhygieneratings;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Favourite {
    @PrimaryKey
    @NonNull private int FHRSID;

    @ColumnInfo(name = "establishmentName")
    private String Name;

    public int getFHRSID() {
        return FHRSID;
    }

    public void setFHRSID(int FHRSID) {
        this.FHRSID = FHRSID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }
}
