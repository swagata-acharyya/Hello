/**
 *
 */
package cordova.plugin.dbxp;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.messaging.FirebaseMessaging;
import cordova.plugin.dbxp.Notification;
import cordova.plugin.dbxp.Configuration;
import cordova.plugin.dbxp.Entities;
import cordova.plugin.dbxp.ApiClient;
import cordova.plugin.dbxp.ApiService;
import cordova.plugin.dbxp.EventBusUtil;
import cordova.plugin.dbxp.SdkUtil;
//import com.moe.pushlibrary.MoEHelper;
//import com.moe.pushlibrary.PayloadBuilder;
//import com.moengage.core.MoEngage;
//import com.moengage.push.PushManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static cordova.plugin.dbxp.SdkConstants.AUTH;
import static cordova.plugin.dbxp.SdkConstants.BANNER;
import static cordova.plugin.dbxp.SdkConstants.BANNER_KEY;
import static cordova.plugin.dbxp.SdkConstants.CONFIG_FILE;
import static cordova.plugin.dbxp.SdkConstants.CONFIG_PATH_KEY;
import static cordova.plugin.dbxp.SdkConstants.EMAIL_KEY;
import static cordova.plugin.dbxp.SdkConstants.ENTITY_LIST;
import static cordova.plugin.dbxp.SdkConstants.KEYS;
import static cordova.plugin.dbxp.SdkConstants.MENU;
import static cordova.plugin.dbxp.SdkConstants.MENU_KEY;
import static cordova.plugin.dbxp.SdkConstants.MESSAGE;
import static cordova.plugin.dbxp.SdkConstants.MESSAGE_KEY;
import static cordova.plugin.dbxp.SdkConstants.TAG;
import static cordova.plugin.dbxp.SdkConstants.THEME;
import static cordova.plugin.dbxp.SdkConstants.THEME_KEY;

public class FlashAndSdk {

    private static Configuration configuration;
    private static Application application;
    //private static MoEngage moEngage;

    private FlashAndSdk() {
        //Private constructor to disable instantiation
    }

    private static void initAnalytics(Configuration configuration) {
        // if (null == moEngage) {
        //     if (null != configuration && null != configuration.getSenderId()
        //             && null != configuration.getAnalyticsKey()) {
        //         // moEngage = new MoEngage.Builder(application, configuration.getAnalyticsKey())
        //         //         .setSenderId(configuration.getSenderId())
        //         //         .setNotificationSmallIcon(R.drawable.notification_icon)
        //         //         .setNotificationLargeIcon(R.drawable.notification_icon)
        //         //         .build();
        //         // MoEngage.initialise(moEngage);
        //         // PushManager.getInstance().setTokenObserver(new PushManager.OnTokenReceivedListener() {
        //         //     @Override
        //         //     public void onTokenReceived(String token) {
        //         //         Log.d("MOETOKEN", "Token received -- " + token);
        //         //     }
        //         // });
        //     } else {
        //         Log.w(TAG, "MoEngage config values not available for initialization");
        //     }
        // }
    }

    public static void sendToAnalytics(Context ctx, Map<String, String> params, String topic) {
        // PayloadBuilder builder = new PayloadBuilder();
        // if (null != topic && null != params) {
        //     for (Map.Entry<String, String> entry : params.entrySet()) {
        //         builder.putAttrString(entry.getKey(), entry.getValue());
        //     }
        //     MoEHelper.getInstance(ctx).trackEvent(topic, builder.build());
        // }
    }

    private static void userLoginEvent(String email) {
        // setUserId(email);
        // MoEHelper.getInstance(application).setEmail(email);
    }

    private static void setUserId(String email) {
        // MoEHelper.getInstance(application).setUniqueId(email);
    }

    public static void logoutUser() {
        // MoEHelper.getInstance(application).logoutUser();
    }

    public static Application getApplication() {
        return application;
    }

    public static void setApplication(Application application) {
        FlashAndSdk.application = application;
    }

    /**
     * Initialises SDK with configurations from file
     *
     * @param file  configurations file path
     * @param app   {@link Application} object
     * @param email ID of the user
     */
    public static void init(String file, Application app, String email) {
        application = app;
        SdkUtil.writeToSharedPref(CONFIG_PATH_KEY, file, application);
        init(email);
    }

