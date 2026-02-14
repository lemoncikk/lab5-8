package org.example.command.commands;

import org.example.command.Command;
import org.example.command.CommandArgs;
import org.example.command.CommandResult;
import org.example.command.fields.*;
import org.example.model.Context;
import org.example.model.MusicBandBuilder;
import org.example.model.MusicGenre;

public class AddCommand implements Command {
    @Override
    public CommandResult execute(Context ctx, CommandArgs args) {
        var fields = args.getFields();
        var mb = new MusicBandBuilder(fields).build();
        ctx.add(mb);
        return new CommandResult("Объект успешно добавлен в коллекцию", null, false);
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public CommandArgs getModel() {
        var prepare = new CommandArgs();
        prepare.addField(new StringField("Название", "Название группы", true))
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
