package model.example;

public class Example {

    private final String name;
    private final String code;
    private final String territoryString;
    private final String[] tags;

    public Example(String name, String code, String territoryString, String[] tags) {
        this.name = name;
        this.code = code;
        this.territoryString = territoryString;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getTerritoryString() {
        return territoryString;
    }

    public String[] getTags() {
        return tags;
    }
}
