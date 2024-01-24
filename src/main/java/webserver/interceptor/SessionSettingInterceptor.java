package webserver.interceptor;

import webserver.MyHttpServletRequest;
import webserver.MyHttpServletResponse;
import webserver.session.CustomSession;

public class SessionSettingInterceptor implements Interceptor{
  @Override
  public boolean preHandle(MyHttpServletRequest request) {
    CustomSession.initCurrentSession(request);
    return true;
  }

  @Override
  public void afterCompletion(MyHttpServletResponse response) {
    response.addCookieHeader();
    CustomSession.finishCurrentSession();
  }
}
