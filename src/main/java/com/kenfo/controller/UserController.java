package com.kenfo.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.drools.util.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/user")
public class UserController {

	@RequestMapping(value="/login",method=RequestMethod.GET)
	public String deploy(Map<String,Object> model){
		
		return "login";
	}
	
	@RequestMapping(value="/doLogin",method=RequestMethod.POST)
	public String doLogin(String userName,HttpSession session,Model model){
		if(StringUtils.isEmpty(userName)){
			model.addAttribute("message","用户名不能为空!");
			return "login";
		}else{
			session.setAttribute("userName", userName);
		}
		return "redirect:/leave/index";
	}
	
	@RequestMapping(value="/logout",method=RequestMethod.GET)
	public String doLogin(HttpSession session,Model model){
		session.removeAttribute("userName");
		return "login";
	}
}
