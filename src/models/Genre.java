package models;

public class Genre {

    private int id;
    private String genre;

    public Genre(int id, String genre) {

        this.id = id;
        this.genre = genre;

    }

    public int getId() {return this.id;}
    public String getGenre() {return this.genre;}

    @Override
    public String toString() {

        return "Genre: " + this.genre;

    }

}