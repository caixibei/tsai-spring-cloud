package tsai.spring.cloud.pojo;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tsaiframework.boot.base.pojo.TsaiPOJO;
import lombok.Data;
import java.io.Serializable;
import java.sql.Timestamp;
@Data
@TableName(value = "TSAI_USER")
public class User extends TsaiPOJO implements Serializable {
    /**
     * 主键序列
     */
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    @TableField(value = "USERNAME")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;

    @TableField(value = "PASSWORD")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT_UPDATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Timestamp createTime;

    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Timestamp updateTime;
}
