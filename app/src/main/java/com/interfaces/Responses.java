package com.interfaces;

import com.getToken.Token;

import retrofit2.Call;

/**
 * Created by hp on 8/19/2016.
 */
public interface Responses {
    void onResponse(Call<Token> call, retrofit2.Response<Token> response);
    void onFailure(Call<Token> call, Throwable t);
}
