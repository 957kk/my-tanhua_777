package com.tanhua.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.common.pojo.VerifyCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FourUserMapper extends BaseMapper<VerifyCode> {
}
