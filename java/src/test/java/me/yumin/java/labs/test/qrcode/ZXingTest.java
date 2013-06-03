/**
 * 
 */
package me.yumin.java.labs.test.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author yumin
 */
public class ZXingTest {

	@Test
	public void test自增陷阱() {

		boolean result = false;

		try {
			String content = "http://item.taobao.com/item.htm?id=12345&et_token=b794bb63cce11a79dd63153419271c4a&et_ver=1.0";
			// 字符串1
			QRCode qrCode = Encoder.encode(content, ErrorCorrectionLevel.H);
			System.out.println(qrCode.toString());
			// 图片版1
			BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 120, 120);
			MatrixToImageWriter.writeToFile(matrix, "png", new File("qr_code_MultiFormatWriter.png"));
			// 图片版2
			QRCodeWriter writer = new QRCodeWriter();
			matrix = writer.encode(content, BarcodeFormat.QR_CODE, 230, 230);
			MatrixToImageWriter.writeToFile(matrix, "png", new File("qr_code_QRCodeWriter.png"));
			// 最终结果
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		Assert.assertTrue(result);
	}
}
