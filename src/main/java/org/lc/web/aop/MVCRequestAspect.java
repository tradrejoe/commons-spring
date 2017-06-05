package org.lc.web.aop;

import static org.lc.db.dto.DataTableDTO.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MVCRequestAspect  extends BaseHttpRequestAspect {
	
	@Around(value="@annotation(org.lc.web.annotations.MVCRequestHandler)", argNames="point")
	public Object dataTableAroundAdvice(ProceedingJoinPoint point) throws Throwable {
		Object value = null;
		//if (!org.lc.web.controller.BaseController.isLoggedIn()) return PROVIDER_PORTAL_HOME; 
		try {
			value = point.proceed();
			super.process();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return value;		
	}
	
}