/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.sliew.carp.module.security.spring.authentication;

import cn.sliew.carp.framework.web.util.I18nUtil;
import cn.sliew.carp.module.security.core.service.SecAuthorizationService;
import cn.sliew.carp.module.security.core.service.SecUserService;
import cn.sliew.carp.module.security.core.service.dto.SecRoleDTO;
import cn.sliew.carp.module.security.core.service.dto.SecUserDTO;
import cn.sliew.carp.module.security.core.service.param.authorize.SecRoleListByUserParam;
import cn.sliew.carp.module.security.spring.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CarpUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SecUserService secUserService;
    @Autowired
    private SecAuthorizationService secAuthorizationService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        try {
            Optional<SecUserDTO> optional = secUserService.getByUserName(userName);
            SecUserDTO secUserDTO = optional.orElseThrow(() -> new BadCredentialsException(I18nUtil.get("response.error.login.password")));
            return fillUserDetails(secUserDTO);
        } catch (Exception e) {
            log.error("获取用户信息异常! userName: {}", userName, e);
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    private CarpUserDetail fillUserDetails(SecUserDTO secUserDTO) {
        CarpUserDetail user = new CarpUserDetail();
        user.setUser(secUserDTO);
        user.setRoles(listAllRoles(secUserDTO.getId()));
        // todo 增加 resource-web

        user.setAuthorities(roles2GrantedAuthority(user.getRoles()));
        return user;
    }

    private List<SecRoleDTO> listAllRoles(Long userId) {
        SecRoleListByUserParam param = new SecRoleListByUserParam();
        param.setUserId(userId);
        return secAuthorizationService.listAuthorizedRolesByUserId(param);
    }

    private List<GrantedAuthority> roles2GrantedAuthority(List<SecRoleDTO> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        String[] roleAuthrities = roles.stream()
                .map(role -> SecurityConstants.ROLE_AUTHORITY_PREFIX + role.getCode().toUpperCase())
                .toArray(length -> new String[length]);
        return AuthorityUtils.createAuthorityList(roleAuthrities);
    }
}
