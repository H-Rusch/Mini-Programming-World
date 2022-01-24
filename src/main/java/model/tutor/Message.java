package model.tutor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Message object containing the code for a program and the string representation of a territory.
 * The message is identified by a unique id.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 123L;

    private UUID id;
    private String code;
    private String territoryXML;

    public Message(UUID id, String code, String territoryXML) {
        this.id = id;
        this.code = code;
        this.territoryXML = territoryXML;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTerritoryXML() {
        return territoryXML;
    }
}
