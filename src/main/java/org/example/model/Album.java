package org.example.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import org.example.command.exceptions.CommandExecutionException;

@XmlAccessorType(XmlAccessType.FIELD)
/**
 * Класс альбома группы
 * Хранит <b>name</b>(название альбома) и <b>tracks</b>(кол-во треков)
 * @see MusicBand
 */
public class Album {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private long tracks; //Значение поля должно быть больше 0

    /**
     * Package-private конструктор
     * <b>НЕ ИСПОЛЬЗУЙТЕ ЕГО ЯВНО, ИСПОЛЬЗУЙТЕ ПУБЛИЧНЫЙ КОНСТРУКТОР</b>
     */
    Album(){}
    /**
     * Публичный конструктор
     * @param name - название альбома
     * @param tracks - кол-во треков в альбоме
     */
    public Album(String name, long tracks) throws CommandExecutionException {
        this();
        setName(name);
        setTracks(tracks);
    }

    /**
     * @return Название альбома
     */
    public String getName() {
        return name;
    }
    /**
     * @return Кол-во треков в альбоме
     */
    public long getTracks() {
        return tracks;
    }
    /**
     * @param name - название альбома
     */
    public void setName(String name) {
        if (name == null || name.isEmpty())
            throw new CommandExecutionException("Album name must be not null and not empty");
        this.name = name;
    }
    /**
     * @param tracks - кол-во треков в альбоме
     */
    public void setTracks(Long tracks) {
        if (tracks == null || tracks < 1) throw new CommandExecutionException("Album tracks count must be greater than 0");
        this.tracks = tracks;
    }

    @Override
    public String toString() {
        return String.format("Название:%s\nТреков на альбоме:%s", name, tracks);
    }
}
