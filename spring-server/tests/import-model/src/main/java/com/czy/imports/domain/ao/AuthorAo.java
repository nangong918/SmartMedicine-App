package com.czy.imports.domain.ao;

import com.czy.imports.domain.Do.ArticleDo;
import json.BaseBean;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/30 16:00
 */
@Data
public class AuthorAo implements BaseBean {
    private Long id;
    private String userAccount;
    private AuthorInfoAo authorInfoAo;
    private List<ArticleDo> articleDos = new ArrayList<>();
}
