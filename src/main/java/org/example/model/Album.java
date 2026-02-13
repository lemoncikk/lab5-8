package org.example.model;

public class Album {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private long tracks; //Значение поля должно быть больше 0

    Album(String name, long tracks) {
        this.name = name;
        this.tracks = tracks;
    }
}
