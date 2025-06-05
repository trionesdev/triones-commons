package com.trionesdev.commons.mybatisplus.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 操作实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseOperatePO extends BaseLogicPO {
    @TableField(fill = FieldFill.INSERT)
    private String createdRole;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedRole;
}
