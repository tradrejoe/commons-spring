package org.lc.web.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import static org.lc.db.dto.DataTableDTO.*;

@Component
@Aspect
public class DataTableAspect extends BaseHttpRequestAspect {

	//@Around("dataTablePointcut()")
	//@Around("execution(* dataTable*(..))")
	@Around(value="@annotation(org.lc.web.annotations.DataTableHandler)", argNames="point")
	public Object dataTableAroundAdvice(ProceedingJoinPoint point) throws Throwable {
		Object value = EMPTY_DATATABLE_DTO;
		//if (!org.lc.web.controller.BaseController.isLoggedIn()) return value; 
		try {
			value = point.proceed();
			super.process();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return value;		
	}
	
	@Pointcut("execution(* dataTable*(..))")
	public void dataTablePointcut(){}
}
