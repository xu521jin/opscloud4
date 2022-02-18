package com.baiyi.opscloud.facade.auth.impl;


import com.baiyi.opscloud.common.exception.auth.AuthRuntimeException;
import com.baiyi.opscloud.common.util.BeanCopierUtil;
import com.baiyi.opscloud.domain.DataTable;
import com.baiyi.opscloud.domain.ErrorEnum;
import com.baiyi.opscloud.domain.generator.opscloud.*;
import com.baiyi.opscloud.domain.param.SimpleExtend;
import com.baiyi.opscloud.domain.param.auth.AuthGroupParam;
import com.baiyi.opscloud.domain.param.auth.AuthResourceParam;
import com.baiyi.opscloud.domain.param.auth.AuthRoleParam;
import com.baiyi.opscloud.domain.param.auth.AuthUserRoleParam;
import com.baiyi.opscloud.domain.vo.auth.AuthGroupVO;
import com.baiyi.opscloud.domain.vo.auth.AuthResourceVO;
import com.baiyi.opscloud.domain.vo.auth.AuthRoleResourceVO;
import com.baiyi.opscloud.domain.vo.auth.AuthRoleVO;
import com.baiyi.opscloud.facade.auth.AuthFacade;
import com.baiyi.opscloud.packer.auth.AuthGroupPacker;
import com.baiyi.opscloud.packer.auth.AuthResourcePacker;
import com.baiyi.opscloud.packer.auth.AuthRolePacker;
import com.baiyi.opscloud.service.auth.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Author baiyi
 * @Date 2020/2/13 8:21 下午
 * @Version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {

    private final AuthRoleService authRoleService;

    private final AuthRolePacker authRolePacker;

    private final AuthGroupService authGroupService;

    private final AuthResourceService authResourceService;

    private final AuthResourcePacker authResourcePacker;

    private final AuthRoleResourceService authRoleResourceService;

    private final AuthGroupPacker authGroupPacker;

    private final AuthUserRoleService authUserRoleService;

    @Override
    public DataTable<AuthRoleVO.Role> queryRolePage(AuthRoleParam.AuthRolePageQuery pageQuery) {
        DataTable<AuthRole> table = authRoleService.queryPageByParam(pageQuery);
        List<AuthRoleVO.Role> data = BeanCopierUtil.copyListProperties(table.getData(), AuthRoleVO.Role.class);
        return new DataTable<>(data , table.getTotalNum());
    }

    @Override
    public void addRole(AuthRoleVO.Role role) {
        authRoleService.add(BeanCopierUtil.copyProperties(role, AuthRole.class));
    }

    @Override
    public void updateRole(AuthRoleVO.Role role) {
        authRoleService.update(BeanCopierUtil.copyProperties(role, AuthRole.class));
    }

    @Override
    public void deleteRoleById(int id) {
        if (authRoleResourceService.countByRoleId(id) != 0)
            throw new AuthRuntimeException(ErrorEnum.AUTH_ROLE_HAS_USED);
        authRoleService.deleteById(id);
    }

    @Override
    public DataTable<AuthGroupVO.Group> queryGroupPage(AuthGroupParam.AuthGroupPageQuery pageQuery) {
        DataTable<AuthGroup> table = authGroupService.queryPageByParam(pageQuery);
        List<AuthGroupVO.Group> data = BeanCopierUtil.copyListProperties(table.getData(), AuthGroupVO.Group.class).stream()
                .peek(e -> authGroupPacker.wrap(e, pageQuery)).collect(Collectors.toList());
        return new DataTable<>(data, table.getTotalNum());
    }

    @Override
    public void addGroup(AuthGroupVO.Group group) {
        authGroupService.add(BeanCopierUtil.copyProperties(group, AuthGroup.class));
    }

    @Override
    public void updateGroup(AuthGroupVO.Group group) {
        authGroupService.update(BeanCopierUtil.copyProperties(group, AuthGroup.class));
    }

    @Override
    public void deleteGroupById(int id) {
        if (authResourceService.countByGroupId(id) != 0)
            throw new AuthRuntimeException(ErrorEnum.AUTH_GROUP_HAS_USED);
        authGroupService.deleteById(id);
    }

    @Override
    public DataTable<AuthResourceVO.Resource> queryRoleBindResourcePage(AuthResourceParam.RoleBindResourcePageQuery pageQuery) {
        DataTable<AuthResource> table = authResourceService.queryRoleBindResourcePageByParam(pageQuery);
        List<AuthResourceVO.Resource> data = BeanCopierUtil.copyListProperties(table.getData(), AuthResourceVO.Resource.class)
                .stream().peek(e -> authResourcePacker.wrap(e, SimpleExtend.EXTEND)).collect(Collectors.toList());
        return new DataTable<>(data, table.getTotalNum());
    }

    @Override
    public void addRoleResource(AuthRoleResourceVO.RoleResource roleResource) {
        try {
            authRoleResourceService.add(BeanCopierUtil.copyProperties(roleResource, AuthRoleResource.class));
        } catch (DuplicateKeyException ignored) {
        }
    }

    @Override
    public void deleteRoleResourceById(int id) {
        authRoleResourceService.deleteById(id);
    }

    @Override
    public DataTable<AuthResourceVO.Resource> queryResourcePage(AuthResourceParam.AuthResourcePageQuery pageQuery) {
        DataTable<AuthResource> table = authResourceService.queryPageByParam(pageQuery);
        List<AuthResourceVO.Resource> data = BeanCopierUtil.copyListProperties(table.getData(), AuthResourceVO.Resource.class)
                .stream().peek(e -> authResourcePacker.wrap(e, SimpleExtend.EXTEND)).collect(Collectors.toList());
        return new DataTable<>(data, table.getTotalNum());
    }

    @Override
    public void updateResource(AuthResourceVO.Resource resource) {
        authResourceService.update(BeanCopierUtil.copyProperties(resource, AuthResource.class));
    }

    @Override
    public void addResource(AuthResourceVO.Resource resource) {
        authResourceService.add(BeanCopierUtil.copyProperties(resource, AuthResource.class));
    }

    @Override
    @Transactional(rollbackFor = {AuthRuntimeException.class, Exception.class})
    public void deleteResourceById(int id) {
        authRoleResourceService.deleteByResourceId(id);
        authResourceService.deleteById(id);
    }

    @Override
    public void updateUserRole(AuthUserRoleParam.UpdateUserRole updateUserRole) {
        List<AuthUserRole> roles = authUserRoleService.queryByUsername(updateUserRole.getUsername());
        updateUserRole.getRoleIds().forEach(id -> {
            if (checkAddUserRole(updateUserRole.getUsername(), roles, id)) {
                AuthUserRole pre = new AuthUserRole();
                pre.setUsername(updateUserRole.getUsername());
                pre.setRoleId(id);
                authUserRoleService.add(pre);
            }
        });
        roles.forEach(e -> authUserRoleService.deleteById(e.getId()));
    }

    private boolean checkAddUserRole(String username, List<AuthUserRole> roles, Integer roleId) {
        Iterator<AuthUserRole> iter = roles.iterator();
        while (iter.hasNext()) {
            AuthUserRole authUserRole = iter.next();
            if (username.equals(authUserRole.getUsername()) && roleId.equals(authUserRole.getRoleId())) {
                iter.remove();
                return false;
            }
        }
        return true;
    }

}
