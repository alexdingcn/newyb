package com.yiban.erp.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * 允许上传的文件类型(定义允许上传的文件后缀名)
 */
public enum FileUploadType {

    JPG,
    JPEG,
    PNG,
    GIF,
    TIFF,
    PDF,
    DOC,
    DOCX,
    XLSX,
    XLS,
    CSV,
    TXT,
    ZIP,
    RAR,
    TAR;

    public static boolean validate(String originalName) {
        if (StringUtils.isBlank(originalName)) {
            return false;
        }
        String endName = originalName.substring(originalName.lastIndexOf('.') + 1);
        for (FileUploadType type : FileUploadType.values()) {
            if (type.name().equalsIgnoreCase(endName)) {
                return true;
            }
        }
        return false;
    }

}
