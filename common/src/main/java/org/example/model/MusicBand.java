package org.example.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.example.exceptions.ValidationException;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Класс-представление музыкальной группы
 * Все сеттеры - package-private
 * <b>Не пытайтесь сконструировать объект данного класса, используйте MusicBandBuilder для инициализации, редактирования и конструирования</b>
 * @see MusicBandBuilder
 */
@XmlRootElement(name = "MusicBand")
@XmlAccessorType(XmlAccessType.FIELD)
public class MusicBand implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    static int next_id = 1;
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name = null; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates = null; //Поле не может быть null
    @XmlJavaTypeAdapter(ZonedDateTimeAdapter.class)
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

    /**
     * <b>ДАННЫЙ МЕТОД НЕОБХОДИМ ТОЛЬКО ДЛЯ ДЕСЕРИАЛИЗАЦИИ, НЕ ИСПОЛЬЗУЙТЕ ЕГОВ КОДЕ</b>
     * @param id  уникальный id поля
     */
    void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        if (name == null || name.isEmpty())
            throw new ValidationException("Expected not null value, but founded null");
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    void setCoordinates(Coordinates coordinates) {
        if (coordinates == null)
            throw new ValidationException("Expected not null value, but founded null");
        this.coordinates = coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    void setCreationDate(ZonedDateTime creationDate) {
        if (creationDate == null)
            throw new ValidationException("Expected not null value, but founded null");
        this.creationDate = creationDate;
    }

    public long getNumberOfParticipants() {
        return numberOfParticipants;
    }

    void setNumberOfParticipants(long numberOfParticipants) {
        if (numberOfParticipants < 1)
            throw new ValidationException("Expected value greater than 1");
        this.numberOfParticipants = numberOfParticipants;
    }

    public long getSinglesCount() {
        return singlesCount;
    }

    void setSinglesCount(long singlesCount) {
        if (singlesCount < 1)
            throw new ValidationException("Expected value greater than 1");
        this.singlesCount = singlesCount;
    }

    public Integer getAlbumsCount() {
        return albumsCount;
    }

    void setAlbumsCount(Integer albumsCount) {
        if (albumsCount < 1)
            throw new ValidationException("Expected value greater than 1");
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

    public ArrayList<String> getFieldsStrings() {
        var list = new ArrayList<String>();
        list.add(String.valueOf(id));
        list.add(name);
        list.add(String.valueOf(coordinates.getX()));
        list.add(String.valueOf(coordinates.getY()));
        list.add(creationDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        list.add(String.valueOf(numberOfParticipants));
        list.add(String.valueOf(singlesCount));
        list.add(String.valueOf(albumsCount));
        list.add(genre.toString());
        list.add(bestAlbum.getName());
        list.add(String.valueOf(bestAlbum.getTracks()));
        return list;
    }

    @Override
    public String toString() {
        return String.format(
                "ID:%d\nНазвание группы:%s\nКоординаты: %s" +
                        "\nДата возникновения:%s\nЧисло участников:%d\n" +
                        "Число синглов:%d\nЧисло альбомов:%d\nМузыкальный жанр:\n" +
                        "Лучший альбом: %s ",
                id, name, coordinates.toString(), creationDate.toString(), numberOfParticipants,
                singlesCount, albumsCount, genre.toString(), bestAlbum.toString()
                );
    }
}
