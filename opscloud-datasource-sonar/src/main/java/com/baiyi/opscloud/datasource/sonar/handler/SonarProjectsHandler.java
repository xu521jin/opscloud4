package com.baiyi.opscloud.datasource.sonar.handler;

import com.baiyi.opscloud.common.datasource.SonarDsInstanceConfig;
import com.baiyi.opscloud.datasource.sonar.entry.SonarProjects;
import com.baiyi.opscloud.datasource.sonar.enums.QualifierEnum;
import com.baiyi.opscloud.datasource.sonar.feign.SonarProjectsFeign;
import com.baiyi.opscloud.datasource.sonar.param.PagingParam;
import com.baiyi.opscloud.datasource.sonar.param.SonarQubeRequestBuilder;
import com.baiyi.opscloud.domain.model.Authorization;
import feign.Feign;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author baiyi
 * @Date 2021/10/22 4:33 下午
 * @Version 1.0
 */
@Slf4j
@Component
public class SonarProjectsHandler {

    private static final QualifierEnum[] QUALIFIERS = {QualifierEnum.TRK,QualifierEnum.APP, QualifierEnum.VW};

    private Map<String, String> buildSearchProjectsParam(PagingParam pagingParam) {
        return SonarQubeRequestBuilder.newBuilder()
                .paramEntry(pagingParam)
                .paramEntry(QUALIFIERS)
                .build()
                .getParams();
    }

    public SonarProjects searchProjects(SonarDsInstanceConfig.Sonar config, PagingParam pagingParam) {
        SonarProjectsFeign sonarAPI = Feign.builder()
                .retryer(new Retryer.Default(3000, 3000, 3))
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(SonarProjectsFeign.class, config.getUrl());
        Authorization.Token token = Authorization.Token.builder()
                .token(config.getToken())
                .build();
        return sonarAPI.searchProjects(token.toBasic(), buildSearchProjectsParam(pagingParam));
    }
}
