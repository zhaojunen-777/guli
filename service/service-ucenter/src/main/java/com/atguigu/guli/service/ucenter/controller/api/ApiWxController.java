package com.atguigu.guli.service.ucenter.controller.api;

import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.common.base.util.JwtUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.ucenter.entity.Member;
import com.atguigu.guli.service.ucenter.service.MemberService;
import com.atguigu.guli.service.ucenter.util.HttpClientUtils;
import com.atguigu.guli.service.ucenter.util.UcenterProperties;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin
@Controller//注意这里没有配置 @RestController
@RequestMapping("/api/ucenter/wx")
@Slf4j
public class ApiWxController {

    @Autowired
    private UcenterProperties ucenterProperties;

    @Autowired
    private MemberService memberService;

    @GetMapping("login")
    public String genQrConnect(HttpSession session){

        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        //处理回调url
        String redirecturi = "";
        try {
            redirecturi = URLEncoder.encode(ucenterProperties.getRedirecturi(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.URL_ENCODE_ERROR);
        }

        //处理state：生成随机数，存入session
        String state = UUID.randomUUID().toString();
        System.out.println("生成 state = " + state);
        session.setAttribute("wx-open-state", state);

        String qrcodeUrl = String.format(
                baseUrl,
                ucenterProperties.getAppid(),
                redirecturi,
                state
        );

        return "redirect:" + qrcodeUrl;
    }

    //redirecturl: http://guli.shop/api/ucenter/wx/callback
    @RequestMapping("callback")
    public String callback(String code, String state,HttpSession session) {

        if(StringUtils.isEmpty(code) || StringUtils.isEmpty(state) ){
            log.error("非法回调请求");
            throw new GuliException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        String sessionState = (String)session.getAttribute("wx-open-state");
        if (!state.equals(sessionState)){
            log.error("非法回调请求");
            throw new GuliException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        //携带授权临时票据code，和appid以及appsecret请求access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";
        String accessTokenuUrl = String.format(
                baseAccessTokenUrl,
                ucenterProperties.getAppid(),
                ucenterProperties.getAppsecret(),
                code
        );

        String result = "";
        try {
            result = HttpClientUtils.get(accessTokenuUrl);
            System.out.println("result = " + result);
        } catch (Exception e) {
            log.error("获取access_token失败");
            throw new GuliException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        Gson gson = new Gson();
        HashMap<String,Object> resultMap = gson.fromJson(result, HashMap.class);
        Object errcodeObj = resultMap.get("errcode");
        if(errcodeObj != null){
            String errmsg = (String)resultMap.get("errmsg");
            Double errcode = (Double)errcodeObj;
            log.error("获取access_token失败 - " + "message: " + errmsg + ", errcode: " + errcode);
            throw new GuliException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        String accessToken = (String)resultMap.get("access_token");
        String openid = (String)resultMap.get("openid");

        System.out.println("accessToken = " + accessToken);
        System.out.println("openid = " + openid);

        Member member = memberService.getByOpenid(openid);
        if (member == null){
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(
                    baseUserInfoUrl,
                    accessToken,
                    openid);
            String resultUserInfo = null;
            try {
                resultUserInfo = HttpClientUtils.get(userInfoUrl);
            }catch (Exception e){
                log.error(ExceptionUtils.getMessage(e));
                throw new GuliException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }
            HashMap resultUserInfoMap = gson.fromJson(resultUserInfo, HashMap.class);
            if(resultUserInfoMap.get("errcode") != null){
                log.error("获取用户信息失败" + "，message：" + resultMap.get("errmsg"));
                throw new GuliException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }
            String nickname = (String)resultUserInfoMap.get("nickname");
            String headimgurl = (String)resultUserInfoMap.get("headimgurl");
            Double sex = (Double)resultUserInfoMap.get("sex");

            member = new Member();
            member.setOpenid(openid);
            member.setNickname(nickname);
            member.setAvatar(headimgurl);
            member.setSex(sex.intValue());
            memberService.save(member);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", member.getId());
        claims.put("nickname", member.getNickname());
        claims.put("avatar", member.getAvatar());
        String token = JwtUtils.generateJWT(claims);
        return "redirect:http://localhost:3000?token=" + token;
    }
}
