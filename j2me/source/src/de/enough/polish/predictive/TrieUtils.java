//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui
package de.enough.polish.predictive;

public class TrieUtils {
	private TrieUtils(){}
	
	public static int byteToInt(byte[] bytes, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (bytes[i + offset] & 0x000000FF) << shift;
		}
		return value;
    }
	
	public static char byteToChar(byte[] bytes, int offset)
	{
		int high = bytes[offset] & 0xff;
		int low = bytes[offset+1] & 0xff;
		return (char)((int)( high << 8 | low ));
	}
		
	public static byte byteToByte(byte[] bytes, int offset)
	{
		return bytes[offset];
	}
	
}
