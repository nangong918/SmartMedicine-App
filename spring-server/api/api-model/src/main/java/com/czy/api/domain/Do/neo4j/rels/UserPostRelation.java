package com.czy.api.domain.Do.neo4j.rels;

import com.czy.api.domain.Do.neo4j.PostNeo4jDo;
import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
import com.czy.api.mapper.UserFeatureRepository;
import lombok.Data;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;


/**
 * @author 13225
 * @date 2025/5/10 10:57
 */
@Data
@RelationshipEntity(UserFeatureRepository.RELS_USER_POSTS)
public class UserPostRelation {

    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private UserFeatureNeo4jDo user;

    @EndNode
    private PostNeo4jDo post;

    @Property("clickTimes")
    private Integer clickTimes = 0;

    // 添加评分字段，初始值为0.0
    @Property("implicitScore")
    private Double implicitScore = 0.0;

    @Property("explicitScore")
    private Double explicitScore = 0.0;

    @Property("lastUpdateTimestamp")
    private Long lastUpdateTimestamp = System.currentTimeMillis();

    // 权重加1的方法
    public void incrementClickTimes() {
        this.clickTimes += 1;
        this.lastUpdateTimestamp = System.currentTimeMillis();
    }

    // 设置评分的方法
    public void setImplicitScore(Double implicitScore) {
        if (implicitScore < -10.0) {
            this.implicitScore = -10.0;
        }
        else if (implicitScore > 10.0) {
            this.implicitScore = 10.0;
        }
        else {
            this.implicitScore = implicitScore;
        }
        this.lastUpdateTimestamp = System.currentTimeMillis();
    }
    public void setExplicitScore(Double explicitScore) {
        if (explicitScore < -10.0) {
            this.explicitScore = -10.0;
        }
        else if (explicitScore > 10.0) {
            this.explicitScore = 10.0;
        }
        else {
            this.explicitScore = explicitScore;
        }
        this.lastUpdateTimestamp = System.currentTimeMillis();
    }
}
