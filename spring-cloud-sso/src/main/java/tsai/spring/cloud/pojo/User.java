package tsai.spring.cloud.pojo;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tsaiframework.boot.base.pojo.TsaiPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tsai_user")
public class User extends TsaiPOJO implements Serializable {
    @TableField(value = "USERNAME")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;

    @TableField(value = "PASSWORD")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;
}
