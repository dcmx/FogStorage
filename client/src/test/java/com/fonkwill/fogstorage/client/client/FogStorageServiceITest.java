package com.fonkwill.fogstorage.client.client;

import com.fonkwill.fogstorage.client.domain.*;
import com.fonkwill.fogstorage.client.domain.enumeration.FogStorageNodeType;
import okhttp3.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class FogStorageServiceITest {

    @Autowired
    ResourceLoader resourceLoader;

    //@Value("${application-test.fog-url}")
    private String fog_url = "";

    private static final Logger logger = LoggerFactory.getLogger(FogStorageServiceITest.class);

    @Test
    public void testDownload_ShouldDownload() {
        if (fog_url == null || fog_url.isEmpty()) {
            logger.warn("No url for fog-url set, test will be ignored ");
            return;
        }

        Node awsNode = new Node().name("AWS_Oregon").type(FogStorageNodeType.AWS);
        Node gcpNode = new Node().name("GCP_Iowa").type(FogStorageNodeType.GCP);

        String originalChecksum = "10A103A451A31D3871153A630392FCD5";
        int fileSize = 10275;
        int chunkSize = 5140;

        Chunk chunk1 = new Chunk();
        chunk1.setUuid("part0");
        chunk1.setPosition(0);
        chunk1.setSize(new Long(chunkSize));

        Chunk chunk2 = new Chunk();
        chunk2.setUuid("part1");
        chunk2.setPosition(1);
        chunk2.setSize(new Long(chunkSize));

        FileInfo originalFile = new FileInfo();
        originalFile.setSize(new Long(fileSize));
        originalFile.setChecksum(originalChecksum);

        List<Placement.Assignment> assignment = new ArrayList<>();
        Placement.Assignment assignment1 = new Placement.Assignment();
        assignment1.setChunk(chunk1);
        assignment1.setNode(awsNode);
        Placement.Assignment assignment2 = new Placement.Assignment();
        assignment2.setChunk(chunk2);
        assignment2.setNode(gcpNode);
        assignment.add(assignment1);
        assignment.add(assignment2);

        Placement placement = new Placement();

        PlacementStrategy strategy = new PlacementStrategy();
        strategy.setParityChunksCount(1);
        strategy.setDataChunksCount(2);
        strategy.setUseFogAsStorage(false);

     //   placement.setOriginalFileInfo(originalFile);
        placement.setStoredAtList(assignment);
        placement.setPlacementStrategy(strategy);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(fog_url).addConverterFactory(GsonConverterFactory.create())
                .build();

        FogStorageService fogStorageMiddlware = retrofit.create(FogStorageService.class);


        Call<ResponseBody> call2 = fogStorageMiddlware.download(placement);
        ResponseBody resource1 = null;
        Headers headers = null;
        try {
            Response<ResponseBody> response = call2.execute();
            headers = response.headers();
            assertEquals(response.code(), 200);

            resource1 = response.body();

        } catch (IOException e) {
            fail();
        }

        try {
            String hex = DigestUtils.md5DigestAsHex(resource1.byteStream());
            assertThat(hex).isEqualToIgnoringCase(originalChecksum);
        } catch (IOException e) {
            fail();
        }

        Measurement measurement = new Measurement(headers);

        assertThat(headers).isNotNull();
        assertThat(measurement.getCodingTime()).isNotNull().isNotEqualToIgnoringCase("-1");

    }

    @Test
    public void testUpload_ShouldUpload(){
        if (fog_url == null || fog_url.isEmpty()) {
            logger.warn("No url for fog-url set, test will be ignored ");
            return;
        }
        UploadMode uploadMode = new UploadMode();
        PlacementStrategy placementStrategy = new PlacementStrategy();
        placementStrategy.setDataChunksCount(2);
        placementStrategy.setParityChunksCount(1);
        uploadMode.setPlacementStrategy(placementStrategy);

        Resource resource = resourceLoader.getResource("classpath:testfiles/test.md");

        MultipartBody.Part body = null;
        try {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), resource.getFile());
            body = MultipartBody.Part.createFormData("uploadFile", "uploadFile", requestFile);

        } catch (IOException e) {
            fail();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(fog_url).addConverterFactory(GsonConverterFactory.create())
                .build();

        FogStorageService fogStorageMiddlware = retrofit.create(FogStorageService.class);


        Call<Placement> call = fogStorageMiddlware.upload(body, placementStrategy.isUseFogAsStorage(), placementStrategy.getDataChunksCount(), placementStrategy.getParityChunksCount());

        Placement placement = null;
        Headers headers = null;
        try {
             Response<Placement> response = call.execute();
             placement = response.body();
             headers = response.headers();
             assertEquals(response.code(), 200);
        } catch (IOException e) {
            fail();
        }
        assertNotNull(placement);


        Measurement measurement = new Measurement(headers);

        assertThat(headers).isNotNull();
        assertThat(measurement.getCodingTime()).isNotNull().isNotEqualToIgnoringCase("-1");


    }

}
