/**
 * 
 */
package cn.androidcloud.tao.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * @author WANG Yumin
 * 
 */
public class CommonUtil {

	/**
	 * 
	 */
	private static final int IO_BUFFER_SIZE = 512;

	/**
	 * 
	 */
	public static boolean isNotEmpty(String str) {

		if (null != str && 0 < str.length() && !"null".equalsIgnoreCase(str)) {

			return true;
		}

		return false;
	}

	/**
	 * 
	 */
	public static String dateFormat(Date date, String format) {

		if (null == date || !isNotEmpty(format)) {

			return "";
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

		return simpleDateFormat.format(date);
	}

	/**
	 * 字符串转换成整型
	 */
	public static int toInt(String str) {

		if (isNotEmpty(str)) {

			try {
				return Integer.parseInt(str.trim());
			} catch (Exception e) {
				return 0;
			}
		}

		return 0;
	}

	/**
	 * 获取文本Properties
	 */
	public Properties getPropertiesPlain(String fileName) {

		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream(fileName);
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return properties;
	}

	/**
	 * 获取XML的Properties
	 */
	public static Properties getPropertiesXml(String fileName) {

		Properties properties = new Properties();
		try {
			properties.loadFromXML(Thread.currentThread()
					.getContextClassLoader().getResourceAsStream(fileName));
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return properties;
	}

	/**
	 * 
	 */
	public static InputStream getInputStream(String crawlUrl)
			throws IOException {

		System.out.println("crawlUrl:" + crawlUrl);
		if (!isNotEmpty(crawlUrl)) {
			return null;
		}

		URL url = new URL(crawlUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.setConnectTimeout(1000);
		conn.setRequestMethod("GET");
		conn.connect();

		for (int i = 0; i < 1; i++) {

			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {

				InputStream inputStream = conn.getInputStream();
				if (null != conn) {

					conn.disconnect();
					conn = null;
				}

				return inputStream;
			}
		}

		return null;
	}

	/**
	 * 
	 */
	public static byte[] fetchImage(String address)
			throws MalformedURLException, IOException {

		InputStream in = null;
		OutputStream out = null;

		try {

			in = new BufferedInputStream(new URL(address).openStream(),
					IO_BUFFER_SIZE);

			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, 4 * 1024);
			copy(in, out);
			out.flush();

			return dataStream.toByteArray();

		} catch (IOException e) {

			android.util.Log.e("IO", "fetchImage:Could not load buddy icon: "
					+ address, e);
		} finally {

			closeStream(in);
			closeStream(out);
		}

		return null;
	}

	/**
	 *
	 */
	private static void closeStream(Closeable stream) {

		if (null != stream) {

			try {
				stream.close();
			} catch (IOException e) {
				android.util.Log.e("IO", "closeStream:Could not close stream!",
						e);
			}
		}
	}

	/**
	 * Copy the content of the input stream into the output stream, using a
	 * temporary byte array buffer whose size is defined by
	 * {@link #IO_BUFFER_SIZE}.
	 */
	private static void copy(InputStream in, OutputStream out)
			throws IOException {

		byte[] b = new byte[4 * 1024];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	/**
	 * 
	 * @param data
	 * @param filename
	 */
	public static void saveImage(byte[] data, String filename) {

		File file = new File(ConstantsUtil.PROJECT_PATH + filename);
		FileOutputStream out;

		if (file.exists()) {
			file.delete();
		}

		try {

			out = new FileOutputStream(ConstantsUtil.PROJECT_PATH + filename);
			out.write(data);
			out.flush();
			out.close();

		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
