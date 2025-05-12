package com.czy.api.domain.ao.feature;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/5/12 10:45
 */
@Data
public class ScoreAo {
    private int clickTimes = 0;
    private double implicitScore = 0.0;
    private double explicitScore = 0.0;

    public void add(@NotNull ScoreAo scoreAo){
        this.clickTimes += scoreAo.getClickTimes();
        this.implicitScore += scoreAo.getImplicitScore();
        this.explicitScore += scoreAo.getExplicitScore();
    }

    public boolean isEmpty(){
        return clickTimes == 0 && implicitScore == 0.0 && explicitScore == 0.0;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // 浅拷贝足够，因为没有引用类型字段
        return super.clone();
    }
}
