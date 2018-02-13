package com.example.fady.movieimdb.Utilities;

import com.example.fady.movieimdb.DataFeed.movies.Genre;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Fady on 2/3/2018.
 */

public class MovieGenres {

    private static HashMap<Integer, String> genresMap;

    public static boolean isGenresListLoaded() {
        return (genresMap != null);
    }

    public static void loadGenresList(List<Genre> genres) {
        if (genres == null) return;
        genresMap = new HashMap<>();
        for (Genre genre : genres) {
            genresMap.put(genre.getId(), genre.getGenreName());
        }
    }

    public static String getGenreName(Integer genreId) {
        if (genreId == null) return null;
        return genresMap.get(genreId);
    }
}
