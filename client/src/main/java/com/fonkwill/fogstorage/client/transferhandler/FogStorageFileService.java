package com.fonkwill.fogstorage.client.transferhandler;

import com.fonkwill.fogstorage.client.transferhandler.vm.PlacementVM;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface FogStorageFileService {

    @Multipart
    @POST("/services/middleware/api/blocks/upload")
    Call<PlacementVM> upload(@Part MultipartBody.Part uploadFile,
                             @Part("useFogAsStorage") boolean useFogAsStorage,
                             @Part("dataChunksCount") int dataChunksCount,
                             @Part("parityChunksCount") int parityChunksCount);


    @POST("/services/middleware/api/blocks/download")
    Call<ResponseBody> download(@Body PlacementVM placement);



}
