package model.tutor;

import java.util.UUID;

public class Answer extends Message {

    public Answer(UUID id, String code, String territoryXML) {
        super(id, code, territoryXML);
    }
}
