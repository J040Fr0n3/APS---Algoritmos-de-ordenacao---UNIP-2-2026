package com.Unip.APS_CC_2026.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ThymeleafController {
	
	@GetMapping("/")
	public String iniciar(Model model) {
	return "index";
	}
}
