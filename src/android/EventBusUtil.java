package cordova.plugin.dbxp;

import cordova.plugin.dbxp.FlashAndSdk;
import cordova.plugin.dbxp.Notification;

//import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static cordova.plugin.dbxp.SdkConstants.BANNER_KEY;
import static cordova.plugin.dbxp.SdkConstants.MENU_KEY;
import static cordova.plugin.dbxp.SdkConstants.MESSAGE_KEY;
import static cordova.plugin.dbxp.SdkConstants.THEME_KEY;

public class EventBusUtil {

    private EventBusUtil() {
        // Prevent instantiation
    }

    /**
     * Notifies app based on {@link List<String>} keys
     *
     * @param keys {@link List<String>} the events to be notified
     * @return {@link List<String>} with keys that were notified to the app
     */
    public static List<String> notifyApp(List<String> keys) {
        List<String> notifiedKeys = new ArrayList<>();
        for (String key : keys) {
            switch (key) {
                case THEME_KEY:
                    Notification themeEvent = new Notification(Notification.Event.THEME);
                    themeEvent.setData(FlashAndSdk.getTheme());
                    notifyApplication(themeEvent);
                    notifiedKeys.add(THEME_KEY);
                    break;
                case MENU_KEY:
                    Notification menuEvent = new Notification(Notification.Event.MENU);
                    menuEvent.setData(FlashAndSdk.getMenu());
                    notifyApplication(menuEvent);
                    notifiedKeys.add(MENU_KEY);
                    break;
                case BANNER_KEY:
                    Notification bannerEvent = new Notification(Notification.Event.BANNER);
                    bannerEvent.setData(FlashAndSdk.getBanner());
                    notifyApplication(bannerEvent);
                    notifiedKeys.add(BANNER_KEY);
                    break;
                case MESSAGE_KEY:
                    Notification notificationEvent = new Notification(Notification.Event.MESSAGE);
                    notificationEvent.setData(FlashAndSdk.getMessage());
                    notifyApplication(notificationEvent);
                    notifiedKeys.add(MESSAGE_KEY);
                    break;
                default:
                    // Do nothing
                    break;
            }
        }
        return notifiedKeys;
    }

    /**
     * Notifies the app with an event
     *
     * @param notification
     * @return {@code true} when notification event is sent
     */
    public static boolean notifyApplication(Notification notification) {
        //EventBus.getDefault().post(notification);
        return true;
    }
}