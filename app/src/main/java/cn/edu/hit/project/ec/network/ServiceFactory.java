package cn.edu.hit.project.ec.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cn.edu.hit.project.ec.App;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceFactory {
    private final static String BASE_URL = String.format("http://%s", App.API_SERVER);
    private final static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private final static Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();

    public static <T> T getService(Class<T> service) {
        return retrofit.create(service);
    }
}
