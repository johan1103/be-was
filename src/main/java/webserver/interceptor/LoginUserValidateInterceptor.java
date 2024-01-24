package webserver.interceptor;

import webserver.MyHttpServletRequest;

public class LoginUserValidateInterceptor implements Interceptor {
  @Override
  public boolean preHandle(MyHttpServletRequest request) {
    return Interceptor.super.preHandle(request);
  }
}
