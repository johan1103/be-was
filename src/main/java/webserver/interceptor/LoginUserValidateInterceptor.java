package webserver.interceptor;

import webserver.MyHttpServletRequest;
import webserver.session.CustomSession;

import java.util.Arrays;
import java.util.UUID;

public class LoginUserValidateInterceptor implements Interceptor {
  private static final String[] includePath = {"/user/list"};
  @Override
  public boolean preHandle(MyHttpServletRequest request) {
    UUID sessionId = CustomSession.currentSession.get();
    if(needAuthorization(request))
      if(!successAuthorization(sessionId))
        throw new AuthorizationException();
    return true;
  }
  private boolean needAuthorization(MyHttpServletRequest request){
    String uri = request.getUri();
    return Arrays.asList(includePath).contains(uri);
  }
  private boolean successAuthorization(UUID sessionId){
    if(sessionId==null)
      return false;
    return CustomSession.findSessionByKey(sessionId) != null;
  }
}
