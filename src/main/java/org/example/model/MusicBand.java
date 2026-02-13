package org.example.model;

import java.time.ZonedDateTime;

public class MusicBand {
    static int next_id = 1;
    private final int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name = null; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates = null; //Поле не может быть null
    private java.time.ZonedDateTime creationDate = null; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long numberOfParticipants = 0; //Значение поля должно быть больше 0
    private long singlesCount = 0; //Значение поля должно быть больше 0
    private Integer albumsCount = null; //Поле может быть null, Значение поля должно быть больше 0
    private MusicGenre genre = null; //Поле может быть null
    private Album bestAlbum = null; //Поле может быть null

    MusicBand() {
        id = next_id++;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public long getNumberOfParticipants() {
        return numberOfParticipants;
    }

    void setNumberOfParticipants(long numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public long getSinglesCount() {
        return singlesCount;
    }

    void setSinglesCount(long singlesCount) {
        this.singlesCount = singlesCount;
    }

    public Integer getAlbumsCount() {
        return albumsCount;
    }

    void setAlbumsCount(Integer albumsCount) {
        this.albumsCount = albumsCount;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    void setGenre(MusicGenre genre) {
        this.genre = genre;
    }

    public Album getBestAlbum() {
        return bestAlbum;
    }

    void setBestAlbum(Album bestAlbum) {
        this.bestAlbum = bestAlbum;
    }
}
