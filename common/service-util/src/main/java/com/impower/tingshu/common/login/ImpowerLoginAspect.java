package com.impower.tingshu.common.login;

import com.impower.tingshu.common.constant.RedisConstant;
import com.impower.tingshu.common.execption.GuiguException;
import com.impower.tingshu.common.result.ResultCodeEnum;
import com.impower.tingshu.common.util.AuthContextHolder;
import com.impower.tingshu.model.user.UserInfo;
import com.impower.tingshu.vo.user.UserInfoVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @classname tingshu-parent
 * @Auther d3Lap1ace
 * @Time 6/8/2024 11:29 周二
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
@Slf4j
@Aspect
@Component
public class ImpowerLoginAspect {

    @Autowired
    private RedisTemplate redisTemplate;


    @Around("execution(* com.impower.tingshu.*.api.*.*(..)) && @annotation(impowerLogin)")
    public Object doBasicProfiling(ProceedingJoinPoint pjp,ImpowerLogin impowerLogin) throws Throwable {
        log.info("[认证切面]前置逻辑...");
        //1.获取请求头token中用户访问令牌
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = sra.getRequest();
        // 获取请求头：token
        String token = request.getHeader("token");
        // 2. 根据令牌获取存入到redis中用户信息（当初登录存入Redis中key=user:login:token Value=UserInfoVo）
        // 构建查询key
        String key = RedisConstant.USER_LOGIN_KEY_PREFIX+token;
        UserInfoVo userInfovo = (UserInfoVo)redisTemplate.opsForValue().get(key);
        // 3.判断目标方法是否需要登录
        if(impowerLogin.required() && userInfovo == null) {
            throw new GuiguException(ResultCodeEnum.LOGIN_AUTH);
        }
        // 4.将用户ID存入ThreadLocal
        if(userInfovo != null) {
            AuthContextHolder.setUserId(userInfovo.getId());
        }
        //5.执行目标方法时候，如果用户登录将用户ID存入ThreadLocal，自然在目标方法（controller，service,mapper）从ThreadLocal获取用户ID
        Object retVal = pjp.proceed();
        log.info("[认证切面]后置逻辑...");
        //6.清理ThreadLocal
        AuthContextHolder.removeUserId();
        return retVal;

    }
}
