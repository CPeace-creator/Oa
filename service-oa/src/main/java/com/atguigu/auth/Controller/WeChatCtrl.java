package com.atguigu.auth.Controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.response.Result;
import com.atguigu.common.response.Utils.JwtUtils;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.wechat.BindPhoneVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author cjh 微信公众号(授权登录)
 * @date 2023/11/12
 */
@Controller
@RequestMapping("/admin/wechat")
@Slf4j
public class WeChatCtrl {
    @Resource
    private SysUserService sysUserService;

    @Autowired
    private WxMpService wxMpService;

    @Value("${wechat.userInfoUrl}")
    private String userInfoUrl;

    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl, HttpServletRequest request) {
        //由于授权回调成功后，要返回原地址路径，原地址路径带“#”号，当前returnUrl获取带“#”的url获取不全，因此前端把“#”号替换为“guiguoa”了，这里要还原一下
        Object WxConsts;
//        String redirectURL = wxMpService.getOAuth2Service().buildAuthorizationUrl(userInfoUrl, WxConsts.OAuth2Scope.SNSAPI_USERINFO, URLEncoder.encode(returnUrl.replace("guiguoa", "#")));
//        log.info("【微信网页授权】获取code,redirectURL={}", redirectURL);
//        return "redirect:" + redirectURL;
        return null;
    }

    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) throws Exception {
        log.info("【微信网页授权】code={}", code);
        log.info("【微信网页授权】state={}", returnUrl);
//        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
//        String openId = accessToken.getOpenId();
//        log.info("【微信网页授权】openId={}", openId);

//        WxOAuth2UserInfo wxMpUser = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
//        log.info("【微信网页授权】wxMpUser={}", JSON.toJSONString(wxMpUser));

//        SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getOpenId, openId));
        String token = "";
        //null != sysUser 说明已经绑定，反之为建立账号绑定，去页面建立账号绑定
//        if(null != sysUser) {
//            token = JwtUtils.createToken(sysUser.getId(), sysUser.getUsername());
//        }
//        if(returnUrl.indexOf("?") == -1) {
//            return "redirect:" + returnUrl + "?token=" + token + "&openId=" + openId;
//        } else {
//            return "redirect:" + returnUrl + "&token=" + token + "&openId=" + openId;
//        }
        return null;
    }

    @ApiOperation(value = "微信账号绑定手机")
    @PostMapping("bindPhone")
    @ResponseBody
    public Result bindPhone(@RequestBody BindPhoneVo bindPhoneVo) {
        SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, bindPhoneVo.getPhone()));
        if(null != sysUser) {
            sysUser.setOpenId(bindPhoneVo.getOpenId());
            sysUserService.updateById(sysUser);

            String token = JwtUtils.createToken(sysUser.getId(), sysUser.getUsername());
            return Result.ok(token);
        } else {
            return Result.fail("手机号码不存在，绑定失败");
        }
    }
}
