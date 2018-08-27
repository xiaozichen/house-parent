package com.mooc.house.web.controller;

import com.mooc.house.biz.service.UserService;
import com.mooc.house.common.constants.CommonConstants;
import com.mooc.house.common.model.User;
import com.mooc.house.common.result.ResultMsg;
import com.mooc.house.common.utils.HashUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UserController {

  @Autowired
  private UserService userService;
  


  /**
   * 注册提交:1.注册验证 2 发送邮件 3验证失败重定向到注册页面 注册页获取:根据account对象为依据判断是否注册页获取请求
   * 
   * @param account
   * @param modelMap
   * @return
   */
  @RequestMapping("accounts/register")
  public String accountsRegister(User account, ModelMap modelMap) {
    if (account == null || account.getName() == null) {
      //modelMap.put("agencyList",  agencyService.getAllAgency());
      return "/user/accounts/register";
    }
    // 用户验证
    ResultMsg resultMsg = UserHelper.validate(account);
    if (resultMsg.isSuccess() && userService.addAccount(account)) {
      modelMap.put("email", account.getEmail());
      return "/user/accounts/registerSubmit";
    } else {
      return "redirect:/accounts/register?" + resultMsg.asUrlParams();
    }
  }

  @RequestMapping("accounts/verify")
  public String verify(String key) {
    boolean result = userService.enable(key);
    if (result) {
      return "redirect:/index?" + ResultMsg.successMsg("激活成功").asUrlParams();
    } else {
      return "redirect:/accounts/register?" + ResultMsg.errorMsg("激活失败,请确认链接是否过期");
    }
  }

  // ----------------------------登录流程------------------------------------

  /**
   * 登录接口
   */
  @RequestMapping("/accounts/signin")
  public String signin(HttpServletRequest req) {
    String username = req.getParameter("username");
    String password = req.getParameter("password");
    String target = req.getParameter("target");
    if (username == null || password == null) {
      req.setAttribute("target", target);
      return "/user/accounts/signin";
    }
    User user = null;
    if (user == null) {
      return "redirect:/accounts/signin?" + "target=" + target + "&username=" + username + "&"
          + ResultMsg.errorMsg("用户名或密码错误").asUrlParams();
    } else {
      HttpSession session = req.getSession(true);
      session.setAttribute(CommonConstants.USER_ATTRIBUTE, user);
      // session.setAttribute(CommonConstants.PLAIN_USER_ATTRIBUTE, user);
      return StringUtils.isNoneBlank(target) ? "redirect:" + target : "redirect:/index";
    }
  }

  /**
   * 登出操作
   * 
   * @param request
   * @return
   */
  @RequestMapping("accounts/logout")
  public String logout(HttpServletRequest request) {
    HttpSession session = request.getSession(true);
    session.invalidate();
    return "redirect:/index";
  }

  // ---------------------个人信息页-------------------------
  /**
   * 1.能够提供页面信息 2.更新用户信息
   * 
   * @param updateUser
   * @param model
   * @return
   */





  
  

}
