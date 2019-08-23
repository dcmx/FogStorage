package com.fonkwill.fogstorage.client.client;

import com.fonkwill.fogstorage.client.domain.LoginVM;
import com.fonkwill.fogstorage.client.domain.Placement;
import com.fonkwill.fogstorage.client.domain.TokenVM;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FogStorageAuthenticationService {

    @POST("/services/middleware/api/authenticate")
    Call<TokenVM> authenticate(@Body LoginVM login);

}
