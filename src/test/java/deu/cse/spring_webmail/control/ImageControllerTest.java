package deu.cse.spring_webmail.control;

import deu.cse.spring_webmail.service.AddressBookService;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ImageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServletContext servletContext;

    @MockBean
    private AddressBookService addressBookService;

    @Test
    @DisplayName("이미지 정상 로드 테스트")
    void testGetImageSuccess() throws Exception {
        // given
        String imageName = "sample.png";
        Path tempDir = Files.createTempDirectory("test-images");
        Path imagePath = tempDir.resolve(imageName);

        // 1. BufferedImage로 진짜 이미지 생성
        BufferedImage bufferedImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.fillRect(0, 0, 10, 10);
        g2d.dispose();
        ImageIO.write(bufferedImage, "png", imagePath.toFile());

        // 2. mock ServletContext의 경로 반환 설정
        when(servletContext.getRealPath("/WEB-INF/views/img_test/img"))
                .thenReturn(tempDir.toAbsolutePath().toString());

        // when & then
        mockMvc.perform(get("/get_image/{imageName}", imageName))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"))
                .andExpect(content().bytes(Files.readAllBytes(imagePath)));

        // 3. 정리
        Files.deleteIfExists(imagePath);
        Files.deleteIfExists(tempDir);
    }


    @Test
    @DisplayName("존재하지 않는 이미지 요청 시 빈 배열 반환")
    void testGetImageNotFound() throws Exception {
        // given
        String imageName = "nonexistent.jpg";
        when(servletContext.getRealPath("/WEB-INF/views/img_test/img"))
                .thenReturn(new File(".").getAbsolutePath());

        // when & then
        mockMvc.perform(get("/get_image/{imageName}", imageName))
                .andExpect(status().isOk())
                .andExpect(content().bytes(new byte[0]));
    }

    @Test
    @DisplayName("/img_test 요청 시 뷰 반환 확인")
    void testImgTestPage() throws Exception {
        mockMvc.perform(get("/img_test"))
                .andExpect(status().isOk())
                .andExpect(view().name("img_test/img_test"));
    }
}
