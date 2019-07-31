package com.fonkwill.fogstorage.client.service;

import com.fonkwill.fogstorage.client.domain.Placement;
import com.fonkwill.fogstorage.client.domain.UploadMode;
import org.springframework.core.io.Resource;

public interface FileService {


    Placement upload(Resource resource, UploadMode uploadMode);

    Resource download(Placement placement);

}
