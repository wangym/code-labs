/**
 * 
 */
package android.wulongdao.etc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * @author yumin
 * 
 */
public class BitmapUtil {

	/**
	 * 
	 */
	private BitmapUtil() {

	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmap(String url) {

		Bitmap bitmap = null;

		if (CommonUtil.isNotEmpty(url)) {
			HttpURLConnection httpConnection = null;
			InputStream inputStream = null;
			try {
				httpConnection = (HttpURLConnection) new URL(url).openConnection();
				httpConnection.connect();
				inputStream = httpConnection.getInputStream();
				bitmap = BitmapFactory.decodeStream(inputStream);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != inputStream) {
						inputStream.close();
						inputStream = null;
					}
					if (null != httpConnection) {
						httpConnection.disconnect();
						httpConnection = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return bitmap;
	}

	/**
	 * 
	 * @param inputPath
	 * @param outputPath
	 * @return
	 */
	public static Bitmap resizeBitmap(String inputPath, String outputPath) {

		Bitmap resizeBitmap = null;

		if (CommonUtil.isNotEmpty(inputPath) && CommonUtil.isNotEmpty(outputPath)) {
			Bitmap sourceBitmap = null;
			FileOutputStream outputStream = null;
			Matrix matrix = null;
			try {
				sourceBitmap = BitmapFactory.decodeFile(inputPath);
				if (null != sourceBitmap) {
					outputStream = new FileOutputStream(new File(outputPath));
					matrix = new Matrix();
					matrix.postScale(GlobalConfig.IMAGE_SCALE_SIZE, GlobalConfig.IMAGE_SCALE_SIZE);
					resizeBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, false);
					resizeBitmap.compress(Bitmap.CompressFormat.JPEG, GlobalConfig.IMAGE_SCALE_QUALITY, outputStream);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != outputStream) {
						outputStream.flush();
						outputStream.close();
					}
					if (null != sourceBitmap && !sourceBitmap.isRecycled()) {
						// sourceBitmap.recycle();
					}
					if (null != resizeBitmap && !resizeBitmap.isRecycled()) {
						// resizeBitmap.recycle();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return resizeBitmap;
	}

}
