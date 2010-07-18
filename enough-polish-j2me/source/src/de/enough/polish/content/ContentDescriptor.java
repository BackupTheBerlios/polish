package de.enough.polish.content;

import de.enough.polish.io.Serializable;
import de.enough.polish.util.ToStringHelper;

/**
 * A class to identify a content with an url, a version and a transform id
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
	 * the default caching policy for only read
	 */
	public static int CACHING_READ = 0x01;

	/**
	 * the default caching policy for read and writing of content
	 */
	public static int CACHING_READ_WRITE = 0x02;

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
	protected String transformId = TRANSFORM_NONE;

	/**
	 * the version
	 */
	protected int version = VERSION_DEFAULT;

	/**
	 * the caching policy
	 */
	protected int cachingPolicy = CACHING_READ_WRITE;

	/**
	 * Creates a new ContentDescriptor instance
	 * 
	 * @param url
	 *            the url
	 */
	public ContentDescriptor(String url) {
		this.url = url;
		this.hash = (url == null) ? 0 : url.hashCode();
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
		this.transformId = descriptor.getTransformID();
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
	 * Returns the hash of the url
	 * 
	 * @return the hash of the url
	 */
	public int getHash() {
		return this.hash;
	}

	/**
	 * Returns the transform id
	 * 
	 * @return the transform id
	 */
	public String getTransformID() {
		return this.transformId;
	}

	/**
	 * Set the transform id
	 * 
	 * @param transformId
	 *            the transform id
	 */
	public void setTransformID(String transformId) {
		this.transformId = transformId;
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
	 * Set the version
	 * 
	 * @param version
	 *            the version
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Returns the caching policy
	 * 
	 * @return the caching policy
	 */
	public int getCachingPolicy() {
		return cachingPolicy;
	}

	/**
	 * Sets the caching policy
	 * 
	 * @param cachingPolicy
	 *            the caching policy
	 */
	public void setCachingPolicy(int cachingPolicy) {
		this.cachingPolicy = cachingPolicy;
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
		return ToStringHelper.createInstance("ContentDescriptor").set("url", this.url)
				.set("hash", this.hash).set("version", this.version).set(
						"cachingPolicy", this.cachingPolicy).toString();
	}

}
