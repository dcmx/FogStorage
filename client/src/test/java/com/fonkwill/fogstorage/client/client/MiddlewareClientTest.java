package com.fonkwill.fogstorage.client.client;

import com.fonkwill.fogstorage.client.ClientApplication;
import com.fonkwill.fogstorage.client.domain.Placement;
import com.fonkwill.fogstorage.client.domain.PlacementStrategy;
import com.fonkwill.fogstorage.client.domain.UploadMode;
import feign.Feign;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClientApplication.class)
public class MiddlewareClientTest {

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    MiddlewareClient client;

    @Test
    public void testUploadClient_ShouldUpload(){

        //MiddlewareClient client = Feign.builder().decoder(new GsonDecoder()).encoder(new GsonEncoder()).target(MiddlewareClient.class, "http://localhost:8081");
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

        File f = new File("README.md");

        ResponseEntity<Placement> placement = null;
        try {
            placement = client.upload(uploadMode, uploadFile);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        System.out.println(placement.getStatusCode());
    }


}
