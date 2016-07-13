package com.tiago.onlyjokes;

/**
 * Created by PRABHU on 11/12/2015.
 */
public class DatabaseModel {
    private String _id;
    private String joke;
    private String tags;
    private String source;
    private String category;
    private String email;

    public String get_id() {return _id;}

    public void set_id(String joke) {
        this._id = _id;
    }

    public String getJoke() {return joke;}

    public void setJoke(String joke) {
        this.joke = joke;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
