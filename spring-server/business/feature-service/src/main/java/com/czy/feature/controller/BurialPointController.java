package com.czy.feature.controller;

import com.czy.api.constant.feature.FeatureConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 13225
 * @date 2025/5/9 10:25
 * 埋点数据
 * 获取用户/帖子的隐形特征
 * 对于用户显性特征，消息将通过mq发送到此服务
 * 对于隐形特征，则需要App进行埋点操作，此controller就是收集隐性操作的Controller
 * 用户显性数据包括：
 *      1.用户的搜索
 *      2.操作数据
 *          点赞，
 *          评论（BERT情感分类NLE：肯定态度，否定态度，中立态度），
 *          收藏；
 *          转发
 * 物品显性数据包括：
 *      1.物品的被搜索：各级匹配加不同的热度值
 *      2.物品的点赞、评论、收藏、转发：不同的操作加上不同的热度值
 *          （
 *              热度值要根据发布时间衰减：
 *              采用现场计算而不是大数据定时任务：大量的对天文数字级别的用户做离线定时任务是不明智的
 *              比如每个埋点数据记录特征和时间，获取用户特征的时候先筛选最近30天的数据，超过30天就直接不获取，并且全部埋点数据按照权重衰减
 *           ）
 *      3.物品的文本特征
 * 用户隐性数据包括：
 *      1.用户的点击召回
 *      2.用户的浏览时长（1.根据文章长度估算大概要读取的时间 - 用户已读取的时间 2.固定判断时长：超过30秒一定增加权重）
 *      3.用户的手机机型；用户的ip地址，用户的经纬度信息（如果能够获取：提供说明文档，让用户同意）
 * 物品隐形数据包括：
 *      1.曝光率
 *      2.点击率
 *      3.物品的被浏览时长
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RestController
@Validated // 启用校验
@RequiredArgsConstructor // 自动注入@Autowired
@RequestMapping(FeatureConstant.BurialPoint_CONTROLLER)
public class BurialPointController {



}
