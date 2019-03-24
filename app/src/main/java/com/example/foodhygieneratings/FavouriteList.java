package com.example.foodhygieneratings;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Favourite.class}, version = 1)
public abstract class FavouriteList extends RoomDatabase {
        public abstract FavouriteDao favDao();

}
