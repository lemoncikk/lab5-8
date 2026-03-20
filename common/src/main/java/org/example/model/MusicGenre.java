package org.example.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import org.example.model.enumUtils.LabelledEnum;

@XmlEnum
public enum MusicGenre implements LabelledEnum {
    @XmlEnumValue("PSYCHEDELIC_ROCK") PSYCHEDELIC_ROCK("Психоделик рок", 1),
    @XmlEnumValue("HIP_HOP")          HIP_HOP("Хип-хоп", 2),
    @XmlEnumValue("BLUES")            BLUES("Блюз", 3);

    private final String label;
    private final int id;

    MusicGenre(String label, int id) {
        this.label = label;
        this.id = id;
    }

    @Override
    public String toString() {
        return label;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }
}