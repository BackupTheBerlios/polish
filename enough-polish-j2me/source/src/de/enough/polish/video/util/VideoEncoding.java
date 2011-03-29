package de.enough.polish.video.util;

import de.enough.polish.util.HashMap;
import de.enough.polish.util.TextUtil;
import de.enough.polish.util.ToStringHelper;

/**
 * Parses an encoding string and stores its values
 * 
 * @author Andre
 * 
 */
public class VideoEncoding {

	/**
	 * key for the mime type
	 */
	private static final String MIME = "encoding";

	/**
	 * key for the width
	 */
	private static final String WIDTH = "width";

	/**
	 * key for the height
	 */
	private static final String HEIGHT = "height";

	/**
	 * key for the video codec
	 */
	private static final String VIDEO_CODEC = "video_codec";

	/**
	 * key for the audio codec
	 */
	private static final String AUDIO_CODEC = "audio_codec";

	/**
	 * key for the mode
	 */
	private static final String MODE = "mode";

	/**
	 * the mime type
	 */
	private final String mime;

	/**
	 * the width
	 */
	private final String width;

	/**
	 * the height
	 */
	private final String height;

	/**
	 * the video codec
	 */
	private final String videoCodec;

	/**
	 * the audio codec
	 */
	private final String audioCodec;

	/**
	 * the mode
	 */
	private final String mode;

	/**
	 * Creates a new VideoEncoding instance
	 * 
	 * @param encoding
	 *            the text of a single encoding
	 * @throws IllegalArgumentException
	 *             if the encoding format is wrong
	 */
	public VideoEncoding(String encoding) throws IllegalArgumentException {
		HashMap encodingMap = parseEncoding(encoding);
		this.mime = (String) encodingMap.get(MIME);
		this.width = (String) encodingMap.get(WIDTH);
		this.height = (String) encodingMap.get(HEIGHT);
		this.videoCodec = (String) encodingMap.get(VIDEO_CODEC);
		this.audioCodec = (String) encodingMap.get(AUDIO_CODEC);
		this.mode = (String) encodingMap.get(MODE);
	}

	/**
	 * Creates a new VideoEncoding instance
	 * 
	 * @param mime
	 *            the mime type
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param videoCodec
	 *            the video codec
	 * @param audioCodec
	 *            the audio codec
	 * @param mode
	 *            the mode
	 */
	public VideoEncoding(String mime, String width, String height,
			String videoCodec, String audioCodec, String mode) {
		this.mime = mime;
		this.width = width;
		this.height = height;
		this.videoCodec = videoCodec;
		this.audioCodec = audioCodec;
		this.mode = mode;
	}

	/**
	 * Parses an encoding key/value pair
	 * 
	 * @param pair
	 *            the pair
	 * @return the key/value pair
	 */
	private String[] parseEncodingValue(String pair) {
		return TextUtil.split(pair, '=');
	}

	/**
	 * Parses the given encoding to extract its values
	 * 
	 * @param encoding
	 *            the encoding
	 * @return the hashmap containing the extracted values with their keys
	 * @throws IllegalArgumentException
	 *             if the encoding format is wrong
	 */
	private HashMap parseEncoding(String encoding)
			throws IllegalArgumentException {
		HashMap encodingMap = new HashMap();
		String[] encodingValues = TextUtil.split(encoding, '&');

		for (int index = 0; index < encodingValues.length; index++) {
			String encodingValue = encodingValues[index];
			String[] encodingPair = parseEncodingValue(encodingValue);

			if (encodingPair.length == 2) {
				String key = encodingPair[0];
				String value = encodingPair[1];

				encodingMap.put(key, value);
			} else {
				throw new IllegalArgumentException(encoding
						+ " has wrong format");
			}
		}

		return encodingMap;
	}

	/**
	 * Returns the mime type
	 * 
	 * @return the mime type
	 */
	public String getMime() {
		return mime;
	}

	/**
	 * Returns the width
	 * 
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * Returns the height
	 * 
	 * @return the height
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * Returns the video codec
	 * 
	 * @return the video codec
	 */
	public String getVideoCodec() {
		return videoCodec;
	}

	/**
	 * Returns the audio codec
	 * 
	 * @return the audio codec
	 */
	public String getAudioCodec() {
		return audioCodec;
	}

	/**
	 * Returns the mode
	 * 
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringHelper(this).set(MIME, this.mime).set(WIDTH,
				this.width).set(HEIGHT, this.height).set(VIDEO_CODEC,
				this.videoCodec).set(AUDIO_CODEC, this.audioCodec).set(MODE,
				this.mode).toString();
	}
}
