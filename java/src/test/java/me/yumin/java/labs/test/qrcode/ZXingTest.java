/**
 * 
 */
package me.yumin.java.labs.test.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author yumin
 */
public class ZXingTest {

	@Test
	public void testZXingQRCode() {

		boolean result = false;

		try {
			// 原始内容
			String content = "你好, 中国!";
			// 生成图片
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q); // 容错率
			hints.put(EncodeHintType.MARGIN, 1); // 边缘
			hints.put(EncodeHintType.CHARACTER_SET, "GBK"); // 字符集
			BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);
			MatrixToImageWriter.writeToFile(matrix, "png", new File("qr_code.png"));
			// 最终结果
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		Assert.assertTrue(result);
	}
}
