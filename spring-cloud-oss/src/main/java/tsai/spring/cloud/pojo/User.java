package tsai.spring.cloud.pojo;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
@TableName(value = "TSAI_USER")
public class User extends org.springframework.security.core.userdetails.User implements UserDetails, Serializable {

    public User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public User(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
                boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    /**主键序列*/
    @TableId(type = IdType.ASSIGN_UUID)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    @TableField(value = "USERNAME")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;

    @TableField(value = "PASSWORD")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;

    /**创建时间*/
    @TableField(value = "CREATE_TIME",fill = FieldFill.INSERT_UPDATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Timestamp createTime;

    /**修改时间*/
    @TableField(value = "UPDATE_TIME",fill = FieldFill.UPDATE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Timestamp updateTime;

    /**当前页*/
    @TableField(exist = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer start;

    /**页大小*/
    @TableField(exist = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer limit;

    /**总页数*/
    @TableField(exist = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer total;


}
