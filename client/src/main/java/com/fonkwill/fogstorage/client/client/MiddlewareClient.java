package com.fonkwill.fogstorage.client.client;

import com.fonkwill.fogstorage.client.configuration.FeignConfiguration;
import com.fonkwill.fogstorage.client.domain.Placement;
import com.fonkwill.fogstorage.client.domain.UploadMode;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URISyntaxException;

@FeignClient(url = "http://localhost:8081", name = "middleware", configuration = FeignConfiguration.class)
public interface MiddlewareClient {

    //@PostMapping(value = "services/middleware/api/files")
    @RequestMapping( method = RequestMethod.POST, value = "/services/middleware/api/files")
    public ResponseEntity<Placement> upload(UploadMode uploadMode, MultipartFile uploadfile) throws URISyntaxException;


}
