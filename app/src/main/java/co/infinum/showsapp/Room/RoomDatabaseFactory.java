package co.infinum.showsapp.Room;

import android.arch.persistence.room.Room;
import android.content.Context;

/**
 * Created by Ivan Lovrencic on 29.7.2018..
 */

public class RoomDatabaseFactory {

    private static AppDatabase database = null;

    private RoomDatabaseFactory(){
    }

    public static AppDatabase db(Context context){
        if(database == null){
            database = Room.databaseBuilder(context,AppDatabase.class,"show-database").fallbackToDestructiveMigration().build();
        }
        return database;
    }
}
