package com.yilu.yinling.user.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yilu.yinling.user.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT r.role_code FROM sys_role r JOIN sys_user_role ur ON ur.role_id=r.id WHERE ur.user_id=#{userId}")
    List<String> selectRoleCodes(@Param("userId") Long userId);
}
