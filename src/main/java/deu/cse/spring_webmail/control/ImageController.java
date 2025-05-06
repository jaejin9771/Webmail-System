package deu.cse.spring_webmail.control;

import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;


/**
 *
 * @author jaejin
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final ServletContext ctx;

    @GetMapping("/img_test")
    public String imgTest() {
        return "img_test/img_test";
    }

    @GetMapping("/get_image/{imageName}")
    @ResponseBody
    public byte[] getImage(@PathVariable String imageName) {
        try {
            String path = ctx.getRealPath("/WEB-INF/views/img_test/img");
            return getImageBytes(path, imageName);
        } catch (Exception e) {
            log.error("이미지 로드 실패", e);
            return new byte[0];
        }
    }

    private byte[] getImageBytes(String folderPath, String imageName) throws Exception {
        File imageFile = new File(folderPath + File.separator + imageName);
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String format = imageName.substring(imageName.lastIndexOf(".") + 1);
        ImageIO.write(bufferedImage, format, out);
        return out.toByteArray();
    }
}
