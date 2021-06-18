package com.baiyi.caesar.facade.user.factory;

import com.baiyi.caesar.facade.user.base.IUserBusinessPermissionPageQuery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author baiyi
 * @Date 2021/6/17 9:30 上午
 * @Version 1.0
 */
public class UserBusinessPermissionFactory {

    private UserBusinessPermissionFactory() {
    }

    static Map<Integer, IUserBusinessPermissionPageQuery> context = new ConcurrentHashMap<>();

    public static IUserBusinessPermissionPageQuery getByBusinessType(int businessType) {
        return context.get(businessType);
    }

    public static void register(IUserBusinessPermissionPageQuery bean) {
        context.put(bean.getBusinessType(), bean);
    }


}
