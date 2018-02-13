package com.example.fady.movieimdb.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fady.movieimdb.DataFeed.movies.MovieBrief;
import com.example.fady.movieimdb.DataFeed.tvShow.TVShowBrief;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fady on 2/4/2018.
 */

public class FavouriteItems {


    public static void addMovieToFav(Context context, Integer movieId, String posterPath, String name) {
        if (movieId == null) return;
        DbHelper databaseHelper = new DbHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        if (!isMovieFav(context, movieId)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbHelper.MOVIE_ID, movieId);
            contentValues.put(DbHelper.POSTER_PATH, posterPath);
            contentValues.put(DbHelper.NAME, name);
            database.insert(DbHelper.FAV_MOVIES_TABLE_NAME, null, contentValues);
        }
        database.close();
    }


    public static boolean isMovieFav(Context context, Integer movieId) {
        if (movieId == null) return false;

        DbHelper databaseHelper = new DbHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        boolean isMovieFav;

        Cursor cursor = database.query(DbHelper.FAV_MOVIES_TABLE_NAME,
                null, DbHelper.MOVIE_ID + " = " + movieId, null, null, null,
                null);

        if (cursor.getCount() == 1)
            isMovieFav = true;
        else
        {  isMovieFav = false;}

        cursor.close();
        database.close();
        return isMovieFav;
    }


    public static List<MovieBrief> getFavMovieBriefs(Context context) {

        DbHelper databaseHelper = new DbHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        List<MovieBrief> favMovies = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.FAV_MOVIES_TABLE_NAME,
                null, null, null, null, null, DbHelper.ID + " DESC");

        while (cursor.moveToNext()) {

            int movieId = cursor.getInt(cursor.getColumnIndex(DbHelper.MOVIE_ID));
            String posterPath = cursor.getString(cursor.getColumnIndex(DbHelper.POSTER_PATH));
            String name = cursor.getString(cursor.getColumnIndex(DbHelper.NAME));

            favMovies.add(new MovieBrief(null, movieId, null, null, name,
                    null, posterPath, null, null, null,
                    null, null, null, null));
        }
        cursor.close();
        database.close();
        return favMovies;
    }


    public static void removeMovieFromFav(Context context, Integer movieId) {

        if (movieId == null) return;

        DbHelper databaseHelper = new DbHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        if (isMovieFav(context, movieId)) {
            database.delete(DbHelper.FAV_MOVIES_TABLE_NAME, DbHelper.MOVIE_ID + " = " + movieId, null);
        }
        database.close();
    }



    public static void addTVShowToFav(Context context, Integer tvShowId, String posterPath, String name) {

        if (tvShowId == null) return;

        DbHelper databaseHelper = new DbHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        if (!isTVShowFav(context, tvShowId)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbHelper.TV_SHOW_ID, tvShowId);
            contentValues.put(DbHelper.POSTER_PATH, posterPath);
            contentValues.put(DbHelper.NAME, name);
            database.insert(DbHelper.FAV_TV_SHOWS_TABLE_NAME, null, contentValues);
        }
        database.close();
    }


    public static boolean isTVShowFav(Context context, Integer tvShowId) {

        if (tvShowId == null) return false;

        DbHelper databaseHelper = new DbHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        boolean isTVShowFav;

        Cursor cursor = database.query(DbHelper.FAV_TV_SHOWS_TABLE_NAME, null,
                DbHelper.TV_SHOW_ID + " = " + tvShowId, null, null, null, null);

        if (cursor.getCount() == 1)
            isTVShowFav = true;
        else
        {  isTVShowFav = false;}

        cursor.close();
        database.close();
        return isTVShowFav;
    }


    public static List<TVShowBrief> getFavTVShowBriefs(Context context) {

        DbHelper databaseHelper = new DbHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        List<TVShowBrief> favTVShows = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.FAV_TV_SHOWS_TABLE_NAME,
                null, null, null, null, null, DbHelper.ID + " DESC");

        while (cursor.moveToNext()) {

            int tvShowId = cursor.getInt(cursor.getColumnIndex(DbHelper.TV_SHOW_ID));
            String posterPath = cursor.getString(cursor.getColumnIndex(DbHelper.POSTER_PATH));
            String name = cursor.getString(cursor.getColumnIndex(DbHelper.NAME));

            favTVShows.add(new TVShowBrief(null, tvShowId, name,
                    null, null, posterPath, null, null, null,
                    null, null, null, null));
        }

        cursor.close();
        database.close();
        return favTVShows;
    }

    public static void removeTVShowFromFav(Context context, Integer tvShowId) {

        if (tvShowId == null) return;

        DbHelper databaseHelper = new DbHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        if (isTVShowFav(context, tvShowId)) {
            database.delete(DbHelper.FAV_TV_SHOWS_TABLE_NAME, DbHelper.TV_SHOW_ID + " = " + tvShowId, null);
        }

        database.close();
    }
}
