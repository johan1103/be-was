package webserver.interceptor;

import webserver.MyHttpServletRequest;
import webserver.MyHttpServletResponse;

public interface RequestHandlerInterceptor {
  default boolean preHandle(MyHttpServletRequest request){
    return true;
  }
  default void afterCompletion(MyHttpServletResponse response){
  }
}
