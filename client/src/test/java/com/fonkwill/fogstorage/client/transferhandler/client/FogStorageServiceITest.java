package com.fonkwill.fogstorage.client.transferhandler.client;

import com.fonkwill.fogstorage.client.transferhandler.FogStorageFileService;
import com.fonkwill.fogstorage.client.transferhandler.authenticaton.FogStorageAuthenticator;
import com.fonkwill.fogstorage.client.transferhandler.vm.PlacementVM;
import com.fonkwill.fogstorage.client.shared.domain.*;
import com.fonkwill.fogstorage.client.shared.domain.enumeration.FogStorageNodeType;
import com.fonkwill.fogstorage.client.cryptoservice.exception.EncryptionException;
import com.fonkwill.fogstorage.client.cryptoservice.impl.AbstractAesService;
import com.fonkwill.fogstorage.client.cryptoservice.utils.EncryptionUtils;
import com.fonkwill.fogstorage.client.cryptoservice.CryptoService;
import com.fonkwill.fogstorage.client.controller.FogStorageContext;
import okhttp3.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class FogStorageServiceITest {

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    CryptoService enDeCryptionService;

    @Value("${application-test.fog-url}")
    private String fog_url = "";

    @Value("${application-test.fog-pk}")
    private String fog_pk = "";

    @Value("${application-test.fog-user}")
    private String fog_user = "";

    @Value("${application-test.fog-pw}")
    private String fog_pw = "";


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


        String key = null;
        try {
            key = EncryptionUtils.getKey(AbstractAesService.keyLengthBit, AbstractAesService.algorithm);
        } catch (EncryptionException e) {
            fail();
        }

        SharedSecret sharedSecret = new SharedSecret();
        sharedSecret.setValue(key);

        FogStorageContext fogStorageContext = new FogStorageContext();
        fogStorageContext.setUsername(fog_user);
        fogStorageContext.setPassword(fog_pw);

        FogStorageAuthenticator fogStorageAuthenticator = new FogStorageAuthenticator(fog_url, sharedSecret, fog_pk, fogStorageContext, enDeCryptionService);

        FogStorageFileService fogStorageMiddlware = getFogStorageFileService(fogStorageAuthenticator);


        PlacementVM placementVM = new PlacementVM();
        try {
            placementVM.setEncodedPlacement(enDeCryptionService.encryptPlacement(placement, key ));
        } catch (EncryptionException e) {
            fail();
        }

        Call<ResponseBody> call2 = fogStorageMiddlware.download(placementVM);
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

    private FogStorageFileService getFogStorageFileService(FogStorageAuthenticator fogStorageAuthenticator) {
        //noinspection Duplicates
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
                authenticator(fogStorageAuthenticator).
                addInterceptor(fogStorageAuthenticator).
                connectTimeout(50, TimeUnit.SECONDS).
                readTimeout(50, TimeUnit.SECONDS).
                writeTimeout(50, TimeUnit.SECONDS).
                build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(fog_url).addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(FogStorageFileService.class);
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
        String key = null;
        try {
            key = EncryptionUtils.getKey(AbstractAesService.keyLengthBit, AbstractAesService.algorithm);
        } catch (EncryptionException e) {
            fail();
        }
        SharedSecret sharedSecret = new SharedSecret();
        sharedSecret.setValue(key);

        FogStorageContext fogStorageContext = new FogStorageContext();
        fogStorageContext.setUsername(fog_user);
        fogStorageContext.setPassword(fog_pw);

        FogStorageAuthenticator fogStorageAuthenticator = new FogStorageAuthenticator(fog_url, sharedSecret, fog_pk, fogStorageContext, enDeCryptionService);

        FogStorageFileService fogStorageMiddlware = getFogStorageFileService(fogStorageAuthenticator);


        Call<PlacementVM> call = fogStorageMiddlware.upload(body, placementStrategy.isUseFogAsStorage(), placementStrategy.getDataChunksCount(), placementStrategy.getParityChunksCount());

        PlacementVM placementVM = null;
        Headers headers = null;
        try {
             Response<PlacementVM> response = call.execute();
             placementVM = response.body();
             headers = response.headers();
             assertEquals(response.code(), 200);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        assertNotNull(placementVM);

        try {
            Placement placement = enDeCryptionService.decryptPlacement(placementVM.getEncodedPlacement(), sharedSecret.getValue());
            assertThat(placement).isNotNull();
        } catch (EncryptionException e) {
            fail();
        }



        Measurement measurement = new Measurement(headers);

        assertThat(headers).isNotNull();
        assertThat(measurement.getCodingTime()).isNotNull().isNotEqualToIgnoringCase("-1");


    }

}
