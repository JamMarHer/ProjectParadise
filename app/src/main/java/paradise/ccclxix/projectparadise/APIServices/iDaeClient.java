package paradise.ccclxix.projectparadise.APIServices;

import java.util.List;

import paradise.ccclxix.projectparadise.APIForms.Event;
import paradise.ccclxix.projectparadise.APIForms.EventResponse;
import paradise.ccclxix.projectparadise.APIForms.User;
import paradise.ccclxix.projectparadise.APIForms.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface iDaeClient {

    @GET("/iDae/api/user/{user}")
    Call<UserResponse> getUserPresense(@Path("user") String user);

    @POST("/iDae/public/api/user/add")
    Call<UserResponse> addUser(@Body User user);

    @POST("/iDae/public/api/user/login")
    Call<UserResponse> loginUser(@Body User user);

    @POST("/iDae/public/api/user/check_token")
    Call<UserResponse> check_token(@Body User user);

    @POST("/iDae/public/api/events/add")
    Call<EventResponse> post_event(@Body Event event);

}
