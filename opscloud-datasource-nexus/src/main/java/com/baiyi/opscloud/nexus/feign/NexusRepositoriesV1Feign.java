package com.baiyi.opscloud.nexus.feign;

import com.baiyi.opscloud.nexus.entry.NexusRepository;
import feign.Headers;
import feign.RequestLine;

import java.util.List;

/**
 * @Author baiyi
 * @Date 2021/10/15 11:27 上午
 * @Version 1.0
 */
public interface NexusRepositoriesV1Feign {

    @RequestLine("GET /service/rest/v1/repositories")
    @Headers("accept: application/json")
    List<NexusRepository.Repository> listRepositories();
}