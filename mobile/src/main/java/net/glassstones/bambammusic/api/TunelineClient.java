package net.glassstones.bambammusic.api;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

public interface TunelineClient {
    @POST("/1/functions/tuneline")
    Call<List<Tune>> getTunes(@Body User user);
}
