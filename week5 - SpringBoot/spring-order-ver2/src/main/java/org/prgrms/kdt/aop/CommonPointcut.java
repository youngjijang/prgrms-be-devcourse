package org.prgrms.kdt.aop;

import org.aspectj.lang.annotation.Pointcut;

public class CommonPointcut {
    @Pointcut("execution(public * org.prgrms.kdt..*.*(..))")
    public void servicePublicMethodPointcut(){};

    @Pointcut("execution(* org.prgrms.kdt..*Repository.*(..))")
    public void repositoryPublicMethodPointcut(){};
}
