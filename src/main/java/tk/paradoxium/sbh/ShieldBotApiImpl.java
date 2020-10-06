package tk.paradoxium.sbh;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShieldBotApiImpl implements ShieldBotApi{

    /*
    The builder for the API, contains all the settings.
    For example: authorization key and etc.
    Feel free to change it to your liking, this is only temporary.
     */

    private final HttpUrl url;
    private final OkHttpClient client;

    /**
     * Used to create the Shield Bot API class.
     * @param token The token you receive from ShieldBotList.tk (not from Discord).
     * @param botId Your bot client ID, can be found on developer portal on Discord (discordapp.com/developers).
     */
    public ShieldBotApiImpl(String token, String botId) {
        this.url = new HttpUrl.Builder()
                .scheme("http")
                .host("shieldbotlist.tk")
                .addPathSegment("api")
                .addPathSegment("auth")
                .addPathSegment("stats")
                .addPathSegment(botId)
                .build();
        this.client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder().addHeader("authorization", token).build();
                    return chain.proceed(request);
                }).build();
    }

    /**
     * Used to create the Shield Bot API class.
     * @param token The token you receive from ShieldBotList.tk (not from Discord).
     * @param botId Your bot client ID, can be found on developer portal on Discord (discordapp.com/developers).
     */
    public ShieldBotApiImpl(String token, Long botId) {
        this.url = new HttpUrl.Builder()
                .scheme("http")
                .host("shieldbotlist.tk")
                .addPathSegment("api")
                .addPathSegment("auth")
                .addPathSegment("stats")
                .addPathSegment(botId.toString())
                .build();
        this.client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder().addHeader("authorization", token).build();
                    return chain.proceed(request);
                }).build();
    }

    /**
     * Sets the server count value, immediately updates to Shield Bot List.
     * @param count The server count value.
     */
    public void setServerCount(int count){
        JSONObject object = new JSONObject().put("server_count", count);
        send(object);
    }

    /**
     * Sends to ShieldBotList the entire request.
     * @param body The body.
     */
    private void send(JSONObject body){
        MediaType JSON = MediaType.parse("application/json");
        RequestBody x = RequestBody.create(body.toString(), JSON);
        Request request = new Request.Builder()
                .post(x).url(url).build();

        Call caller = client.newCall(request);
        caller.enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Logger.getLogger(ShieldBotApiImpl.class.getName()).log(Level.SEVERE, "Exception occurred", e);
            }

            /*
             * Fail-safe that actually doesn't work, yeah...
             */
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    if(!response.isSuccessful() || response.message().isEmpty()){
                        try {
                            JSONObject body = new JSONObject(response.body().string());
                            Logger.getLogger(ShieldBotApiImpl.class.getName()).log(Level.SEVERE, "Exception occurred", body.getString("error"));
                        } catch (Exception e){
                            Logger.getLogger(ShieldBotApiImpl.class.getName()).log(Level.SEVERE, "Exception occurred");
                        }
                    }
                } catch (Exception e){
                    Logger.getLogger(ShieldBotApiImpl.class.getName()).log(Level.SEVERE, "Exception occurred", e);
                } finally {
                    response.body().close();
                }
            }
        });
    }

}