package pl.kacpik.barber.utils;

import com.google.zxing.WriterException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class QRCodeGeneratorTest {

    @Autowired
    private QRCodeGenerator qrCodeGenerator;

    @Test
    public void shouldGenerateQRCode() {
        byte[] data = qrCodeGenerator.createQRCode("https://www.google.com/test");

        assertNotNull(data);
        assertTrue(data.length > 0);
    }
}