    private static void init(String email) {
        initAnalytics(getConfiguration());
        userLoginEvent(email);
        Log.d(TAG, "Going to check if download needed");

        if (!isAllKeyPresent(application) || !SdkUtil.readFromSharedPref(EMAIL_KEY, "", application).equalsIgnoreCase(email)) {
            SdkUtil.writeToSharedPref(EMAIL_KEY, email, application);
            Log.d(TAG, "Download needed. Downloading");
            new AppConfigUpdater().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static boolean isAllKeyPresent(Context ctx) {
        return (SdkUtil.doesKeyExist(ctx, THEME_KEY) && SdkUtil.doesKeyExist(ctx, MESSAGE_KEY) && SdkUtil.doesKeyExist(ctx, MENU_KEY) && SdkUtil.doesKeyExist(ctx, BANNER_KEY));
    }

    public static void reInit() {
        new AppConfigUpdater().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Initialises SDK with configurations from file
     *
     * @param app   {@link Application} object
     * @param email ID of the user
     */
    public static void init(Application app, String email) {
        application = app;
        init(email);
    }

    private static class AppConfigUpdater extends AsyncTask<Void, Void, Configuration> {

        @Override
        protected Configuration doInBackground(Void... voids) {
            return getConfiguration();
        }

        @Override
        protected void onPostExecute(Configuration configuration) {
            super.onPostExecute(configuration);
            fetchAndStoreAppConfig(application, configuration, new HashSet<String>(Arrays.asList(THEME, MENU, BANNER, MESSAGE)), SdkUtil.readFromSharedPref(EMAIL_KEY, "", application));
            subscribeToFirebaseTopic(configuration);
        }
    }

    /**
     * Gets cached {@link Configuration} or reads from {@code filePath} and returns new instance
     *
     * @return {@link Configuration} object
     */
    public static Configuration getConfiguration() {
        if (null == configuration) {
            try {
                String configPath = SdkUtil.readFromSharedPref(CONFIG_PATH_KEY, null, application);
                if (null != configPath) {
                    String config = FileUtils.readFileToString(new File(configPath), Charset.defaultCharset());
                    configuration = SdkUtil.getObjectMapper().readValue(config, Configuration.class);
                    Log.d(TAG, "Configuration: " + configuration.toString());
                    EventBusUtil.notifyApplication(new Notification(Notification.Event.INIT_SUCCESS));
                } else {
                    String config = IOUtils.toString(application.getAssets().open(CONFIG_FILE), Charset.defaultCharset());
                    configuration = SdkUtil.getObjectMapper().readValue(config, Configuration.class);
                    Log.d(TAG, "Configuration: " + configuration.toString());
                    EventBusUtil.notifyApplication(new Notification(Notification.Event.INIT_SUCCESS));
                }
            } catch (IOException e) {
                EventBusUtil.notifyApplication(new Notification(Notification.Event.INIT_FAILURE));
                Log.e(TAG, "Error while reading configuration", e);
            }
        }
        return configuration;
    }

    /**
     * ADDED FOR TESTING PURPOSES ONLY
     */
    public static void setConfiguration(Configuration configuration) {
        FlashAndSdk.configuration = configuration;
    }

    private static void subscribeToFirebaseTopic(Configuration configuration) {
        if (null != configuration && null != configuration.getTopic()) {
            FirebaseMessaging.getInstance().subscribeToTopic(configuration.getTopic());
        } else {
            Log.e(TAG, "Could not subscribe to Firebase as topic or configuration is null");
        }
    }

    /**
     * Fetches and stores app related configs
     *
     * @param context         {@link Context} object
     * @param configuration   {@link Configuration} object
     * @param modulesToUpdate {@link Set<String>} containing modules for which config has to be updated
     * @param email           ID of the user
     */
    public static void fetchAndStoreAppConfig(final Context context, final Configuration configuration, Set<String> modulesToUpdate, String email) {
        if (null != configuration && null != modulesToUpdate) {
            ApiService apiService = ApiClient.getClient(configuration.getBaseUrl()).create(ApiService.class);
            Map<String, List> configMap = SdkUtil.getConfigMap(modulesToUpdate);
            try {
                fetchAndStoreConfig(context, apiService.getConfig(AUTH + configuration.getApiKey(), configuration.getAppId(), SdkUtil.getObjectMapper().writeValueAsString(new Entities(configMap.get(ENTITY_LIST)).getEntityList()), email), configMap.get(KEYS));
            } catch (JsonProcessingException e) {
                EventBusUtil.notifyApplication(new Notification(Notification.Event.SERVER_CONFIG_ERROR));
                Log.e(TAG, "Error while creating request for API call", e);
            }
        } else {
            EventBusUtil.notifyApplication(new Notification(Notification.Event.CONFIG_ERROR));
            Log.e(TAG, "Could not fetch app config as configuration is null or modules is null");
        }
    }

    private static void fetchAndStoreConfig(final Context context, final Call<ResponseBody> call, final List<String> keys) {
        Log.d(TAG, "Calling url: " + call.request().url());
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (null != response && null != response.body()) {
                        SdkUtil.writeConfigToSharedPref(keys, response.body().string(), context);
                        EventBusUtil.notifyApp(keys);
                    } else {
                        EventBusUtil.notifyApplication(new Notification(Notification.Event.SERVER_CONFIG_ERROR));
                    }
                } catch (IOException e) {
                    EventBusUtil.notifyApplication(new Notification(Notification.Event.SERVER_CONFIG_ERROR));
                    Log.e(TAG, "Error while reading config from server", e);
                } catch (JSONException e) {
                    EventBusUtil.notifyApplication(new Notification(Notification.Event.SERVER_CONFIG_ERROR));
                    Log.e(TAG, "Error while parsing config from server", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error while fetching config from server: " + call.request().url(), t);
                EventBusUtil.notifyApplication(new Notification(Notification.Event.API_ERROR));
            }
        });
    }

    /**
     * @return theme config
     */
    public static String getTheme() {
        return SdkUtil.readFromSharedPref(THEME_KEY, null, application);
    }

    /**
     * @return menu config
     */
    public static String getMenu() {
        return SdkUtil.readFromSharedPref(MENU_KEY, null, application);
    }

    /**
     * @return banner config
     */
    public static String getBanner() {
        return SdkUtil.readFromSharedPref(BANNER_KEY, null, application);
    }

    /**
     * @return message config
     */
    public static String getMessage() {
        return SdkUtil.readFromSharedPref(MESSAGE_KEY, null, application);
    }
}

