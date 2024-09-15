package com.HIT.StoreManagementApp.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoPageController {

    @GetMapping("/info-page")
    public String infoPage(Model model) {
        // Add any necessary data to the model
        return "infoPage";  // This should correspond to an infoPage.html or similar in your templates folder
    }
}