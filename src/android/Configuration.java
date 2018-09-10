package cordova.plugin.dbxp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Configuration {

    @JsonProperty("base_url")
    private String baseUrl;

    @JsonProperty("fcm_topic")
    private String topic;

    @JsonProperty("api_key")
    private String apiKey;

    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("analytics_key")
    private String analyticsKey;

    @JsonProperty("sender_id")
    private String senderId;

    /**
     * @return {@link String} instance representing the base URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * @return {@link String} instance representing the topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * @return {@link String} instance representing the API key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @return {@link String} instance representing the app ID
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @return {@link String} instance representing the analytics key
     */
    public String getAnalyticsKey() {
        return analyticsKey;
    }

    /**
     * @return {@link String} instance representing the sender ID
     */
    public String getSenderId() {
        return senderId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Configuration{");
        sb.append("baseUrl='").append(baseUrl).append('\'');
        sb.append(", topic='").append(topic).append('\'');
        sb.append(", apiKey='").append(apiKey).append('\'');
        sb.append(", appId='").append(appId).append('\'');
        sb.append(", analyticsKey='").append(analyticsKey).append('\'');
        sb.append(", senderId='").append(senderId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}