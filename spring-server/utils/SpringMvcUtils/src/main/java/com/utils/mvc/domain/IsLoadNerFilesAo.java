package com.utils.mvc.domain;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/5/5 15:58
 */
@Data
public class IsLoadNerFilesAo {
    private Boolean isLoadNerFiles = false;
    public IsLoadNerFilesAo(){

    }
    public IsLoadNerFilesAo(Boolean isLoadNerFiles){
        this.isLoadNerFiles = isLoadNerFiles;
    }
}
