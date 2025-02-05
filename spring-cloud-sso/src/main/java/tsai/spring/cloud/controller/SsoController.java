package tsai.spring.cloud.controller;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static cn.hutool.core.img.ImgUtil.toBufferedImage;
@RestController
@RequestMapping("/sso")
public class SsoController {

    /**
     * 登录成功跳转
     * @return 跳转地址
     */
    @PostMapping("/dashboard")
    public String dashboard() {
        return "redirect:/dashboard.html";
    }

    /**
     * 登录失败跳转
     * @return 跳转地址
     */
    @PostMapping("/error")
    public String error() {
        return "redirect:/error.html";
    }

    /**
     * 获取扭曲干扰的验证码
     * @param response 响应报文
     */
    @GetMapping("/shearCaptcha")
    public void getShearCaptcha(HttpServletResponse response) {
        //定义图形验证码的长、宽、验证码字符数、干扰线宽度
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(150, 50,4,3);
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
     * @param response 响应报文
     */
    @GetMapping("/lineCaptcha")
    public void getLineCaptcha(HttpServletResponse response) {
        //定义图形验证码的长、宽、验证码位数、干扰线数量
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(150, 50,4,80);
        //设置背景颜色
        lineCaptcha.setBackground(new Color(249, 251, 220));
        //生成四位验证码
        String code = RandomUtil.randomNumbers(4);
        Image image = lineCaptcha.createImage(code);
        //返回验证码信息
        responseCode(response, code, image);
    }

    /**
     * 获取圆圈干扰的验证码
     * @param response 响应报文
     */
    @GetMapping("/circleCaptcha")
    public void getCircleCaptcha(HttpServletResponse response) {
        // 定义图形验证码的长、宽、验证码位数、干扰圈圈数量
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(150, 50,4,30);
        // 设置背景颜色
        circleCaptcha.setBackground(new Color(249, 251, 220));
        // 生成四位验证码
        String code = RandomUtil.randomNumbers(4);
        Image image = circleCaptcha.createImage(code);
        // 返回验证码信息
        responseCode(response, code, image);
    }

    protected static void responseCode(HttpServletResponse response, String code, Image image) {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        try {
            BufferedImage bufferedImage = toBufferedImage(image);
            // 创建 ByteArrayOutputStream 用于存储图片数据
            ByteArrayOutputStream outputStrem = new ByteArrayOutputStream();
            // 写入图片数据到 ByteArrayOutputStream
            ImageIO.write(bufferedImage, "jpeg", outputStrem);
            // 将 ByteArrayOutputStream 转换为 ByteArrayInputStream
            byte[] imageInBytes = outputStrem.toByteArray();
            IoUtil.write(response.getOutputStream(), true, imageInBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
