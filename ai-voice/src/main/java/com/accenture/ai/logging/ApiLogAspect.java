package com.accenture.ai.logging;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.Gson;

@Aspect
@Component
public class ApiLogAspect {

	private static final LogAgent LOGGER = LogAgent.getLogAgent(ApiLogAspect.class);
	private static final Gson GSON = new Gson();

	@Pointcut("execution(* com.accenture.ai.controller.*.*(..))")
	private void controllerAspect() {
	}

	@Before(value = "controllerAspect()")
	public void beforeTest(JoinPoint point) throws Throwable {

		if ("health".equals(point.getSignature().getName())) {
			return;
		}
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("HttpRequest log:");
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();

		// add url to log
		if (ra != null) {
			strBuilder.append(" URI:");
			ServletRequestAttributes sra = (ServletRequestAttributes) ra;
			strBuilder.append(sra.getRequest().getRequestURI());
			strBuilder.append(";");
		}

		// add the Args of method to log
		buildArgs(point.getArgs(), strBuilder);
		LOGGER.info(strBuilder.toString());

	}

	@AfterReturning(returning = "obj", pointcut = "controllerAspect()")
	public void methodAfterReturing(JoinPoint point, Object obj) {
		if ("health".equals(point.getSignature().getName())) {
			return;
		}
		if (obj != null) {
			LOGGER.info("HttpResponse log:" + GSON.toJson(obj));
		}
	}

	private void buildArgs(Object[] args, StringBuilder strBuilder) {
		if (args != null) {
			strBuilder.append("Controller Received Parameters：");
			for (Object obj : args) {
				if (obj == null || obj instanceof HttpSession 
						|| obj instanceof ServletRequest
						|| obj instanceof ServletResponse 
						|| obj instanceof BindingResult) {
					continue;
				}
				strBuilder.append(obj.getClass().getSimpleName());
				strBuilder.append(":");
				strBuilder.append(GSON.toJson(obj));
				strBuilder.append(";");
			}
		}
	}
}
