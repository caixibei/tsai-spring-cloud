package tsai.spring.cloud.controller;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.annotations.Beta;
import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.util.BranchUtil;
import com.tsaiframework.boot.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tsai.spring.cloud.constant.RedisConstant;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import static cn.hutool.core.img.ImgUtil.toBufferedImage;
/**
 * 验证码处理器
 *
 * @author tsai
 */
@Slf4j
@RestController
@RequestMapping("/captcha")
@SuppressWarnings({WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION, WarningsConstants.DUPLICATES})
public class CaptchaController {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取扭曲干扰的验证码
     *
     * @param response 响应报文
     */
    @Beta
    @GetMapping("/shear")
    public void getShearCaptcha(HttpServletResponse response) {
        //定义图形验证码的长、宽、验证码字符数、干扰线宽度
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(150, 50, 4, 3);
        //设置背景颜色
        shearCaptcha.setBackground(new Color(249, 251, 220));
        //生成四位验证码
        String code = RandomUtil.randomNumbers(4);
        //生成验证码图片
        Image image = shearCaptcha.createImage(code);
        //返回验证码信息
        responseCode(response, code, image);
    }

    /**
     * 获取线条干扰的验证码
     *
     * @param response 响应报文
     */
    @GetMapping("/line")
    public void getLineCaptcha(HttpServletRequest request, HttpServletResponse response) {
        // 定义图形验证码的长、宽、验证码位数、干扰线数量
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(150, 50, 4, 20);
        // 设置背景颜色
        lineCaptcha.setBackground(new Color(255, 255, 255));
        // 获取输入的账号，作为存储验证码的键（Redis）
        String username = request.getParameter("username");
        // 如果传入空的用户名直接返回错误信息
        BranchUtil.branchHandler(StrUtil.isBlank(username), () -> {
            response.reset();
            response.setContentType("application/json;charset=utf-8");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
            response.setHeader("Access-Control-Allow-Origin", "*");
            JSONObject object = new JSONObject();
            object.putOnce("code", HttpStatus.HTTP_BAD_REQUEST);
            object.putOnce("success", false);
            object.putOnce("timestamp", System.currentTimeMillis());
            object.putOnce("message", "请输入用户名！");
            try {
                response.getWriter().write(JSONUtil.toJsonStr(object));
                response.flushBuffer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> {
            // 生成四位验证码
            String code = RandomUtil.randomString("abcdefghijkmnpqrstuvwxyz234567890ABCDEFGHJKLMNPQRSTUVWXYZ", 5);
            Image image = lineCaptcha.createImage(code);
            String uuid = IdUtil.fastSimpleUUID();
            // 存储在 Redis 中，同时设置有效时长为3分钟
            String key = StrUtil.concat(false, RedisConstant.CAPTCHA_KEY_PREFIX, uuid);
            redisUtil.setEx(key, code, 3, TimeUnit.MINUTES);
            log.info("生成验证码======> uuid:{} \t code: {}", uuid, code);
            // 设置验证码图片的key，同时以图片形式返回验证码信息
            request.getSession().setAttribute("uuid", uuid);
            responseCode(response, code, image);
        });
    }

    /**
     * 获取圆圈干扰的验证码
     *
     * @param response 响应报文
     */
    @Beta
    @GetMapping("/circle")
    public void getCircleCaptcha(HttpServletResponse response) {
        // 定义图形验证码的长、宽、验证码位数、干扰圈圈数量
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(150, 50, 4, 30);
        // 设置背景颜色
        circleCaptcha.setBackground(new Color(249, 251, 220));
        // 生成四位验证码
        String code = RandomUtil.randomNumbers(4);
        Image image = circleCaptcha.createImage(code);
        // 返回验证码信息
        responseCode(response, code, image);
    }

    @SuppressWarnings(WarningsConstants.DEPRECATION)
    protected static void responseCode(HttpServletResponse response, String code, Image image) {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        try {
            BufferedImage bufferedImage = toBufferedImage(image);
            // 创建 ByteArrayOutputStream 用于存储图片数据
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // 写入图片数据到 ByteArrayOutputStream
            ImageIO.write(bufferedImage, "jpeg", outputStream);
            // 将 ByteArrayOutputStream 转换为 ByteArrayInputStream
            byte[] imageInBytes = outputStream.toByteArray();
            IoUtil.write(response.getOutputStream(), true, imageInBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
