package de.enough.polish.content;

import de.enough.polish.io.Serializable;
import de.enough.polish.util.ToStringHelper;

/**
 * A class to identify a content with an url,
 * a version and a transform id
 * 
 * @author Andre
 * 
 */
public class ContentDescriptor implements Serializable {
	/**
	 * the default version
	 */
	public static String TRANSFORM_NONE = "none";
	
	/**
	 * the default version
	 */
	public static int VERSION_DEFAULT = Integer.MIN_VALUE;

	/**
	 * the url
	 */
	protected final String url;

	/**
	 * the hash of the url
	 */
	protected final int hash;
	
	/**
	 * the transformation
	 */
	protected final String transformId;
	
	/**
	 * the version
	 */
	protected final int version;

	/**
	 * Convenience method that uses a default version
	 * 
	 * @param url
	 *            the url
	 */
	public ContentDescriptor(String url) {
		this(url, TRANSFORM_NONE, VERSION_DEFAULT);
	}
	
	/**
	 * Creates a new ContentDescriptor instance
	 * 
	 * @param url
	 *            the url
	 * @param version
	 *            the version
	 */
	public ContentDescriptor(String url, String transformId) {
		this(url, transformId, VERSION_DEFAULT);
	}
	
	/**
	 * Creates a new ContentDescriptor instance
	 * 
	 * @param url
	 *            the url
	 * @param version
	 *            the version
	 */
	public ContentDescriptor(String url, String transformId, int version) {
		this.url = url;
		this.hash = (url == null) ? 0 : url.hashCode();
		this.transformId = transformId;
		this.version = version;
	}

	/**
	 * Copies a ContentDescriptor instance
	 * 
	 * @param descriptor
	 *            the descriptor to copy
	 */
	protected ContentDescriptor(ContentDescriptor descriptor) {
		this.url = descriptor.getUrl();
		this.hash = descriptor.getHash();
		this.version = descriptor.getVersion();
		this.transformId = descriptor.getTransform();
	}

	/**
	 * Returns the url
	 * 
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}
	
	/**
	 * Returns the transform id
	 * 
	 * @return the transform id
	 */
	public String getTransform() {
		return this.transformId;
	}
	
	/**
	 * Returns the version
	 * 
	 * @return the version
	 */
	public int getVersion() {
		return this.version;
	}

	/**
	 * Returns the hash of the url
	 * 
	 * @return the hash of the url
	 */
	public int getHash() {
		return this.hash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		ContentDescriptor reference = (ContentDescriptor) obj;
		return reference.getHash() == getHash();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getHash();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringHelper("ContentDescriptor").
		add("url", this.url).
		add("version", this.version).
		add("hash", this.hash).
		toString();
	}

}
