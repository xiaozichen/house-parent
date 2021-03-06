package com.mooc.house.biz.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mooc.house.biz.mapper.UserMapper;
import com.mooc.house.common.model.User;
import com.mooc.house.common.utils.BeanHelper;
import com.mooc.house.common.utils.HashUtils;

@Service
public class UserService {


  @Autowired
  private FileService fileService;

  @Autowired
  private MailService mailService;

  @Autowired
  private UserMapper userMapper;

  @Value("${file.path}")
  private String imgPrefix;

  public List<User> getUsers() {
    return userMapper.selectUsers();
  }

  /**
   * 1.插入数据库，非激活;密码加盐md5;保存头像文件到本地 2.生成key，绑定email 3.发送邮件给用户
   * 
   * @param account
   * @return
   */
  @Transactional(rollbackFor = Exception.class)
  public boolean addAccount(User account) {
    account.setPasswd(HashUtils.encryPassword(account.getPasswd()));
    List<String> imgList = fileService.getImgPaths(Lists.newArrayList(account.getAvatarFile()));
    if (!imgList.isEmpty()) {
      account.setAvatar(imgList.get(0));
    }
    BeanHelper.setDefaultProp(account, User.class);
    BeanHelper.onInsert(account);
    account.setEnable(0);
    userMapper.insert(account);
    mailService.registerNotify(account.getEmail());
    return true;
  }


  public boolean enable(String key) {
     return  mailService.enable(key);
  }

    public User auth(String username, String password) {
    User user =new User();
    user.setEmail(username);
    user.setPasswd(HashUtils.encryPassword(password));
    user.setEnable(1);
    List<User> users =  this.getUserByQuery(user);
    if(!users.isEmpty()){
      return users.get(0);
    }
       return  null;
    }

  public List<User> getUserByQuery(User user) {
    List<User> users = userMapper.selectUsersByQuery(user);
    String prefix = this.getClass().getResource("/").getPath();
    users.forEach(u -> {
       u.setAvatar("/static"+imgPrefix+u.getAvatar());
    });
    return users;
  }

    public void updateUser(User updateUser, String email) {
       updateUser.setEmail(email);
       BeanHelper.onUpdate(updateUser);
       userMapper.update(updateUser);
    }
}
