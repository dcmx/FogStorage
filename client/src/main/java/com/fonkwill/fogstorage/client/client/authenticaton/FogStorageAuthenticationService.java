package com.fonkwill.fogstorage.client.client.authenticaton;

import com.fonkwill.fogstorage.client.client.vm.LoginVM;
import com.fonkwill.fogstorage.client.client.vm.TokenVM;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FogStorageAuthenticationService {

    @POST("/services/middleware/api/authenticate")
    Call<TokenVM> authenticate(@Body LoginVM login);

}
