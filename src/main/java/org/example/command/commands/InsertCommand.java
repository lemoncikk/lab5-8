package org.example.command.commands;

import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.command.fields.*;
import org.example.model.Context;
import org.example.model.MusicBandBuilder;
import org.example.model.MusicGenre;

import java.util.ArrayList;

public class InsertCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        var fields = args.getFields();
        var mb = new MusicBandBuilder(new ArrayList<>(fields.subList(1, fields.size()))).build();
        ctx.insertAt((int)(fields.getFirst().getValue()), mb);
        return new CommandResult("Объект успешно добавлен", null, false);
    }

    @Override
    public String getDescription() {
        return "Вставляет новый элемент в коллекцию по указанному индексу";
    }

    @Override
    public String getName() {
        return "insert_at";
    }

    @Override
    public CommandArgs getModel() {
        var prepare = new CommandArgs();
        prepare.addField(new IntField("Индекс", "Порядковый номер элемента коллекции", true))
                .addField(new StringField("Название", "Название группы", true))
                .addField(new LongField("X", "Координата x местоположения", true))
                .addField(new IntField("Y", "Координата y местоположения", true))
                .addField(new DateField("Дата", "Дата создания группы", true))
                .addField(new LongField("Число участников", "Число участников входящих в группу", true))
                .addField(new LongField("Число песен", "Число песен выпущенных группой", true))
                .addField(new IntField("Число альбомов", "Число альбомов выпущенной группой", true))
                .addField(new EnumField<>("Музыкальный жанр", "Музыкальный жанр в котором выступает группа", true, MusicGenre.class))
                .addField(new StringField("Названия лучшего альбома", "Название лучшего альбома выпущенного группой", true))
                .addField(new LongField("Количество треков", "Количество треков в лучшем альбоме", true));
        return prepare;
    }
}
