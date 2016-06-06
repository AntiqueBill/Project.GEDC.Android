package cn.edu.hit.project.ec.loaders.user;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;

import cn.edu.hit.project.ec.models.user.User;
import cn.edu.hit.project.ec.network.ServiceFactory;
import cn.edu.hit.project.ec.network.UserService;
import retrofit2.Call;

public class RegisterLoader extends AsyncTaskLoader<User> {
    private String name;
    private String email;
    private String password;

    public RegisterLoader(Context context, String name, String email, String password) {
        super(context);
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public User loadInBackground() {
        UserService service = ServiceFactory.getService(UserService.class);
        Call<User> call = service.register(name, email, password);
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
