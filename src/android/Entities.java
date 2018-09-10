package cordova.plugin.dbxp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Entities {

    @JsonProperty("entities")
    private List<String> entityList = null;

    public Entities(List<String> entityList) {
        this.entityList = entityList;
    }

    public List<String> getEntityList() {
        return entityList;
    }
}