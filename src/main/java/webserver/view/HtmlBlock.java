package webserver.view;

import model.User;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public enum HtmlBlock {
  LOGOUT("<li><a href=\"user/login.html\" role=\"button\">로그인</a></li>"),
  LOGIN("<li>{user-id}님</li>"),
  USER_LIST("<tr>\n" +
          "<th scope=\"row\">{row}</th>" +
          "<td>{user-id}</td>" +
          "<td>{user-name}</td>" +
          "<td>{user-email}</td>" +
          "<td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>\n" +
          "</tr>");
  public final String block;
  HtmlBlock(String block){
    this.block=block;
  }

  public static String buildLoginBlock(String id){
    return LOGIN.block.replace("{user-id}",id);
  }
  public static String buildUserListBlock(List<User> users){
    StringBuilder stringBuilder = new StringBuilder();
    int row=0;
    for(User u : users){
      String userBlock = USER_LIST.block
              .replace("{row}",String.valueOf(++row))
              .replace("{user-id}",u.getUserId()).replace("{user-name}",u.getName())
              .replace("{user-email}",u.getEmail());
      stringBuilder.append(userBlock);
    }
    return stringBuilder.toString();
  }
}
