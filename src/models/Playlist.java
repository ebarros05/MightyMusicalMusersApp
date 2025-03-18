package models;

import java.util.List;

public class Playlist {

    private String name;
    private List<Song> songs;

    public Playlist(String name, List<Song> songs) {

        this.name = name;
        this.songs = songs;

    }

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    @Override
    public String toString() {

        String output = this.name;

        for(Song song : songs) {

            output += " " + song.toString();

        }

        return output;

    }

}