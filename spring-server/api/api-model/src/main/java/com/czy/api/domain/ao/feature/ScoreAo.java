package com.czy.api.domain.ao.feature;

import json.BaseBean;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/5/12 10:45
 */
@Data
public class ScoreAo implements BaseBean {
    private int clickTimes = 0;
    private double implicitScore = 0.0;
    private double explicitScore = 0.0;

    /**
     * 累加
     * @param scoreAo   需要累
     */
    public void add(@NotNull ScoreAo scoreAo){
        this.clickTimes += scoreAo.getClickTimes();
        this.implicitScore += scoreAo.getImplicitScore();
        this.explicitScore += scoreAo.getExplicitScore();
    }

    /**
     * 乘
     * @param weight    需要乘
     */
    public void product(float weight){
        this.clickTimes = (int) (this.clickTimes * weight);
        this.implicitScore *= weight;
        this.explicitScore *= weight;
    }

    /**
     * 乘
     * @param weight    需要乘
     */
    public ScoreAo productBack(float weight){
        ScoreAo scoreAo = new ScoreAo();
        scoreAo.setClickTimes(this.clickTimes);
        scoreAo.setImplicitScore(this.implicitScore);
        scoreAo.setExplicitScore(this.explicitScore);
        scoreAo.clickTimes = (int) (scoreAo.clickTimes * weight);
        scoreAo.implicitScore *= weight;
        scoreAo.explicitScore *= weight;
        return scoreAo;
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
