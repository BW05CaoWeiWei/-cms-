package com.caoweiwei.cms.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.caoweiwei.cms.entity.User;
import com.caoweiwei.cms.utils.ConstantFinal;


public class CmsInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		if(request.getSession().getAttribute(ConstantFinal.USER_SESSION_KEY)==null) {
			
			response.sendRedirect("/user/login");
			return false;
		}
		
		String requestURI = request.getRequestURI();
		User user=(User)request.getSession().getAttribute(ConstantFinal.USER_SESSION_KEY);
		
		if(!"1".equals(user.getRole()) && requestURI.matches("^/admin/\\w*")){
			request.setAttribute("error", "您不是管理员！请勿作死");
			request.getRequestDispatcher("/user/login").forward(request, response);
			return false;
		}
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
