package com.kitty.blog.service;

import com.kitty.blog.dto.user.LoginResponseDto;
import com.kitty.blog.model.Role;
import com.kitty.blog.model.User;
import com.kitty.blog.model.userRole.UserRole;
import com.kitty.blog.repository.RoleRepository;
import com.kitty.blog.repository.UserRepository;
import com.kitty.blog.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username){
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null){
            return null;
        }
        List<UserRole> byUserId = userRoleRepository.findByUserId(user.getUserId()).orElse(null);
        assert byUserId != null;
        if (byUserId.isEmpty()){
            return null;
        }
        List<String> roles = new ArrayList<>();
        for (UserRole userRole : byUserId){
            String roleName =
                    ((Role)roleRepository.findById(userRole.getId().getRoleId()).orElse(null))
                            .getAdministratorName();
            roles.add(roleName);
        }
        Set<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        // 创建实例
        LoginResponseDto userDetails = new LoginResponseDto();
        userDetails.setId(user.getUserId());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());
        userDetails.setToken(user.getToken());
        userDetails.setRoles(authorities);
        return userDetails;

    }

}
