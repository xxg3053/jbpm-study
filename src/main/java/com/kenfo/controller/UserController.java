package com.kenfo.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
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
	public String doLogin(String userName,String type,HttpSession session,Model model){
		if(StringUtils.isEmpty(userName)){
			model.addAttribute("message","用户名不能为空!");
			return "login";
		}
		if(StringUtils.isNotEmpty(type)){
			session.setAttribute("userName", userName);
			
			if(type.equals("leave")){
				return "redirect:/leave/index";
			}else if(type.equals("sign")){
				return "redirect:/sign/index";
			}else if(type.equals("mail")){
				return "redirect:/mail/index";
			}else{
				return "index";
			}
		}else{
			return "index";
		}
		
	}
	
	@RequestMapping(value="/logout",method=RequestMethod.GET)
	public String doLogin(HttpSession session,Model model){
		session.removeAttribute("userName");
		return "login";
	}
}
