package com.weishao.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.subject.Subject;
import com.weishao.bean.ResponseBean;
import com.weishao.database.UserService;
import com.weishao.database.UserBean;
import com.weishao.exception.UnauthorizedException;
import com.weishao.util.JWTUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebController {

	private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    private UserService userService;

    @Autowired
    public void setService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseBean login(@RequestBody Map<String,String> params) {
		String username=params.get("username");
		String password=params.get("password");
        UserBean userBean = userService.getUser(username);
        if(Objects.isNull(userBean)) {
        	logger.info("user not exists,username={}",username);
        	throw new UnauthorizedException(String.format("Invalid username for user [%s]", username));
        }
        
        if (userBean.getPassword().equals(password)) {
        	Map<String,Object> data=new HashMap<String,Object>();
        	String access_token=JWTUtil.sign(username, password);
        	data.put("access_token", access_token);
        	data.put("token_type","bearer");
        	data.put("expires_in",JWTUtil.EXPIRE_TIME/1000);
        	logger.info("login success,username={},token={}",username,access_token);
            return new ResponseBean(200, "success",data);
        } else {
            throw new UnauthorizedException(String.format("Invalid password for user [%s]", username));
        }
    }

	@GetMapping("/article")
	public ResponseBean article() {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			return new ResponseBean(200, "success", "You are already logged in");
		} else {
			return new ResponseBean(200, "success", "You are guest");
		}
	}

    @GetMapping("/require_auth")
    @RequiresAuthentication
    public ResponseBean requireAuth() {
        return new ResponseBean(200, "success","You are authenticated");
    }

    @GetMapping("/require_role")
    @RequiresRoles("admin")
    public ResponseBean requireRole() {
        return new ResponseBean(200, "success","You are visiting require_role");
    }

    @GetMapping("/require_permission")
    @RequiresPermissions(logical = Logical.AND, value = {"view", "edit"})
    public ResponseBean requirePermission() {
        return new ResponseBean(200, "success","You are visiting permission require edit,view");
    }

}
