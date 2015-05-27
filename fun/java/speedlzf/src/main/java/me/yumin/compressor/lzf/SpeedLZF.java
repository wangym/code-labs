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
// SpeedLZF.java
// Since: 2011/05/15
//
//--------------------------------------
package me.yumin.compressor.lzf;

/**
 * @author yumin
 * 
 */
public class SpeedLZF {

	/**
	 * 
	 */
	private static final int HASH_SIZE = 1 << 14; // 16384
	private static final int MAX_LITERAL = 1 << 5; // 32
	private static final int MAX_OFF = 1 << 13; // 8192
	private static final int MAX_REF = (1 << 8) + (1 << 3); // 256 + 8

	/*
	 * ==================== Public methods ====================
	 */

	/**
	 * 
	 * @param in
	 * @return
	 */
	public static byte[] compress(byte[] in) {

		int inLen = in.length;
		byte[] tmp = Util.arraycopy(Util.intToBytes(inLen), inLen, Constant.BYTES_OFFSET);

		int outLen = compressAlgorithm(in, tmp);
		byte[] out = Util.arraycopy(tmp, outLen, outLen);

		return out;
	}

	/**
	 * 
	 * @param in
	 * @return
	 */
	public static byte[] uncompress(byte[] in) {

		int inLen = Util.bytesToInt(in);

		byte[] out = Util.newByte(inLen);
		uncompressAlgorithm(in, Constant.BYTES_OFFSET, in.length
				- Constant.BYTES_OFFSET, out, 0, inLen);

		return out;
	}

	/*
	 * ==================== Private methods ====================
	 */

	private static int first(byte[] input, int inputPosition) {
		return (input[inputPosition] << 8) | (input[inputPosition + 1] & 255);
	}

	private static int next(int v, byte[] in, int inPos) {
		return (v << 8) | (in[inPos + 2] & 255);
	}

	private static int hash(int h) {
		return ((h * 2777) >> 9) & (HASH_SIZE - 1);
	}

	/**
	 * 
	 * @param input
	 * @param temp
	 * @return
	 */
	private static int compressAlgorithm(byte[] input, byte[] temp) {

		int inputPosition = 0;
		int inputLength = input.length;
		int tempStartPos = Constant.BYTES_OFFSET + 1;
		int[] hashTab = new int[HASH_SIZE]; // ?
		int literals = 0; // ?
		int future = first(input, inputPosition); // 24930

		while (inputPosition < inputLength - Constant.BYTES_OFFSET) {
			byte p2 = input[inputPosition + 2];
			future = (future << 8) + (p2 & 255);
			int off = hash(future);
			int ref = hashTab[off];
			hashTab[off] = inputPosition;
			if (ref < inputPosition && ref > 0
					&& (off = inputPosition - ref - 1) < MAX_OFF
					&& input[ref + 2] == p2
					&& input[ref + 1] == (byte) (future >> 8)
					&& input[ref] == (byte) (future >> 16)) {
				int maxLen = inputLength - inputPosition - 2;
				if (maxLen > MAX_REF) {
					maxLen = MAX_REF;
				}
				if (literals == 0) {
					tempStartPos--;
				} else {
					temp[tempStartPos - literals - 1] = (byte) (literals - 1);
					literals = 0;
				}
				int len = 3;
				while (len < maxLen
						&& input[ref + len] == input[inputPosition + len]) {
					len++;
				}
				len -= 2;
				if (len < 7) {

					temp[tempStartPos++] = (byte) ((off >> 8) + (len << 5));

				} else {

					temp[tempStartPos++] = (byte) ((off >> 8) + (7 << 5));
					temp[tempStartPos++] = (byte) (len - 7);
				}
				temp[tempStartPos++] = (byte) off;
				tempStartPos++;
				inputPosition += len;
				future = first(input, inputPosition);
				future = next(future, input, inputPosition);
				hashTab[hash(future)] = inputPosition++;
				future = next(future, input, inputPosition);
				hashTab[hash(future)] = inputPosition++;

			} else {

				temp[tempStartPos++] = input[inputPosition++];
				literals++;
				if (literals == MAX_LITERAL) {

					temp[tempStartPos - literals - 1] = (byte) (literals - 1);
					literals = 0;
					tempStartPos++;
				}
			}
		}

		while (inputPosition < inputLength) {
			temp[tempStartPos++] = input[inputPosition++];
			literals++;
			if (literals == MAX_LITERAL) {
				temp[tempStartPos - literals - 1] = (byte) (literals - 1);
				literals = 0;
				tempStartPos++;
			}
		}
		temp[tempStartPos - literals - 1] = (byte) (literals - 1);
		if (literals == 0) {
			tempStartPos--;
		}

		return tempStartPos;
	}

	/**
	 * 
	 * @param in
	 * @param inPos
	 * @param inLen
	 * @param out
	 * @param outPos
	 * @param outLen
	 */
	private static void uncompressAlgorithm(byte[] in, int inPos, int inLen,
			byte[] out, int outPos, int outLen) {
		if (inPos < 0 || outPos < 0 || outLen < 0) {
			throw new IllegalArgumentException();
		}
		do {
			int ctrl = in[inPos++] & 255;
			if (ctrl < MAX_LITERAL) {
				ctrl++;
				System.arraycopy(in, inPos, out, outPos, ctrl);
				outPos += ctrl;
				inPos += ctrl;
			} else {
				int len = ctrl >> 5;
				if (len == 7) {
					len += in[inPos++] & 255;
				}
				len += 2;
				ctrl = -((ctrl & 0x1f) << 8) - 1;
				ctrl -= in[inPos++] & 255;
				ctrl += outPos;
				if (outPos + len >= out.length) {
					throw new ArrayIndexOutOfBoundsException();
				}
				for (int i = 0; i < len; i++) {
					out[outPos++] = out[ctrl++];
				}
			}
		} while (outPos < outLen);
	}
}
