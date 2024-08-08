package com.impower.tingshu.common.login;

import java.lang.annotation.*;

/**
 * 自定义注解：在方法上使用该注解，验证认证状态；鉴别用户身份
 * <p>
 * 元注解：
 *
 * @Target：注解使用位置
 * @Retention: 注解保留阶段（源码，字节码，运行）
 * @Inherited: 注解是否可以被继承
 * @Documented：产生java文档 javadoc命令生成API文档是否包含注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ImpowerLogin {


    /**
     * 方法/注解属性 required：默认情况要求调用使用ImpowerLogin方法必须登录才能访问
     * @return
     */
    boolean required() default true;

}