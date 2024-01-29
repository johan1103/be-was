package webserver.controller;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpStatus;
import webserver.session.CustomSession;
import webserver.view.HtmlBlock;
import webserver.view.View;

import java.util.*;
import java.util.stream.Collectors;

public class MyController {
  private final Logger logger = LoggerFactory.getLogger(MyController.class);
  @GetMapping(uri = "/user/form.html")
  public View joinForm(String word, Integer age){
    logger.info("Controller executed word : {}, age :{}",word,age);
    return new View("/templates/user/form.html");
  }
  @GetMapping(uri = "/user/login.html")
  public View loginForm(){
    return new View("/templates/user/login.html");
  }
  @GetMapping(uri = "/user/list")
  public View userListpage(){
    HashMap<String,String> attributes = new HashMap<>();
    List<User> users = new ArrayList<>(Database.findAll());
    attributes.put("{$login-block}",HtmlBlock.buildLoginBlock(CustomSession.getUserFromCurrentSession().getUserId()));
    attributes.put("{$user-list-block}",HtmlBlock.buildUserListBlock(users));
    return new View("/templates/user/list.html",attributes);
  }
  @GetMapping(uri = "/index.html")
  public View mainPage(){
    HashMap<String, String> attributes = new HashMap<>();
    User user = CustomSession.getUserFromCurrentSession();
    if(user==null)
      attributes.put("{$login-block}",HtmlBlock.LOGOUT.block);
    else{
      attributes.put("{$login-block}",HtmlBlock.buildLoginBlock(user.getUserId()));
    }
    return new View("/templates/index.html",attributes);
  }
  @PostMapping(uri = "/user/create")
  public View join(@RequestBody UserForm userForm){
    if(Database.findUserById(userForm.getUserId())==null)
      Database.addUser(new User(userForm.getUserId(), userForm.getPassword(), userForm.getName(), userForm.getEmail()));
    else
      throw new DuplicateUserException();
    return new View("/index.html", HttpStatus.REDIRECT);
  }

  @PostMapping(uri = "/user/login")
  public View login(@RequestBody LoginForm loginForm){
    User findUser = Database.findUserById(loginForm.userId);
    if(findUser==null)
      throw new RuntimeException();
    if(loginForm.correctUser(findUser)) {
      UUID sessionKey = CustomSession.registerUser(findUser.getUserId());
      CustomSession.setCookie.set(sessionKey);
      return new View("/index.html",HttpStatus.REDIRECT);
    }
    return new View("/user/login_failed.html",HttpStatus.REDIRECT);
  }
  @GetMapping(uri = "/user/login_failed.html")
  public View failedLogin(){
    return new View("/templates/user/login_failed.html");
  }
  public static class LoginForm{
    private String userId;
    private String password;
    public LoginForm(){
    }
    public boolean correctUser(User user){
      return this.userId.equals(user.getUserId()) && this.password.equals(user.getPassword());
    }
  }
}
