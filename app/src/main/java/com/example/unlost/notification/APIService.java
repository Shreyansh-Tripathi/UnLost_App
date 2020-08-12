package com.example.unlost.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAfj6Xk98:APA91bHLb3rNCWxCiod1QwEcewiw6ZR1ptXLJDz0stBHZ2B_jAtnSQvriIECHGq_3YPzuiZA3dpvxRF7uD0afuElC2B5Yi9pd62Hi5LD_jIyp6Eg8MURt3bZylxa2FiM0RHUu6qNGVq9"

            }

    )
    @POST("fcm/send")
    Call<Response> sendNotification(@Body NotificationSender body);
}
