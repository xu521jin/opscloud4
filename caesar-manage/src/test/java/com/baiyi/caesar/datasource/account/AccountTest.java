package com.baiyi.caesar.datasource.account;

import com.baiyi.caesar.BaseUnit;
import com.baiyi.caesar.account.factory.AccountProviderFactory;
import com.baiyi.caesar.domain.generator.caesar.DatasourceInstance;
import com.baiyi.caesar.domain.vo.datasource.DsInstanceVO;
import com.baiyi.caesar.packer.datasource.DsInstancePacker;
import com.baiyi.caesar.service.datasource.DsInstanceService;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * @Author baiyi
 * @Date 2021/6/10 1:50 下午
 * @Version 1.0
 */
public class AccountTest extends BaseUnit {

    @Resource
    private DsInstanceService dsInstancService;

    @Resource
    private DsInstancePacker dsInstancePacker;

    @Test
    void pullAccount() {
        DatasourceInstance dsInstance = dsInstancService.getById(1);
        DsInstanceVO.Instance instance = DsInstancePacker.toVO(dsInstance);
        dsInstancePacker.wrap(instance);
        AccountProviderFactory.getAccountByKey("LDAP").pullAccount(instance);
    }

}
