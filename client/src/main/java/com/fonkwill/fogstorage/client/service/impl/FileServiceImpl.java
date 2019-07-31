package com.fonkwill.fogstorage.client.service.impl;

import com.fonkwill.fogstorage.client.domain.Placement;
import com.fonkwill.fogstorage.client.domain.UploadMode;
import com.fonkwill.fogstorage.client.service.FileService;
import org.springframework.core.io.Resource;

public class FileServiceImpl implements FileService {
    @Override
    public Placement upload(Resource resource, UploadMode uploadMode) {
        return null;
    }

    @Override
    public Resource download(Placement placement) {
        return null;
    }
}
