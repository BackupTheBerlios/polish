package org.mozilla.javascript.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Js;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeBoolean;
import org.mozilla.javascript.NativeDate;
import org.mozilla.javascript.NativeNumber;
import org.mozilla.javascript.NativeRecordstore;
import org.mozilla.javascript.NativeString;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

public class Util {
	
	public static int lastIndexOf(String source, String target, int fromIndex) {
		return lastIndexOf(source, source.length(), target, target.length(), fromIndex);
	}
	
	private static int lastIndexOf(String source, int sourceCount,
			String target, int targetCount, int fromIndex) {
		/*
		 * Check arguments; return immediately where possible. For consistency,
		 * don't check for null str.
		 */
		int rightIndex = sourceCount - targetCount;
		if (fromIndex < 0) {
			return -1;
		}
		if (fromIndex > rightIndex) {
			fromIndex = rightIndex;
		}
		/* Empty string always matches. */
		if (targetCount == 0) {
			return fromIndex;
		}

		int strLastIndex = targetCount - 1;
		char strLastChar = target.charAt(strLastIndex);
		int min = targetCount - 1;
		int i = min + fromIndex;

		startSearchForLastChar: while (true) {
			while (i >= min && source.charAt(i) != strLastChar) {
				i--;
			}
			if (i < min) {
				return -1;
			}
			int j = i - 1;
			int start = j - (targetCount - 1);
			int k = strLastIndex - 1;

			while (j > start) {
				if (source.charAt(j--) != target.charAt(k--)) {
					i--;
					continue startSearchForLastChar;
				}
			}
			return start + 1;
		}
	}

	public static Object createArrayFromRecord(Context cx, Scriptable scope, byte[] record) {
	    Scriptable top = Js.getTopLevelScope(scope);
	    Scriptable result = ScriptRuntime.newObject(cx, top, "Array", null);
	
	    ByteArrayInputStream bis = new ByteArrayInputStream(record);
		DataInputStream dis = new DataInputStream(bis);
		try {
			int type, i = 0;
			Object value;
			while (dis.available() != 0) {
				type = dis.readByte();
				switch(type) {
				case NativeRecordstore.RSTYPE_INT:
					value = new Double(dis.readInt());
					break;
				case NativeRecordstore.RSTYPE_LONG:
					value = new Double(dis.readLong());
					break;
				case NativeRecordstore.RSTYPE_DOUBLE:
					value = new Double(dis.readDouble());
					break;
				case NativeRecordstore.RSTYPE_DATE:
					Object args[] = new Object[] {new Double(dis.readLong())};
					value = ScriptRuntime.newObject(cx, top, "Date", args);
					break;
				case NativeRecordstore.RSTYPE_STRING:
					value = dis.readUTF();
					break;
				case NativeRecordstore.RSTYPE_BOOLEAN:
					value = ScriptRuntime.wrapBoolean(dis.readBoolean());
					break;
				case NativeRecordstore.RSTYPE_BYTEARRAY:
					dis.skip(dis.readInt());
					continue;
				case NativeRecordstore.RSTYPE_IMAGE:
					Object[] iArgs = new Object[1];
					dis.readUTF();
					byte[] ba = new byte[dis.readInt()];
					dis.readFully(ba);
					iArgs[0] = ba;
					value = ScriptRuntime.newObject(cx, top, "Image", iArgs);
					break;
				default:
					throw new IllegalArgumentException(String.valueOf(type));
	
				}
				result.put(i++, result, value);
			}
		} catch (IOException e) {
	        throw NativeRecordstore.createRecordStoreError(e.getMessage());
		}
		return result;
	}

