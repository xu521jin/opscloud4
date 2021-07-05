package com.baiyi.opscloud.datasource.factory;


import com.baiyi.opscloud.datasource.provider.base.asset.SimpleAssetProvider;
import com.google.common.collect.ArrayListMultimap;
import org.springframework.data.util.CastUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author baiyi
 * @Date 2021/6/18 11:06 上午
 * @Version 1.0
 */

public class AssetProviderFactory {

    private AssetProviderFactory() {
    }

    //         instanceType & key
    private static Map<String, ArrayListMultimap<String, SimpleAssetProvider>> context = new ConcurrentHashMap<>();

    public static <T extends SimpleAssetProvider> T getProvider(String instanceType, String assetType) {
        if (context.containsKey(instanceType))
            return CastUtils.cast(context.get(instanceType).get(assetType).get(0));
        return null;
    }

    public static <T extends SimpleAssetProvider> List<T> getProviders(String instanceType, String assetType) {
        if (context.containsKey(instanceType))
            return CastUtils.cast(context.get(instanceType).get(assetType));
        return null;
    }

    public static <T extends SimpleAssetProvider> void register(T bean) {
        if (context.containsKey(bean.getInstanceType())) {
            context.get(bean.getInstanceType()).put(bean.getAssetType(), bean);
        } else {
            ArrayListMultimap<String, SimpleAssetProvider> multimap = ArrayListMultimap.create();
            multimap.put(bean.getAssetType(), bean);
            context.put(bean.getInstanceType(), multimap);
        }
    }
}