package org.example.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import org.example.exceptions.CommandExecutionException;

import java.io.Serial;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates implements Serializable {
    @XmlTransient
    @Serial
    private static final long serialVersionUID = 1L;
    private long x = 0;
    private Integer y = 0; //Поле не может быть null

    Coordinates(){}

    Coordinates(long x, Integer y){
        this.x = x;
        this.y = y;
    }

    long getX() {
        return x;
    }

    Integer getY() {
        return y;
    }

    void setX(Long x) {
        if (x == null) throw new CommandExecutionException("Expected not null value, but founded null");
        this.x = x;
    }

    void setY(Integer y) {
        if (y == null) throw new CommandExecutionException("Expected not null value, but founded null");
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}
