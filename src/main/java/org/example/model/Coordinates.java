package org.example.model;

public class Coordinates {
    private long x;
    private Integer y; //Поле не может быть null

    Coordinates(long x, Integer y){
        this.x = x;
        this.y = y;
    }
}
