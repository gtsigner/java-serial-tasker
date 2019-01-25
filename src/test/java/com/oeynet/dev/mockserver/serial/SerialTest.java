package com.oeynet.dev.mockserver.serial;

import com.alibaba.fastjson.JSON;
import com.oeynet.dev.mockserver.api.Config;
import com.oeynet.dev.mockserver.domain.models.ConfigGame;
import com.oeynet.dev.mockserver.domain.models.ConfigRoot;
import com.oeynet.dev.mockserver.utils.FileRead;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SerialTest {

    @Test
    public void send() throws Exception {


    }

    @Test
    public void config() throws Exception {
        ConfigRoot root = FileRead.getConfigRoot();
        System.out.println("游戏数量：" + root.getGames().size());
    }
}
