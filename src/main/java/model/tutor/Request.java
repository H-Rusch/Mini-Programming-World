package model.tutor;

import java.util.UUID;

public class Request extends Message {

    public Request(UUID id, String code, String territoryXML) {
        super(id, code, territoryXML);
    }
}
