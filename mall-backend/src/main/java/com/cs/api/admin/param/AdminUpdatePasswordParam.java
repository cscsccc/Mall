package com.cs.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class AdminUpdatePasswordParam implements Serializable {
    @ApiModelProperty(value = "原始密码")
    @NotEmpty(message = "旧密码不应为空呢")
    private String originalPassword;

    @ApiModelProperty(value = "新密码")
    @NotEmpty(message = "新密码不应为空呢")
    private String newPassword;
}
