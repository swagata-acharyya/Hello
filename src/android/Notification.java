package cordova.plugin.dbxp;

public class Notification {

    public enum Event {
        INIT_SUCCESS, INIT_FAILURE, THEME, BANNER, MESSAGE, MENU, CONFIG_ERROR, SERVER_CONFIG_ERROR, API_ERROR, UNKNOWN_MODULE
    }

    private Event event;
    private String data;

    public Notification(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}