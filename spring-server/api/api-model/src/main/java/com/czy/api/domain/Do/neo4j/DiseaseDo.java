package com.czy.api.domain.Do.neo4j;


import com.czy.api.domain.Do.neo4j.base.BaseNeo4jDo;
import json.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/25 16:44
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("疾病")
public class DiseaseDo extends BaseNeo4jDo implements BaseBean {
    // nodeLabel
    public static final String nodeLabel = "疾病";
    @Property("desc")
    private String desc; // 疾病描述
    @Property("prevent")
    private String prevent; // 预防措施
    @Property("cause")
    private String cause; // 病因
    @Property("easy_get")
    private String easyGet; // 易感染人群
    @Property("cure_department")
    private List<String> cureDepartment; // 治疗科室
    @Property("cure_way")
    private List<String> cureWay; // 治疗方式
    @Property("cure_lasttime")
    private String cureLastTime; // 治疗时长
    @Property("symptom")
    private String symptom; // 症状




    @Property("get_prob")
    private String getProb; // 感染概率
    @Property("cured_prob")
    private String curedProb; // 治愈概率

    @Override
    public String getNodeLabel() {
        return nodeLabel;
    }
}
