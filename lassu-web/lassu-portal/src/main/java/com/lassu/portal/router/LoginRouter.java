package com.lassu.portal.router;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lassu.common.model.User;
import com.lassu.common.model.UserStatus;
import com.lassu.common.util.CommonConstant;
import com.lassu.service.user.UserService;

@Controller
public class LoginRouter {

	@Autowired
	private UserService userService;
	
	@GetMapping(CommonConstant.URL_LOGIN)
	public String login(@RequestParam(value = "error", required = false) String error, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return "redirect:" + CommonConstant.URL_DEFAULT_SUCCESS;
		}
		if (error != null) {
			if (error.equalsIgnoreCase(CommonConstant.AUTH_CODE_LOGIN_DEACTIVE)) {
				model.addAttribute("msg", "Your account is deactivated");
			} else if (error.equalsIgnoreCase(CommonConstant.AUTH_CODE_LOGIN_DISABLED)) {
				model.addAttribute("msg", "Your account is disabled");
			} else if (error.equalsIgnoreCase(CommonConstant.AUTH_CODE_LOGIN_INVALID)) {
				model.addAttribute("msg", "Invalid username and/or password");
			}
		}
		return "login";
	}

	@GetMapping(CommonConstant.URL_DEFAULT_SUCCESS)
	public String loginSuccess(Model model, Principal principal) {
		User user = userService.findUserByUsername(principal.getName());
		if (user.getUserStatus() == UserStatus.INSECURE) {
			model.addAttribute("securitymsg","The login and password you have used appears to have been used on another website. This is not recommended. Please choose a unique password for this website");
		}
		return "dashboard";
	}
	
}
