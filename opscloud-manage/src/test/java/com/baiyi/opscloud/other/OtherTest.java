package com.baiyi.opscloud.other;

import com.baiyi.opscloud.BaseUnit;
import com.baiyi.opscloud.common.redis.RedisUtil;
import com.baiyi.opscloud.common.util.NewTimeUtil;
import com.baiyi.opscloud.workorder.validator.QueueValidator;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import jakarta.validation.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

/**
 * @Author baiyi
 * @Date 2022/4/5 12:14
 * @Version 1.0
 */
public class OtherTest extends BaseUnit {

    @Resource
    private ValidatorFactory validatorFactory;

    @Resource
    private Validator validator;

    @Resource
    private QueueValidator queueValidator;


    @Resource
    private RedisUtil redisUtil;


    @Data
    @Builder
    @Valid
    public static class ATest {

        @NotNull
        @Size(min = 15, max = 900, message = "AADDBBB")
        private String id;

    }


    @Test
    void test1() {
        ATest a = ATest.builder()
                .id("1")
                .build();

        ValidatorFactory vF = Validation.buildDefaultValidatorFactory();

        Validator v = validatorFactory.getValidator();

        StringBuilder sb = new StringBuilder();

        Set<ConstraintViolation<ATest>> constraintViolationSet = validator.validate(a);
        constraintViolationSet.forEach(vInfo -> {
            sb.append(vInfo.getMessage());
        });

        print(sb.toString());
    }

    @Test
    void test2() {
        // 校验工单条目失败: 消息保留周期应介于1分钟至14天之间、最大消息大小应介于1KB和256KB之间
        Map<String, String> attributes = Maps.newHashMap();
        attributes.put("DelaySeconds", "1");
        attributes.put("MaximumMessageSize", "262144");
        // 60
        attributes.put("MessageRetentionPeriod", "1");
        attributes.put("ReceiveMessageWaitTimeSeconds", "1");
        attributes.put("VisibilityTimeout", "1");
        queueValidator.verify(attributes);
    }

    @Test
    void test3() throws InterruptedException {
//        consulAlertRule.preData();
//        DsAssetParam.AssetPageQuery pageQuery = DsAssetParam.AssetPageQuery.builder()
//                .assetType("CONSUL_SERVICE")
//                .extend(true)
//                .instanceId(36)
//                .length(5)
//                .page(1)
//                .queryName("device-check")
//                .relation(false)
//                .build();
//        List<DsAssetVO.Asset> assetList = dsInstanceAssetFacade.queryAssetPage(pageQuery).getData();
//        DatasourceInstance dsInstance = dsInstanceService.getById(36);
//        DatasourceConfig dsConfig = dsConfigHelper.getConfigByInstanceUuid(dsInstance.getUuid());
//        ConsulConfig.Consul consul = dsConfigHelper.build(dsConfig, ConsulConfig.class).getConsul();
//        print("11111");
//        assetList.forEach(asset -> consulAlertRule.evaluate(asset, consul.getStrategyMatchExpressions()));
//        print("22222");
//        assetList.forEach(asset -> consulAlertRule.evaluate(asset, consul.getStrategyMatchExpressions()));
//        print("33333");
//        assetList.forEach(asset -> consulAlertRule.evaluate(asset, consul.getStrategyMatchExpressions()));
//        print("44444");
//        assetList.forEach(asset -> consulAlertRule.evaluate(asset, consul.getStrategyMatchExpressions()));
//        print("55555");
//        assetList.forEach(asset -> consulAlertRule.evaluate(asset, consul.getStrategyMatchExpressions()));
//        TimeUnit.MINUTES.sleep(20L);
    }

    @Test
    void test4() throws InterruptedException {
        String key = "redisUtilTest";
        redisUtil.set(key, 1, 1);
        Integer count = (Integer) redisUtil.get(key);
        print(count);
        redisUtil.incr(key, 1);
        Integer count1 = (Integer) redisUtil.get(key);
        print(count1);
        NewTimeUtil.sleep(2L);
        Integer count2 = (Integer) redisUtil.get(key);
        print(count2);
    }

}
