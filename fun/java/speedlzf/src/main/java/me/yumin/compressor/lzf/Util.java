/*--------------------------------------------------------------------------
 *  Copyright 2011 yumin.wang
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *--------------------------------------------------------------------------*/

//--------------------------------------
// SpeedLZF-Java Project
//
// Util.java
// Since: 2011/05/15
//--------------------------------------
package me.yumin.compressor.lzf;

/**
 * @author yumin
 * 
 */
public class Util {

	/**
	 * 
	 * @param len
	 * @return
	 */
	public static byte[] newByte(int len) {

		byte[] bytes = {};
		if (0 < len) {
			bytes = new byte[len];
		}

		return bytes;
	}

	/**
	 * 
	 * @param integer
	 * @return
	 */
	public static byte[] intToBytes(int integer) {

		byte[] bytes = newByte(Constant.BYTES_OFFSET);
		bytes[0] = (byte) (0xFF & integer);
		bytes[1] = (byte) ((0xFF00 & integer) >> 8);
		bytes[2] = (byte) ((0xFF0000 & integer) >> 16);
		bytes[3] = (byte) ((0xFF000000 & integer) >> 24);

		return bytes;
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static int bytesToInt(byte[] bytes) {

		int integer = bytes[0] & 0xFF;
		integer |= ((bytes[1] << 8) & 0xFF00);
		integer |= ((bytes[2] << 16) & 0xFF0000);
		integer |= ((bytes[3] << 24) & 0xFF000000);

		return integer;
	}

	/**
	 * 
	 * @param src
	 * @param destLen
	 * @param len
	 * @return
	 */
	public static byte[] arraycopy(byte[] src, int destLen, int len) {

		byte[] dest = newByte(destLen);
		System.arraycopy(src, 0, dest, 0, len);

		return dest;
	}
}
