package club.neters.user.core.config.security;

import club.neters.user.core.constant.CommonConstant;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * @author wuare
 * @date 2021/7/1
 */
public class CustomAccessDecisionVoter implements AccessDecisionVoter<FilterInvocation> {

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, FilterInvocation invocation, Collection<ConfigAttribute> attributes) {
        // TODO 扩展
        String[] whiteList = CommonConstant.SECURITY_WHITELIST;
        String[] permissionRequests = {"/test"};
        HttpServletRequest request = invocation.getRequest();
        // OPTIONS全放开
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return ACCESS_GRANTED;
        }
        // 白名单全放开
        for (String url : whiteList) {
            RequestMatcher pathMatcher = new AntPathRequestMatcher(url);
            if (pathMatcher.matches(request)) {
                return ACCESS_GRANTED;
            }
        }

        // 开始处理授权验证逻辑
        // TODO 此处可从数据库扩展处理，也可以使用任意其他的声明
        String userNameVal = "";
        try {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            userNameVal = String.valueOf(jwt.getClaims().get("username"));
        } catch (Exception ignored) {
        }
        for (String url : permissionRequests) {
            RequestMatcher pathMatcher = new AntPathRequestMatcher(url);
            if (pathMatcher.matches(request) && userNameVal.equals("test")) {
                return ACCESS_GRANTED;
            }
        }
        return ACCESS_DENIED;
    }
}
