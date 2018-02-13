package com.example.fady.movieimdb.DataFeed.tvShow;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Fady on 2/1/2018.
 */

public class Network {

    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;

    public Network(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
