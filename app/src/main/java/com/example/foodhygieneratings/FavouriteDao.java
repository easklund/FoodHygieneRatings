package com.example.foodhygieneratings;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface FavouriteDao {

        @Insert
        void insertFavourite(Favourite fav);

        @Query("SELECT * FROM favourite WHERE FHRSID=:fhrsid LIMIT 1")
        Favourite retrieveFavfromFHRSID(int fhrsid);

        @Query("SELECT * FROM favourite")
        Favourite[] retrieveAllFave();

        @Query("DELETE FROM favourite WHERE FHRSID=:fhrsid")
        void deleteFavFRomFHRSID(int fhrsid);

}
