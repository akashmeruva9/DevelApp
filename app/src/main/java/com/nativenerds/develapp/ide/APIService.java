package com.nativenerds.develapp.ide;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {

    @POST("execute")
    Call<String> execute(@Body PostData postData);
}
