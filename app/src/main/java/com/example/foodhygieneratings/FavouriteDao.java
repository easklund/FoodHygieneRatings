package com.example.foodhygieneratings;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface FavouriteDao {

        @Insert
        void insertBook(Favourite fav);

        @Query("SELECT * FROM favourite WHERE FHRSID=:fhrsid LIMIT 1")
        Favourite retrieveFavfromFHRSID(int fhrsid);

}
