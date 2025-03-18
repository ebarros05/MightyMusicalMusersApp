package models;

import java.sql.Date;

public class Song {

    private int id;
    private String title;
    private int length;
    private Date releaseDate;
    // Should we connect this back to the album(s) the song appears on?
    // Ex. List<Album> appearsOn;

    public Song(int id, String title, int length, Date releaseDate) {

        this.id = id;
        this.title = title;
        this.length = length;
        this.releaseDate = releaseDate;

    }

    public int getId() {return this.id;}
    public String getTitle() {return this.title;}
    public int getLength() {return this.length;}
    public Date getReleaseDate() {return this.releaseDate;}

    @Override
    public String toString() {

        return this.id + "; " + this.title + "; " + this.length + "; " + this.releaseDate;

    }

}