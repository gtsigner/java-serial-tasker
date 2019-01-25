package com.oeynet.dev.mockserver.controller;

import com.oeynet.dev.mockserver.domain.models.Game;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping({"/", "/index", "index.html"})
    public String index() {
        return "index";
    }


    @RequestMapping("/game/{gameName}")
    public String game(@PathVariable String gameName, Model model) {
        Game gm = new Game();
        gm.setTitle("游戏A");
        model.addAttribute("game", gm);
        return "game";
    }
}
