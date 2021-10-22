package com.baiyi.opscloud.datasource.sonar.entry.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author baiyi
 * @Date 2020/12/24 5:03 下午
 * @Version 1.0
 */
public class BaseSonarElement {

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Paging {
        private Integer pageIndex;
        private Integer pageSize;
        private Integer total;
    }

    /**
     *     {
     *       "organization": "my-org-1",
     *       "id": "project-uuid-1",
     *       "key": "project-key-1",
     *       "name": "Project Name 1",
     *       "qualifier": "TRK",
     *       "visibility": "public",
     *       "lastAnalysisDate": "2017-03-01T11:39:03+0300",
     *       "revision": "cfb82f55c6ef32e61828c4cb3db2da12795fd767"
     *     }
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Project {
        private String organization;
        private String id;
        private String key;
        private String name;
        private String qualifier;
        private String visibility;
       // private Date lastAnalysisDate;
        private String revision;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Component {

        private String id;
        private String key;
        private String name;
        private String qualifier;
        private String description;
        private List<Measure> measures;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Measure extends BasePeriod{

        private String metric;
        private String value;
        private List<Period> periods;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Period extends BasePeriod {

        private Integer index;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BasePeriod {

        private String value;
        private Boolean bestValue;
    }

}
