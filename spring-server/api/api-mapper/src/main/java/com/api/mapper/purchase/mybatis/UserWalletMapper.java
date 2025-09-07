package com.api.mapper.purchase.mybatis;

import com.czy.api.domain.Do.purchase.UserWalletDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @author 13225
 * @date 2025/8/27 17:37
 */
@Mapper
public interface UserWalletMapper {

    ///  增
    int insert(UserWalletDo record);

    /// 删
    int deleteById(Long id);
    int deleteByUserId(Long userId);

    /// 改
    // 充值
    int userRecharge(
            @Param("userId") Long userId,
            @Param("rechargeAmount") BigDecimal rechargeAmount
    );

    // 直接扣款
    int deduct(
            @Param("userId") Long userId,
            @Param("payAmount") BigDecimal payAmount
    );

    // 支付扣款
//    int checkAndLockToPay(
//            @Param("userId") Long userId,
//            @Param("orderId") Long orderId,
//            @Param("payAmount") BigDecimal payAmount
//    );

    /// 查
    UserWalletDo getUserWalletByUserId(Long userId);
    UserWalletDo getUserWalletAndLockByUserId(Long userId);
}
