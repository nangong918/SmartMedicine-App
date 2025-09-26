package com.ai.medicine.domain.Do.neo4j;


import com.ai.medicine.domain.Do.neo4j.base.BaseNeo4jDo;
import com.ai.medicine.domain.constant.MedicalEntityEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/25 16:44
 */
@EqualsAndHashCode(callSuper = true)

@Node("疾病")
@Data
public class DiseaseDo extends BaseNeo4jDo {


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
        return MedicalEntityEnum.DISEASE.getDescription();
    }

    public String toDocumentString() {
        return "疾病：" + super.name + "\n" +
                "描述：" + this.desc + "\n" +
                "病因：" + this.cause + "\n" +
                "预防措施：" + this.prevent + "\n" +
                "症状：" + this.symptom + "\n" +
                "治疗方式：" + this.cureWay + "\n" +
                "治疗时长：" + this.cureLastTime + "\n" +
                "治疗科室：" + this.cureDepartment + "\n" +
                "感染概率：" + this.getProb + "\n" +
                "治愈概率：" + this.curedProb;
    }
}
