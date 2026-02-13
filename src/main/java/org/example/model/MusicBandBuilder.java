package org.example.model;

import org.example.Commands.Fields.*;

import java.util.ArrayList;

public class MusicBandBuilder {
    MusicBand mb = new MusicBand();
    public MusicBandBuilder(ArrayList<Field<?>> fields) {
        mb.setName(((StringField)(fields.get(0))).getValue());
        mb.setCoordinates(new Coordinates(
                ((LongField)(fields.get(1))).getValue(),
                (((IntField)(fields.get(2)))).getValue()
        ));
        mb.setCreationDate(((DataField)(fields.get(3))).getValue());
        mb.setNumberOfParticipants(((LongField)(fields.get(4))).getValue());
        mb.setSinglesCount(((LongField)(fields.get(5))).getValue());
        mb.setAlbumsCount(((IntField)(fields.get(6))).getValue());
        mb.setGenre(((EnumField<MusicGenre>)(fields.get(7))).getValue());
        mb.setBestAlbum(new Album(
                ((StringField)(fields.get(8))).getValue(),
                ((LongField)(fields.get(9))).getValue()
        ));
    }
    public MusicBand build() {
        return mb;
    }
}
