package com.accenture.ai.service.user.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.accenture.ai.model.User;
import com.accenture.ai.service.user.UserRoleService;
import com.accenture.ai.service.user.UserService;

public class AIUserDetailsServiceImpl implements UserDetailsService {

	@Autowired

	private UserService userService;

	@Autowired

	private UserRoleService userRoleService;


	@Override

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userService.findByUsername(username);

		if (user == null) {

			throw new UsernameNotFoundException("用户名：" + username + "不存在！");

		}

		Collection<SimpleGrantedAuthority> collection = new HashSet<SimpleGrantedAuthority>();

		Iterator<String> iterator = userRoleService.findRoles(user.getId()).iterator();

		while (iterator.hasNext()) {

			collection.add(new SimpleGrantedAuthority(iterator.next()));

		}

		return new org.springframework.security.core.userdetails.User(username, user.getPassword(), collection);

	}

}