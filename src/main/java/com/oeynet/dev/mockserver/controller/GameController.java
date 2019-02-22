package com.oeynet.dev.mockserver.controller;

import com.oeynet.dev.mockserver.api.Config;
import com.oeynet.dev.mockserver.domain.models.ConfigGame;
import com.oeynet.dev.mockserver.domain.models.ConfigRoot;
import com.oeynet.dev.mockserver.domain.models.Message;
import com.oeynet.dev.mockserver.serial.SerialProtocolType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@RequestMapping("/api/game")
@RestController
public class GameController {


    private Config config = Config.getInstance();


    @RequestMapping(method = RequestMethod.GET, value = "/message")
    public Map<String, Object> getMessage() {
        Map<String, Object> map = new HashMap<>();
        ArrayList<Message> list = new ArrayList<>();
        Queue<Message> queue = Config.getInstance().getMessageQueue();
        int count = 4;//每次取6条就行了
        while (!queue.isEmpty() && count > 0) {
            count--;
            list.add(queue.poll());
        }
        map.put("code", 200);
        map.put("message", "获取成功");
        map.put("data", list);
        return map;
    }

    @RequestMapping("/config")
    public Map<String, Object> gameRoot() {
        ConfigRoot root = null;
        try {
            root = Config.getInstance().getConfig();
        } catch (Exception e) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("data", root);
        map.put("code", "1001");
        map.put("message", "获取成功");

        return map;
    }

    /**
     * 这个是游戏得配置
     *
     * @return 游戏得具体数据
     */
    @RequestMapping("/{id}")
    public Map<String, Object> getGame(@PathVariable("id") Integer gameId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();

        try {
            map.put("code", 1);
            map.put("message", "获取成功");
            ConfigRoot root = Config.getInstance().getConfig();
            ConfigGame gm = null;
            for (ConfigGame game : root.getGames()) {
                if (game.getId() == gameId) {
                    gm = game;
                    break;
                }
            }
            map.put("data", gm);
        } catch (Exception e) {
            map.put("code", 1004);
            response.setStatus(400);
            map.put("message", "对不起,游戏没有找到");
        }
        return map;
    }

    /**
     * 获取游戏状态
     *
     * @param gameId
     * @return
     */
    @RequestMapping(value = "/{id}/status")
    public Map<String, Object> getGameStatus(@PathVariable("id") Integer gameId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", gameId);
        ConfigGame game = config.getGame(gameId);
        if (null == game) {
            response.setStatus(400);
            map.put("message", "对不起,未找到相关游戏信息，获取游戏状态失败");
            return map;
        }
        config.getSerialPort().sendSearchCmd(SerialProtocolType.ALL_ADDR);
        return map;
    }

    /**
     * 一键重置 游戏ID
     *
     * @param gameId 游戏ID
     * @return
     */
    @RequestMapping(value = "/{id}/reset")
    public Map<String, Object> reset(@PathVariable("id") Integer gameId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", "Success");
        map.put("code", 1001);
        Config config = Config.getInstance();

        ConfigGame game = config.getGame(gameId);
        if (null == game) {
            response.setStatus(400);
            map.put("message", "对不起,未找到相关游戏");
            return map;
        }
        //获取重置指令
        byte reset = (byte) game.getReset();
        Config.getInstance().getSerialPort().sendSetCmd(reset, SerialProtocolType.ALL_ADDR);
        return map;
    }

    /**
     * 一键准备
     *
     * @param gameId 游戏ID
     * @return
     */
    @RequestMapping(value = "/{id}/ready")
    public Map<String, Object> ready(@PathVariable("id") Integer gameId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", "Success");
        map.put("code", 1001);
        ConfigGame game = config.getGame(gameId);
        if (null == game) {
            response.setStatus(400);
            map.put("message", "对不起,未找到相关游戏信息，游戏准备失败");
            return map;
        }
        byte cmd = (byte) game.getReady();
        Config.getInstance().getSerialPort().sendSetCmd(cmd, SerialProtocolType.ALL_ADDR);
        return map;
    }

    /**
     * 测试线路
     *
     * @param gameId 游戏ID
     * @return
     */
    @RequestMapping(value = "/{id}/test")
    public Map<String, Object> test(@PathVariable("id") Integer gameId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", "success");
        map.put("code", 1001);
        ConfigGame game = config.getGame(gameId);
        if (null == game) {
            response.setStatus(400);
            map.put("message", "对不起,未找到相关游戏信息，游戏测试失败");
            return map;
        }
        byte cmd = (byte) game.getStart();
        Config.getInstance().getSerialPort().sendSetCmd(cmd, SerialProtocolType.ALL_ADDR);
        return map;
    }

    /**
     * 开始游戏
     *
     * @param gameId 游戏ID
     * @return
     */
    @RequestMapping(value = "/{id}/start")
    public Map<String, Object> start(@PathVariable("id") Integer gameId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", "操作成功");
        map.put("code", 1001);
        ConfigGame game = config.getGame(gameId);
        if (null == game) {
            response.setStatus(400);
            map.put("message", "对不起,未找到相关游戏信息，游戏开始失败");
            return map;
        }
        byte cmd = (byte) game.getStart();
        Config.getInstance().getSerialPort().sendSetCmd(cmd, SerialProtocolType.ALL_ADDR);
        return map;
    }

    /**
     * 设置房间状态
     *
     * @param gameId 游戏ID
     * @return
     */
    @RequestMapping(value = "/{id}/set")
    public Map<String, Object> setLevel(@PathVariable("id") Integer gameId, @RequestBody Map<String, Object> reqMap) {

        int roomId = (int) reqMap.get("room");
        int level = (int) reqMap.get("level");

        Map<String, Object> map = new HashMap<>();
        map.put("message", "success");
        map.put("code", 1001);
        map.put("data", roomId + level);
        Config.getInstance().getSerialPort().sendSetCmd((byte) level, (byte) roomId);
        return map;
    }
}
//3E 7B 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 13 00 7D 0D 0A
