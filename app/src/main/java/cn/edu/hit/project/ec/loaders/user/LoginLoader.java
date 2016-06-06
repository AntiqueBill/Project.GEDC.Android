package cn.edu.hit.project.ec.loaders.user;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;

import cn.edu.hit.project.ec.models.user.User;
import cn.edu.hit.project.ec.network.ServiceFactory;
import cn.edu.hit.project.ec.network.UserService;
import retrofit2.Call;

public class LoginLoader extends AsyncTaskLoader<User> {
    private String name;
    private String password;

    public LoginLoader(Context context, String name, String password) {
        super(context);
        this.name = name;
        this.password = password;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public User loadInBackground() {
        UserService service = ServiceFactory.getService(UserService.class);
        Call<User> call = service.login(name, password);
        User user = null;
        try {
            user = call.execute().body();
            if (user == null || user.apiToken == null || user.apiToken.equals("")) {
                user = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }
}
