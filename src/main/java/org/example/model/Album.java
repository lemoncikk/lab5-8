package org.example.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Album {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private long tracks; //Значение поля должно быть больше 0

    Album(){}

    Album(String name, long tracks) {
        this.name = name;
        this.tracks = tracks;
    }

    public String getName() {
        return name;
    }

    public long getTracks() {
        return tracks;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTracks(long tracks) {
        this.tracks = tracks;
    }

    @Override
    public String toString() {
        return String.format("Название:%s\nТреков на альбоме:%s", name, tracks);
    }
}
