package cordova.plugin.dbxp;

import android.app.Application;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import cordova.plugin.dbxp.FlashAndSdk;
import cordova.plugin.dbxp.SdkUtil;
// import com.moengage.push.PushManager;
// import com.moengage.pushbase.push.MoEngageNotificationUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static cordova.plugin.dbxp.SdkConstants.CHANGED_MODULES_KEY;
import static cordova.plugin.dbxp.SdkConstants.EMAIL_KEY;
import static cordova.plugin.dbxp.SdkConstants.TAG;

public class FlashSdkMessageService extends FirebaseMessagingService {

    private Application application;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // if (remoteMessage != null) {
        //     Map<String, String> pushPayload = remoteMessage.getData();
        //     if (MoEngageNotificationUtils.isFromMoEngagePlatform(pushPayload)) {
        //         PushManager.getInstance().getPushHandler().handlePushPayload(getApplicationContext(), pushPayload);
        //     } else {
                handleNotification(remoteMessage);
        //     }
        // }
    }

    private void handleNotification(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Data: " + remoteMessage.getData());
        if (null != remoteMessage.getData() && remoteMessage.getData().size() > 0 && remoteMessage.getData().containsKey(CHANGED_MODULES_KEY)) {
            try {
                if (null == application) {
                    application = getApplication();
                }
                Set<String> modulesToUpdate = new HashSet<>();
                JSONArray jsonArray = new JSONArray(remoteMessage.getData().get(CHANGED_MODULES_KEY));
                for (int i = 0; i < jsonArray.length(); i++) {
                    modulesToUpdate.add(jsonArray.getString(i));
                }

                // This scenario occurs when the application is not running and the app receives a notification.
                // On some devices, this service starts in the background and this check ensures that the app
                // can update configurations in the background without any issues.
                if (null == FlashAndSdk.getApplication()) {
                    FlashAndSdk.setApplication(application);
                }
                FlashAndSdk.fetchAndStoreAppConfig(FlashAndSdk.getApplication(), FlashAndSdk.getConfiguration(), modulesToUpdate, SdkUtil.readFromSharedPref(EMAIL_KEY, "", application));
            } catch (JSONException e) {
                Log.e(TAG, "Error while reading JSON for " + CHANGED_MODULES_KEY, e);
            }
        }
    }

    /**
     * NOTE: THIS IS REQUIRED FOR TESTING PURPOSES ONLY
     *
     * @return {@link Application} object
     */
    public Application getContext() {
        return application;
    }

    /**
     * NOTE: THIS IS REQUIRED FOR TESTING PURPOSES ONLY
     *
     * @param application {@link Application} object
     */
    public void setApplication(Application application) {
        this.application = application;
    }
}