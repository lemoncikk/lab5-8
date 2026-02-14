package org.example.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum MusicGenre {
    @XmlEnumValue("PSYCHEDELIC_ROCK") PSYCHEDELIC_ROCK("Психоделик рок"),
    @XmlEnumValue("HIP_HOP")          HIP_HOP("Хип-хоп"),
    @XmlEnumValue("BLUES")            BLUES("Блюз");

    private final String label;

    MusicGenre(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}