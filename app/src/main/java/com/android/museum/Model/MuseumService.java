package com.android.museum.Model;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MuseumService {
    @GET("{id}")
    Call<Museum> getMuseum(@Path("id") String id);

    @Multipart
    @POST("{id}/pictures")
    Call<ResponseBody> uploadPhotoMuseum(
            @Part MultipartBody.Part file,
            @Part("description") RequestBody name,
            @Path("id") String id
            );
}
