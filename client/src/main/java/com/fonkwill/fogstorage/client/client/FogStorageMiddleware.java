package com.fonkwill.fogstorage.client.client;

import com.fonkwill.fogstorage.client.domain.Placement;
import com.fonkwill.fogstorage.client.domain.UploadMode;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.Call;
import retrofit2.http.*;

import java.net.URISyntaxException;

public interface FogStorageMiddleware {

    @Multipart
    @POST("/services/middleware/api/files/upload")
    Call<Placement> upload(@Part MultipartBody.Part uploadFile,
                           @Part("useFogAsStorage") boolean useFogAsStorage,
                           @Part("dataChunksCount") int dataChunksCount,
                           @Part("parityChunksCount") int parityChunksCount);


    @POST("/services/middleware/api/files/download")
    Call<ResponseBody> download(@Body Placement placement);

}
