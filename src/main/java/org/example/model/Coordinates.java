package org.example.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates {
    private long x = 0;
    private Integer y = 0; //Поле не может быть null

    Coordinates(long x, Integer y){
        this.x = x;
        this.y = y;
    }
    Coordinates(){}

    public long getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public void setX(long x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}
