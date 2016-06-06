package cn.edu.hit.project.ec.network;

import cn.edu.hit.project.ec.models.user.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {
    @FormUrlEncoded
    @POST("auth/login")
    Call<User> login(@Field("name") String name, @Field("password") String password);

    @FormUrlEncoded
    @POST("auth/register")
    Call<User> register(@Field("name") String name, @Field("email") String email, @Field("password") String password);
}
