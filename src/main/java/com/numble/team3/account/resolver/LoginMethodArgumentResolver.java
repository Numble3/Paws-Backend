package com.numble.team3.account.resolver;

import com.numble.team3.account.annotation.LoginUser;
import com.numble.team3.exception.sign.TokenFailureException;
import com.numble.team3.jwt.PrivateClaims;
import com.numble.team3.jwt.TokenHelper;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginMethodArgumentResolver implements HandlerMethodArgumentResolver {
  private final TokenHelper refreshTokenHelper;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterAnnotation(LoginUser.class) != null
        && parameter.getParameterType().equals(UserInfo.class);
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory)
      throws RuntimeException {
    HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

    PrivateClaims privateClaims =
        refreshTokenHelper
            .parse(extractTokenWithRequest(request))
            .orElseThrow(TokenFailureException::new);
    Long accountId = Long.valueOf(privateClaims.getAccountId());
    List<String> roleTypes = privateClaims.getRoleTypes();
    return new UserInfo(accountId, roleTypes);
  }

  private String extractTokenWithRequest(HttpServletRequest request) {
    return Arrays.stream(request.getCookies())
            .filter(cookie -> cookie.getName().equals("refreshToken"))
            .findAny()
            .orElseThrow(TokenFailureException::new)
            .getValue();
  }
}
