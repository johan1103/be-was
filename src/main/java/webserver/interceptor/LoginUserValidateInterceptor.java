package webserver.interceptor;

import webserver.MyHttpServletRequest;

public class LoginUserValidateInterceptor implements RequestHandlerInterceptor{
  @Override
  public boolean preHandle(MyHttpServletRequest request) {
    return RequestHandlerInterceptor.super.preHandle(request);
  }
}
