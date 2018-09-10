package cordova.plugin.dbxp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cordova.plugin.dbxp.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cordova.plugin.dbxp.SdkConstants.BANNER;
import static cordova.plugin.dbxp.SdkConstants.BANNER_KEY;
import static cordova.plugin.dbxp.SdkConstants.DATA;
import static cordova.plugin.dbxp.SdkConstants.ENTITY_LIST;
import static cordova.plugin.dbxp.SdkConstants.KEYS;
import static cordova.plugin.dbxp.SdkConstants.MENU;
import static cordova.plugin.dbxp.SdkConstants.MENU_KEY;
import static cordova.plugin.dbxp.SdkConstants.MESSAGE;
import static cordova.plugin.dbxp.SdkConstants.MESSAGE_KEY;
import static cordova.plugin.dbxp.SdkConstants.TAG;
import static cordova.plugin.dbxp.SdkConstants.THEME;
import static cordova.plugin.dbxp.SdkConstants.THEME_KEY;

public class SdkUtil {

    private static ObjectMapper objectMapper;
    private static final String SHARED_PREF_KEY = "SdkSharedPref";

    private SdkUtil() {
        // Prevent instantiation
    }

    /**
     * Creates and returns new {@link ObjectMapper} instance if not cached already, otherwise returns cached {@link ObjectMapper} instance
     *
     * @return {@link ObjectMapper} instance
     */
    public static ObjectMapper getObjectMapper() {
        if (null == objectMapper) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return objectMapper;
    }

    private static SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
    }

    /**
     * Extracts and stores value of response for config
     *
     * @param keys    List<String> to store in {@link SharedPreferences}
     * @param config  config
     * @param context {@link Context} object
     * @return the modules that were successfully stored
     * @throws JSONException if response is not well formed
     */
    public static List<String> writeConfigToSharedPref(List<String> keys, String config, Context context) throws JSONException {
        List<String> modulesStored = new ArrayList<>();
        if (null != config) {
            JSONObject jsonObject = new JSONObject(config);
            if (jsonObject.has(DATA)) {
                Log.d(TAG, "Data: " + jsonObject.toString());
                JSONArray data = jsonObject.getJSONArray(DATA);
                modulesStored.addAll(storeConfigs(data, keys, context));
            } else {
                Log.d(TAG, "Response doesn't contain '" + DATA + "'");
            }
        } else {
            Log.d(TAG, "Response body is null for " + keys.toString());
        }
        return modulesStored;
    }

    private static List<String> storeConfigs(JSONArray data, List<String> keys, Context context) throws JSONException {
        List<String> modulesStored = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            for (String key : keys) {
                if (data.getJSONObject(i).has(key)) {
                    String value = data.getJSONObject(i).get(key).toString();
                    writeToSharedPref(key, value, context);
                    modulesStored.add(key);
                    Log.d(TAG, "Stored key " + key + " with value: " + value);
                    break;
                }
            }
        }
        return modulesStored;
    }

    /**
     * Saves a string value in {@link SharedPreferences}
     *
     * @param prefsKey   key to map the value
     * @param prefsValue value to be stored
     * @param context    {@link Context} object
     */
    public static void writeToSharedPref(String prefsKey, String prefsValue, Context context) {
        getSharedPref(context).edit().putString(prefsKey, prefsValue).apply();
    }

    /**
     * Gets string value stored in {@link SharedPreferences}
     *
     * @param prefsKey     key to look up
     * @param defaultValue default value to be returned if key is not found
     * @param context      {@link Context} object
     * @return string value if found, otherwise {@code defaultValue}
     */
    public static String readFromSharedPref(String prefsKey, String defaultValue, Context context) {
        return getSharedPref(context).getString(prefsKey, defaultValue);
    }

    public static boolean doesKeyExist(Context context, String key) {
        return getSharedPref(context).contains(key);
    }

    /**
     * Constructs entities and their respective keys based on modules
     *
     * @param modules {@link Set<String>} modules
     * @return {@link Map<String, List>} with {@code KEYS} and {@code ENTITY_LIST} values
     */
    public static Map<String, List> getConfigMap(Set<String> modules) {
        List<String> entityList = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        Map<String, List> configMap = new HashMap<>();
        for (String module : modules) {
            switch (module) {
                case THEME:
                    entityList.add(THEME);
                    keys.add(THEME_KEY);
                    break;
                case MENU:
                    entityList.add(MENU);
                    keys.add(MENU_KEY);
                    break;
                case BANNER:
                    entityList.add(BANNER);
                    keys.add(BANNER_KEY);
                    break;
                case MESSAGE:
                    entityList.add(MESSAGE);
                    keys.add(MESSAGE_KEY);
                    break;
                default:
                    EventBusUtil.notifyApplication(new Notification(Notification.Event.UNKNOWN_MODULE));
                    Log.w(TAG, "Unknown module " + module);
                    break;
            }
        }
        configMap.put(KEYS, keys);
        configMap.put(ENTITY_LIST, entityList);
        return configMap;
    }
}