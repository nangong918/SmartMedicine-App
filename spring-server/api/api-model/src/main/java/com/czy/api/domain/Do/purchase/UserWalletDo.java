package com.czy.api.domain.Do.purchase;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

/**
 * @author 13225
 * @date 2025/8/27 17:35
 */
@Data
public class UserWalletDo {
    @Id
    private Long id;
    private Long userId;
    private BigDecimal balance;
}
