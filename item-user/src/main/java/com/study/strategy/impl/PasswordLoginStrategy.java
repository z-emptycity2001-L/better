package com.study.strategy.impl;

import com.study.Constants.jwt.JwtConstants;
import com.study.Constants.user.ThreadLocalConstants;
import com.study.entity.dto.ResponseDto;
import com.study.entity.dto.user.LoginDTO;
import com.study.entity.dto.user.RegisterDTO;
import com.study.entity.po.User;
import com.study.exception.BusinessException;
import com.study.Eume.ExceptEnum;
import com.study.mapper.UserMapper;
import com.study.strategy.LoginStrategy;
import com.study.util.UserRedisUtil;
import com.study.utils.DateUtil;
import com.study.utils.JwtTokenUtil;
import com.study.utils.ThreadLocalUtil;
import com.study.utils.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class PasswordLoginStrategy implements LoginStrategy {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ThreadLocalUtil threadLocalUtil;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisUtil redisUtil;

    public ResponseDto<String> login(LoginDTO loginDTO) {
        String password = loginDTO.getPassword();
        String username = loginDTO.getUsername();
        User user = userMapper.selectUserByUsername(username);
        if(Objects.isNull(user)){
            throw BusinessException.of(ExceptEnum.LOGIN_NO_USER_RECORD);
        }
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(password.getBytes());
        if(!md5DigestAsHex.equals(user.getPassword())){
            throw BusinessException.of(ExceptEnum.LOGIN_PASSWORD_ERROR);
        }
        loginDTO.setPassword(md5DigestAsHex);
        threadLocalUtil.setThreadLocal(ThreadLocalConstants.USERINFO,loginDTO);
        String token = jwtTokenUtil.generateToken(user.getId(), JwtConstants.TOKEN_EXPIRATION);//创建一个三天有效期的token
        redisUtil.set(JwtConstants.TOKEN_KEY+user.getUsername(),token,JwtConstants.TOKEN_REDIS_EXPIRATION, TimeUnit.MILLISECONDS);//redis中存放token，有效期为3小时
        return ResponseDto.success(token);
    }

    public ResponseDto<RegisterDTO> register(RegisterDTO registerDTO) {
        String password = registerDTO.getPassword();
        String username = registerDTO.getUsername();
        String validPassword = registerDTO.getValidPassword();
        if(!Objects.equals(password,validPassword)){
            throw BusinessException.of(ExceptEnum.REGISTER_PASSWORD_TWICE_DIFFERENT);
        }
        User user = userMapper.selectUserByUsername(username);
        if(!Objects.isNull(user)){
            throw BusinessException.of(ExceptEnum.REGISTER_USER_EXISTED);
        }
        String currentTime = DateUtil.getCurrentTime("yyyy-MM-dd HH:mm-ss");
        User responseUser = User.builder().
                username(username).
                password(DigestUtils.md5DigestAsHex(password.getBytes())).
                createTime(currentTime).
                updateTime(currentTime).
                build();

        Integer affect = userMapper.insertUser(responseUser);
        if (Objects.isNull(affect)||affect<1){
            throw BusinessException.of(ExceptEnum.COMMON_ERROR_RESPONSE);
        }
        return ResponseDto.success("注册成功");
    }

    @Override
    public ResponseDto<String> logout(LoginDTO loginDTO) {
        int times=3;
        try{
            /* 如果lua脚本执行失败那么就重试 @Param{times} 次 */
            while(
                    UserRedisUtil.getLogout(JwtConstants.TOKEN_KEY + loginDTO.getUsername(),
                    JwtConstants.BLACK_LIST_TOKEN_PRE_KEY + loginDTO.getUsername(),
                    redisUtil.get(JwtConstants.TOKEN_KEY + loginDTO.getUsername()).toString(),
                    JwtConstants.TOKEN_EXPIRATION)!=1
                    &&times>0){
                times--;
            }
            return ResponseDto.success("success");
        }catch (Exception e){
            throw BusinessException.of(ExceptEnum.COMMON_ERROR_RESPONSE);
        }
    }
}
