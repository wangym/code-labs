/**
 * 
 */
package me.yumin.java.labs.test.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author yumin
 */
public class ZXingTest {

	@Test
	public void testQREncode() {

		boolean result = false;

		try {
			String content = "你好, 中国!";
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q); // 容错率
			hints.put(EncodeHintType.MARGIN, 1); // 边缘
			hints.put(EncodeHintType.CHARACTER_SET, "GBK"); // 字符集
			BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);
			MatrixToImageWriter.writeToFile(matrix, "png", new File("qr_code.png"));
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		Assert.assertTrue(result);
	}

	@Test
	public void testQRDecode() {

		Result result = null;

		try {
			BufferedImage bufferedImage = ImageIO.read(new File("qr_code.png"));
			LuminanceSource luminanceSource = new BufferedImageLuminanceSource(bufferedImage);
			BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));
			result = new QRCodeReader().decode(binaryBitmap);
			if (null != result) {
				System.out.println(result);
				System.out.println(result.getResultMetadata());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Assert.assertTrue(null != result);
	}
}
