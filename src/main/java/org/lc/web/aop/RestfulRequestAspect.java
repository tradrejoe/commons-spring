package org.lc.web.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class RestfulRequestAspect  extends BaseHttpRequestAspect {

	@Around(value="@annotation(org.lc.web.annotations.RestfulRequestHandler)", argNames="point")
	public Object dataTableAroundAdvice(ProceedingJoinPoint point) throws Throwable {
		Object value = null;
		//if (!org.lc.web.controller.BaseController.isLoggedIn()) return value; 
		try {
			value = point.proceed();
			super.process();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return value;		
	}
	
}