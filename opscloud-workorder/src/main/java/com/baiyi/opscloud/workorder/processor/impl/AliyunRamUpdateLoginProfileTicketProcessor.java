package com.baiyi.opscloud.workorder.processor.impl;

import com.baiyi.opscloud.common.constants.enums.DsTypeEnum;
import com.baiyi.opscloud.datasource.facade.UserAmFacade;
import com.baiyi.opscloud.domain.generator.opscloud.DatasourceInstanceAsset;
import com.baiyi.opscloud.domain.generator.opscloud.User;
import com.baiyi.opscloud.domain.generator.opscloud.WorkOrderTicketEntry;
import com.baiyi.opscloud.domain.param.user.UserAmParam;
import com.baiyi.opscloud.domain.param.workorder.WorkOrderTicketEntryParam;
import com.baiyi.opscloud.workorder.constants.WorkOrderKeyConstants;
import com.baiyi.opscloud.workorder.exception.TicketProcessException;
import com.baiyi.opscloud.workorder.exception.TicketVerifyException;
import com.baiyi.opscloud.workorder.processor.impl.extended.AbstractDsAssetPermissionExtendedBaseTicketProcessor;
import com.baiyi.opscloud.workorder.util.AmPasswordUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author baiyi
 * @Date 2023/6/7 11:12
 * @Version 1.0
 */
@Slf4j
@Component
public class AliyunRamUpdateLoginProfileTicketProcessor  extends AbstractDsAssetPermissionExtendedBaseTicketProcessor {

    @Resource
    private UserAmFacade userAmFacade;

//    @Resource
//    private StringEncryptor stringEncryptor;

    @Override
    protected void process(WorkOrderTicketEntry ticketEntry, DatasourceInstanceAsset entry) throws TicketProcessException {
        User createUser = queryCreateUser(ticketEntry);

        // 使用OC账户密码
        // String newPassword = stringEncryptor.decrypt(createUser.getPassword());

        String newPassword = AmPasswordUtil.generatorRandomPassword();

        UserAmParam.UpdateLoginProfile updateLoginProfile = UserAmParam.UpdateLoginProfile.builder()
                .instanceUuid(ticketEntry.getInstanceUuid())
                .username(createUser.getUsername())
                .password(newPassword)
                .build();
        try {
            userAmFacade.updateLoginProfile(updateLoginProfile);
        } catch (Exception e) {
            throw new TicketProcessException("工单更新用户登录配置失败: {}", e.getMessage());
        }
    }

    @Override
    public void handleVerify(WorkOrderTicketEntryParam.TicketEntry ticketEntry) throws TicketVerifyException {
        DatasourceInstanceAsset entry = this.toEntry(ticketEntry.getContent());
        DatasourceInstanceAsset asset = getAsset(entry);
        verifyEntry(asset);
    }

    @Override
    public String getKey() {
        return WorkOrderKeyConstants.ALIYUN_RAM_UPDATE_LOGIN_PROFILE.name();
    }

    @Override
    public String getInstanceType() {
        return DsTypeEnum.ALIYUN.name();
    }

}