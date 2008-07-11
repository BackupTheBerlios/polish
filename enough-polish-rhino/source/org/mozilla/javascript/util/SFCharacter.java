// This file was generated AUTOMATICALLY from a template file Mon Sep 24 22:27:26 PDT 2007

/* @(#)CharacterDataLatin1.java.template	1.6 04/09/14
 *
 * Copyright 1994-2002 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 *
 */

package org.mozilla.javascript.util;

/**
 * The CharacterData class encapsulates the large tables found in
 * Java.lang.Character.
 */

public class SFCharacter {

	public static final int MIN_RADIX = 2;
	public static final int MAX_RADIX = 36;

	/**
	 * General category "Cn" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte UNASSIGNED = 0;

	/**
	 * General category "Lu" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte UPPERCASE_LETTER = 1;

	/**
	 * General category "Ll" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte LOWERCASE_LETTER = 2;

	/**
	 * General category "Lt" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte TITLECASE_LETTER = 3;

	/**
	 * General category "Lm" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte MODIFIER_LETTER = 4;

	/**
	 * General category "Lo" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte OTHER_LETTER = 5;

	/**
	 * General category "Mn" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte NON_SPACING_MARK = 6;

	/**
	 * General category "Me" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte ENCLOSING_MARK = 7;

	/**
	 * General category "Mc" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte COMBINING_SPACING_MARK = 8;

	/**
	 * General category "Nd" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte DECIMAL_DIGIT_NUMBER = 9;

	/**
	 * General category "Nl" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte LETTER_NUMBER = 10;

	/**
	 * General category "No" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte OTHER_NUMBER = 11;

	/**
	 * General category "Zs" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte SPACE_SEPARATOR = 12;

	/**
	 * General category "Zl" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte LINE_SEPARATOR = 13;

	/**
	 * General category "Zp" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte PARAGRAPH_SEPARATOR = 14;

	/**
	 * General category "Cc" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte CONTROL = 15;

	/**
	 * General category "Cf" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte FORMAT = 16;

	/**
	 * General category "Co" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte PRIVATE_USE = 18;

	/**
	 * General category "Cs" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte SURROGATE = 19;

	/**
	 * General category "Pd" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte DASH_PUNCTUATION = 20;

	/**
	 * General category "Ps" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte START_PUNCTUATION = 21;

	/**
	 * General category "Pe" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte END_PUNCTUATION = 22;

	/**
	 * General category "Pc" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte CONNECTOR_PUNCTUATION = 23;

	/**
	 * General category "Po" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte OTHER_PUNCTUATION = 24;

	/**
	 * General category "Sm" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte MATH_SYMBOL = 25;

	/**
	 * General category "Sc" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte CURRENCY_SYMBOL = 26;

	/**
	 * General category "Sk" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte MODIFIER_SYMBOL = 27;

	/**
	 * General category "So" in the Unicode specification.
	 * 
	 * @since 1.1
	 */
	public static final byte OTHER_SYMBOL = 28;

	/**
	 * General category "Pi" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte INITIAL_QUOTE_PUNCTUATION = 29;

	/**
	 * General category "Pf" in the Unicode specification.
	 * 
	 * @since 1.4
	 */
	public static final byte FINAL_QUOTE_PUNCTUATION = 30;

	/**
	 * Error flag. Use int (code point) to avoid confusion with U+FFFF.
	 */
	static final int ERROR = 0xFFFFFFFF;

	/*
	 * The character properties are currently encoded into 32 bits in the
	 * following manner: 1 bit mirrored property 4 bits directionality property
	 * 9 bits signed offset used for converting case 1 bit if 1, adding the
	 * signed offset converts the character to lowercase 1 bit if 1, subtracting
	 * the signed offset converts the character to uppercase 1 bit if 1, this
	 * character has a titlecase equivalent (possibly itself) 3 bits 0 may not
	 * be part of an identifier 1 ignorable control; may continue a Unicode
	 * identifier or Java identifier 2 may continue a Java identifier but not a
	 * Unicode identifier (unused) 3 may continue a Unicode identifier or Java
	 * identifier 4 is a Java whitespace character 5 may start or continue a
	 * Java identifier; may continue but not start a Unicode identifier
	 * (underscores) 6 may start or continue a Java identifier but not a Unicode
	 * identifier ($) 7 may start or continue a Unicode identifier or Java
	 * identifier Thus: 5, 6, 7 may start a Java identifier 1, 2, 3, 5, 6, 7 may
	 * continue a Java identifier 7 may start a Unicode identifier 1, 3, 5, 7
	 * may continue a Unicode identifier 1 is ignorable within an identifier 4
	 * is Java whitespace 2 bits 0 this character has no numeric property 1
	 * adding the digit offset to the character code and then masking with 0x1F
	 * will produce the desired numeric value 2 this character has a "strange"
	 * numeric value 3 a Java supradecimal digit: adding the digit offset to the
	 * character code, then masking with 0x1F, then adding 10 will produce the
	 * desired numeric value 5 bits digit offset 5 bits character type
	 * 
	 * The encoding of character properties is subject to change at any time.
	 */

	static int getProperties(int ch) {
		char offset = (char) ch;
		int props = A[offset];
		return props;
	}

	public static int getType(int ch) {
		int props = getProperties(ch);
		return (props & 0x1F);
	}

	public static boolean isLetter(int ch) {
		int type = getType(ch);
		return (((((1 << SFCharacter.UPPERCASE_LETTER)
				| (1 << SFCharacter.LOWERCASE_LETTER)
				| (1 << SFCharacter.TITLECASE_LETTER)
				| (1 << SFCharacter.MODIFIER_LETTER) | (1 << SFCharacter.OTHER_LETTER)) >> type) & 1) != 0);
	}

