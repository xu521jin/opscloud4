package com.baiyi.opscloud.datasource.accountGroup.impl;

import com.baiyi.opscloud.common.datasource.LdapDsInstanceConfig;
import com.baiyi.opscloud.common.constant.enums.DsTypeEnum;
import com.baiyi.opscloud.datasource.accountGroup.impl.base.AbstractAccountGroupProvider;
import com.baiyi.opscloud.domain.generator.opscloud.DatasourceConfig;
import com.baiyi.opscloud.domain.generator.opscloud.User;
import com.baiyi.opscloud.domain.generator.opscloud.UserGroup;
import com.baiyi.opscloud.domain.types.BusinessTypeEnum;
import com.baiyi.opscloud.domain.vo.business.BaseBusiness;
import com.baiyi.opscloud.datasource.ldap.repo.GroupRepo;
import com.baiyi.opscloud.service.user.UserGroupService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author baiyi
 * @Date 2021/9/14 5:45 下午
 * @Version 1.0
 */
@Component
public class LdapAccountGroupProvider extends AbstractAccountGroupProvider {

    @Resource
    private GroupRepo groupRepo;

    @Resource
    private UserGroupService userGroupService;

    protected static ThreadLocal<LdapDsInstanceConfig.Ldap> configContext = new ThreadLocal<>();

    @Override
    protected void initialConfig(DatasourceConfig dsConfig) {
        configContext.set(dsConfigHelper.build(dsConfig, LdapDsInstanceConfig.class).getLdap());
    }

    @Override
    protected void doCreate(UserGroup userGroup) {
        try {
            groupRepo.create(configContext.get(), userGroup.getName());
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void doUpdate(UserGroup userGroup) {
    }

    @Override
    protected void doDelete(UserGroup userGroup) {
        try {
            groupRepo.delete(configContext.get(), userGroup.getName());
        } catch (Exception ignored) {
        }
    }

    @Override
    public void doGrant(User user, BaseBusiness.IBusiness businessResource) {
        groupRepo.addGroupMember(configContext.get(), getBusinessResource(businessResource.getBusinessId()).getName(), user.getUsername());
    }

    @Override
    public void doRevoke(User user, BaseBusiness.IBusiness businessResource) {
        groupRepo.removeGroupMember(configContext.get(), getBusinessResource(businessResource.getBusinessId()).getName(), user.getUsername());
    }

    private UserGroup getBusinessResource(int businessId) {
        return userGroupService.getById(businessId);
    }

    @Override
    public int getBusinessResourceType() {
        return BusinessTypeEnum.USERGROUP.getType();
    }

    @Override
    public String getInstanceType() {
        return DsTypeEnum.LDAP.getName();
    }

}
