package com.czy.api.domain.Do.neo4j;


import json.BaseBean;
import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/25 16:44
 */
@Data
@NodeEntity("疾病")
public class DiseaseDo implements BaseBean {
    @Id
    @Field("id")
    private Long id;
    @Field("name")
    private String name; // 疾病名称
    @Field("desc")
    private String desc; // 疾病描述
    @Field("prevent")
    private String prevent; // 预防措施
    @Field("cause")
    private String cause; // 病因
    @Field("easy_get")
    private String easyGet; // 易感染人群
    @Field("cure_department")
    private List<String> cureDepartment; // 治疗科室
    @Field("cure_way")
    private List<String> cureWay; // 治疗方式
    @Field("cure_lasttime")
    private String cureLastTime; // 治疗时长
    @Field("symptom")
    private String symptom; // 症状




    @Field("get_prob")
    private String getProb; // 感染概率
    @Field("cured_prob")
    private String curedProb; // 治愈概率
}
