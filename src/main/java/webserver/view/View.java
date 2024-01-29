package webserver.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpStatus;
import webserver.MyHttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class View {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private HashMap<String,String> attributes;
  private String path;
  private HttpStatus httpStatus;
  public View(String path){
    this(path,new HashMap<>(),HttpStatus.OK);
  }
  public View(String path,HttpStatus httpStatus){
    this(path,new HashMap<>(),httpStatus);
  }
  public View(String path,HashMap<String,String> attributes){
    this(path,attributes,HttpStatus.OK);
  }
  public View(String path,HashMap<String,String> attributes,HttpStatus httpStatus){
    this.path=path;
    this.attributes=attributes;
    this.httpStatus=httpStatus;
  }
  public void addAttributes(String key, String value){
    attributes.put(key, value);
  }
  public MyHttpServletResponse buildResponse(){
    if(httpStatus.equals(HttpStatus.REDIRECT))
      return MyHttpServletResponse.redirect(path);
    else
      return buildHtmlResponse();
  }
  public MyHttpServletResponse buildHtmlResponse(){
    byte[] html;
    String stringHtml="";
    try (InputStream in = this.getClass().getResourceAsStream(this.path)){
      if(in==null)
        throw new RuntimeException();
      html = in.readAllBytes();
      stringHtml = new String(html);
    }catch (IOException ioException){
      logger.error(ioException.getMessage());
    }
    if(stringHtml.isEmpty())
      return new MyHttpServletResponse();
    for(Map.Entry<String,String> entry : attributes.entrySet())
      stringHtml=replaceContent(entry.getKey(),stringHtml);
    return new MyHttpServletResponse(HttpStatus.OK,stringHtml.getBytes());
  }
  public String replaceContent(String key, String html){
    String toReplace = attributes.get(key);
    if(!html.contains(key))
      throw new RuntimeException();
    return html.replace(key,toReplace);
  }
}
