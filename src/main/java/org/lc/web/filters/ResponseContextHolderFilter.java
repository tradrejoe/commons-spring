package org.lc.web.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ResponseContextHolderFilter extends OncePerRequestFilter {

	   @Override
	   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
	      try {
	         RequestAndResponseContextHolder.response(response);
	      } finally {
	         filterChain.doFilter(request, response);
	      }
	   }
	}
