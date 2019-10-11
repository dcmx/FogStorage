package com.fonkwill.fogstorage.client.transferhandler.authenticaton;

import com.fonkwill.fogstorage.client.transferhandler.vm.LoginVM;
import com.fonkwill.fogstorage.client.transferhandler.vm.TokenVM;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FogStorageAuthenticationService {

    @POST("/services/middleware/api/authenticate-user")
    Call<TokenVM> authenticate(@Body LoginVM login);

}
