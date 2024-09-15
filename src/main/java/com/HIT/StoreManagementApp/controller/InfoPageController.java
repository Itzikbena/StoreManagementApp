package com.HIT.StoreManagementApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;  // <-- Correct import for Spring's Model
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InfoPageController {

    @GetMapping("/infopage")
    public String infopage(@RequestParam("branchId") Long branchId, @RequestParam("userId") Long userId, Model model) {
        // Add branchId and userId to the model so it can be used in the template
        model.addAttribute("branchId", branchId);
        model.addAttribute("userId", userId);

        // Return the name of the template (infopage.html)
        return "infopage";
    }

}
