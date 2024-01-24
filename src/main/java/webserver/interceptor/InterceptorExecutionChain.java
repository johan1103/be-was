package webserver.interceptor;

import webserver.MyHttpServletRequest;
import webserver.MyHttpServletResponse;

import java.util.ArrayList;
import java.util.List;

public class InterceptorExecutionChain {
  private final List<Interceptor> interceptors = new ArrayList<>();
  public InterceptorExecutionChain(){
    interceptors.add(new LoginUserValidateInterceptor());
  }
  public boolean applyPreHandle(MyHttpServletRequest request){
    boolean successPreHandling = true;
    for(Interceptor i : interceptors){
      successPreHandling = successPreHandling & i.preHandle(request);
    }
    return successPreHandling;
  }

  public void afterCompletion(MyHttpServletResponse response){
    try {
     for (Interceptor i : interceptors){
       i.afterCompletion(response);
     }
    }catch (Exception ignored){
    }
  }
}