	public static boolean isJavaIdentifierStart(int ch) {
		int props = getProperties(ch);
		return ((props & 0x00007000) >= 0x00005000);
	}

	public static boolean isJavaIdentifierPart(int ch) {
		int props = getProperties(ch);
		return ((props & 0x00003000) != 0);
	}


	public static boolean isWhitespace(int ch) {
		int props = getProperties(ch);
		return ((props & 0x00007000) == 0x00004000);
	}

	static char[] sharpsMap = new char[] { 'S', 'S' };

	// The following tables and code generated using:
	// java GenerateCharacter -template
	// ../../tools/GenerateCharacter/CharacterDataLatin1.java.template -spec
	// ../../tools/GenerateCharacter/UnicodeData.txt -specialcasing
	// ../../tools/GenerateCharacter/SpecialCasing.txt -o
	// C:/BUILD_AREA/jdk6_03/control/build/windows-i586/gensrc/java/lang/CharacterDataLatin1.java
	// -string -usecharforbyte -latin1 8
	// The A table has 256 entries for a total of 1024 bytes.

	// Bereits zusammengebautes Array
	//#if polish.Name == "WindowsMobile"
	//# static final int A[] = { 0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x5800400f,0x5000400f,0x5800400f,0x6000400f,0x5000400f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x5000400f,0x5000400f,0x5000400f,0x5800400f,0x6000400c,0x68000018,0x68000018,0x28000018,0x2800601a,0x28000018,0x68000018,0x68000018,0xe8000015,0xe8000016,0x68000018,0x28000019,0x38000018,0x28000014,0x38000018,0x20000018,0x18003609,0x18003609,0x18003609,0x18003609,0x18003609,0x18003609,0x18003609,0x18003609,0x18003609,0x18003609,0x38000018,0x68000018,0xe8000019,0x68000019,0xe8000019,0x68000018,0x68000018,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0x827fe1,0xe8000015,0x68000018,0xe8000016,0x6800001b,0x68005017,0x6800001b,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0x817fe2,0xe8000015,0x68000019,0xe8000016,0x68000019,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x5000100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x4800100f,0x3800000c,0x68000018,0x2800601a,0x2800601a,0x2800601a,0x2800601a,0x6800001c,0x6800001c,0x6800001b,0x6800001c,0x7002,0xe800001d,0x68000019,0x68001010,0x6800001c,0x6800001b,0x2800001c,0x28000019,0x1800060b,0x1800060b,0x6800001b,0x7fd7002,0x6800001c,0x68000018,0x6800001b,0x1800050b,0x7002,0xe800001e,0x6800080b,0x6800080b,0x6800080b,0x68000018,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x68000019,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x827001,0x7fd7002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x68000019,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x817002,0x61d7002 };
	//#else
	static final int A[] = new int[256];
	static final String A_DATA = "\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800"
			+ "\u100F\u4800\u100F\u4800\u100F\u5800\u400F\u5000\u400F\u5800\u400F\u6000\u400F"
			+ "\u5000\u400F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800"
			+ "\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F"
			+ "\u4800\u100F\u4800\u100F\u5000\u400F\u5000\u400F\u5000\u400F\u5800\u400F\u6000"
			+ "\u400C\u6800\030\u6800\030\u2800\030\u2800\u601A\u2800\030\u6800\030\u6800"
			+ "\030\uE800\025\uE800\026\u6800\030\u2800\031\u3800\030\u2800\024\u3800\030"
			+ "\u2000\030\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800"
			+ "\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u3800\030\u6800\030"
			+ "\uE800\031\u6800\031\uE800\031\u6800\030\u6800\030\202\u7FE1\202\u7FE1\202"
			+ "\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1"
			+ "\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202"
			+ "\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1"
			+ "\202\u7FE1\uE800\025\u6800\030\uE800\026\u6800\033\u6800\u5017\u6800\033\201"
			+ "\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2"
			+ "\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201"
			+ "\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2"
			+ "\201\u7FE2\201\u7FE2\201\u7FE2\uE800\025\u6800\031\uE800\026\u6800\031\u4800"
			+ "\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u5000\u100F"
			+ "\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800"
			+ "\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F"
			+ "\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800"
			+ "\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F"
			+ "\u3800\014\u6800\030\u2800\u601A\u2800\u601A\u2800\u601A\u2800\u601A\u6800"
			+ "\034\u6800\034\u6800\033\u6800\034\000\u7002\uE800\035\u6800\031\u6800\u1010"
			+ "\u6800\034\u6800\033\u2800\034\u2800\031\u1800\u060B\u1800\u060B\u6800\033"
			+ "\u07FD\u7002\u6800\034\u6800\030\u6800\033\u1800\u050B\000\u7002\uE800\036"
			+ "\u6800\u080B\u6800\u080B\u6800\u080B\u6800\030\202\u7001\202\u7001\202\u7001"
			+ "\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001\202"
			+ "\u7001\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001"
			+ "\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001\u6800\031\202\u7001\202"
			+ "\u7001\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001\u07FD\u7002\201\u7002"
			+ "\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201"
			+ "\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002"
			+ "\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\u6800"
			+ "\031\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002"
			+ "\u061D\u7002";
	
	// In all, the character property tables require 1024 bytes.

	static {
		{ // THIS CODE WAS AUTOMATICALLY CREATED BY GenerateCharacter:
			char[] data = A_DATA.toCharArray();
			//assert (data.length == (256 * 2));
			int i = 0, j = 0;
			while (i < (256 * 2)) {
				int entry = data[i++] << 16;
				A[j++] = entry | data[i++];
			}
		}

	}
	//#endif
}