	public static byte[] getRecordFromArray(NativeArray array, Scriptable start) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		Object value;
		try {
			for (int i = 0; i < array.getLength(); i++) {
				value = array.get(i, start);
				if (value instanceof NativeNumber) {
					dos.writeByte(NativeRecordstore.RSTYPE_DOUBLE);
					dos.writeDouble(ScriptRuntime.toNumber(value));
				} else if (value instanceof Double) {
					dos.writeByte(NativeRecordstore.RSTYPE_DOUBLE);
					dos.writeDouble(((Double) value).doubleValue());
				} else if (value instanceof Long) {
					dos.writeByte(NativeRecordstore.RSTYPE_LONG);
					dos.writeDouble(((Long) value).doubleValue());
				} else if (value instanceof Integer) {
					dos.writeByte(NativeRecordstore.RSTYPE_INT);
					dos.writeDouble(((Integer) value).doubleValue());
				} else if (value instanceof NativeString) {
					dos.writeByte(NativeRecordstore.RSTYPE_STRING);
					dos.writeUTF(ScriptRuntime.toString(value));
				} else if (value instanceof String) {
					dos.writeByte(NativeRecordstore.RSTYPE_STRING);
					dos.writeUTF((String) value);
				} else if (value instanceof NativeBoolean) {
					dos.writeByte(NativeRecordstore.RSTYPE_BOOLEAN);
					dos.writeBoolean(ScriptRuntime.toBoolean(value));
				} else if (value instanceof Boolean) {
					dos.writeByte(NativeRecordstore.RSTYPE_BOOLEAN);
					dos.writeBoolean(((Boolean) value).booleanValue());
				} else if (value instanceof NativeDate) {
					dos.writeByte(NativeRecordstore.RSTYPE_DATE);
					dos.writeLong(((NativeDate) value).getDate().getTime());
				} else {
			        throw NativeRecordstore.createRecordStoreError(null);
				}
			}
		} catch (IOException e) {
	        throw NativeRecordstore.createRecordStoreError(e.getMessage());
		} finally {
			try {
				dos.close();
			} catch (IOException ignored) {
			}
		}
		return bos.toByteArray();
	}

	public static Object createArrayFromRecord(Context context,
			Scriptable scope, DataInputStream dis, boolean readSize) {
	    Scriptable top = Js.getTopLevelScope(scope);
	    Scriptable result = ScriptRuntime.newObject(context, top, "Array", null);
	
		try {
			int type, i = 0;
			Object value;
			int size = (readSize) ?  dis.readInt() : dis.available();
			int pos = 0;
			while (pos  < size) {
				type = dis.readByte();
				pos += 1;
				switch(type) {
				case NativeRecordstore.RSTYPE_INT:
					value = new Double(dis.readInt());
					pos += 4;
					break;
				case NativeRecordstore.RSTYPE_LONG:
					value = new Double(dis.readLong());
					pos += 8;
					break;
				case NativeRecordstore.RSTYPE_DOUBLE:
					value = new Double(dis.readDouble());
					pos += 8;
					break;
				case NativeRecordstore.RSTYPE_DATE:
					Object args[] = new Object[] {new Double(dis.readLong())};
					value = ScriptRuntime.newObject(context, top, "Date", args);
					pos += 8;
					break;
				case NativeRecordstore.RSTYPE_STRING:
					value = dis.readUTF();
					pos += ((String)value).getBytes("UTF-8").length + 2;
					break;
				case NativeRecordstore.RSTYPE_BOOLEAN:
					value = ScriptRuntime.wrapBoolean(dis.readBoolean());
					pos += 1;
					break;
				case NativeRecordstore.RSTYPE_IMAGE:
					Object[] iArgs = new Object[1];
					value = dis.readUTF();
					int len = dis.readInt();
					byte[] data = new byte[len];
					pos += ((String)value).getBytes("UTF-8").length + 6 + len;
					dis.readFully(data);
					iArgs[0] = data;
					value = ScriptRuntime.newObject(context, top, "Image", iArgs);
					break;
				default:
					throw new IllegalArgumentException(String.valueOf(type));
	
				}
				result.put(i++, result, value);
			}
		} catch (IOException e) {
	        throw NativeRecordstore.createRecordStoreError(e.getMessage());
		}
		return result;
	}
	
	public static String createStringFromRespBody(DataInputStream dis) {
		StringBuffer result = new StringBuffer();
		try {
			int type;
			while (dis.available() > 0) {
				dis.mark(1);
				type = dis.readByte();
				switch(type) {
				case NativeRecordstore.RSTYPE_INT:
					result.append(dis.readInt());
					break;
				case NativeRecordstore.RSTYPE_LONG:
					result.append(dis.readLong());
					break;
				case NativeRecordstore.RSTYPE_DOUBLE:
					result.append(dis.readDouble());
					break;
				case NativeRecordstore.RSTYPE_DATE:
					result.append(new Date(dis.readLong()).toString());
					break;
				case NativeRecordstore.RSTYPE_STRING:
					result.append(dis.readUTF());
					break;
				case NativeRecordstore.RSTYPE_BOOLEAN:
					result.append(ScriptRuntime.wrapBoolean(dis.readBoolean()).toString());
					break;
				default:
					dis.reset();
					result.append("Sze=").append(dis.readInt());
					break;
	
				}
				result.append(';');
		}
		} catch (IOException e) {
			result.append("!!! IOException !!!");
		}
		return result.toString();
	}
	
	public static String createStringFromRespBody(byte[] record) {
	    ByteArrayInputStream bis = new ByteArrayInputStream(record);
		DataInputStream dis = new DataInputStream(bis);
		return createStringFromRespBody(dis);
}
}