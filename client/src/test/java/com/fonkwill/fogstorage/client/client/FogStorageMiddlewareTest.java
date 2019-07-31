package com.fonkwill.fogstorage.client.client;

import com.fonkwill.fogstorage.client.ClientApplication;
import com.fonkwill.fogstorage.client.domain.Placement;
import com.fonkwill.fogstorage.client.domain.PlacementStrategy;
import com.fonkwill.fogstorage.client.domain.UploadMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClientApplication.class)
public class FogStorageMiddlewareTest {

    @Autowired
    ResourceLoader resourceLoader;

    @Test
    public void testUpload_ShouldUpload(){
        UploadMode uploadMode = new UploadMode();
        PlacementStrategy placementStrategy = new PlacementStrategy();
        placementStrategy.setDataChunksCount(2);
        placementStrategy.setParityChunksCount(1);
        uploadMode.setPlacementStrategy(placementStrategy);

        Resource resource = resourceLoader.getResource("README.md");
        MultipartFile uploadFile = null;
        try {
            uploadFile = new MockMultipartFile("uploadFile", "uploadFile", "multipart/form-data", IOUtils.toByteArray(resource.getInputStream()));
        } catch (IOException e) {
            fail();
        }


        MultipartBody.Part body = null;
        try {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), resource.getFile());
            body = MultipartBody.Part.createFormData("uploadFile", "uploadFile", requestFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        String json = new Gson().toJson(uploadMode);
        RequestBody uploadJson = null;
        {
             uploadJson = RequestBody.create(MediaType.parse("application/json"), json);
        }




        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8081").addConverterFactory(GsonConverterFactory.create())
                .build();

        assertNotNull(body);
        assertNotNull(uploadJson);

        FogStorageMiddleware fogStorageMiddlware = retrofit.create(FogStorageMiddleware.class);

  //      MultipartBody.Part filePart = MultipartBody.Part.createFormData("file",  ,);

//        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), , file));

        Call<Placement> call = fogStorageMiddlware.upload(body, false, 2, 1);

        Placement placement = null;
        try {
             Response<Placement> response = call.execute();
             placement = response.body();
             assertEquals(response.code(), 200);
        } catch (IOException e) {
            fail();
        }
        assertNotNull(placement);

        Placement requestPlacement = new Placement();
        requestPlacement.setPlacementStrategy(placementStrategy);

        Call<ResponseBody> call2 = fogStorageMiddlware.download(placement);
        ResponseBody resource1 = null;
        try {
            Response<ResponseBody> response = call2.execute();
            assertEquals(response.code(), 200);

            resource1 = response.body();
        } catch (IOException e) {
            fail();
        }
        File target = new File("Readme2");

        try {
            FileUtils.copyInputStreamToFile(resource1.byteStream(), target);
        } catch (IOException e) {
            fail();
        }


    }

}
