package com.oeynet.dev.mockserver.utils;

import com.alibaba.fastjson.JSON;
import com.oeynet.dev.mockserver.domain.models.ConfigRoot;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FileRead {

    public static ConfigRoot getConfigRoot() throws IOException {
        URL url = ResourceUtils.getURL("config/config.json");

        System.out.println("配置文件URL: " + url);
        InputStream stream = url.openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder str = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            str.append(line);
        }
        br.close();
        return JSON.parseObject(str.toString(), ConfigRoot.class);
    }
}
