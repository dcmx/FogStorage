package com.fonkwill.fogstorage.client.client;

import com.fonkwill.fogstorage.client.domain.Placement;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface FogStorageService {

    @Multipart
    @POST("/services/middleware/api/files/upload")
    Call<Placement> upload(@Part MultipartBody.Part uploadFile,
                           @Part("useFogAsStorage") boolean useFogAsStorage,
                           @Part("dataChunksCount") int dataChunksCount,
                           @Part("parityChunksCount") int parityChunksCount);


    @POST("/services/middleware/api/files/download")
    Call<ResponseBody> download(@Body Placement placement);

}
