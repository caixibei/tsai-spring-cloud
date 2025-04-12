package tsai.spring.cloud.pojo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tsaiframework.boot.base.pojo.TsaiPOJO;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 * 
 * @author tsai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tsai_user")
public class User extends TsaiPOJO implements Serializable {

    /**
     * 用户名
     */
    @TableField(value = "username")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;

}
