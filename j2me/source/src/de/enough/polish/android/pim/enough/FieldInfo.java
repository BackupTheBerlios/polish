//#condition polish.android
/**
 * 
 */
package de.enough.polish.android.pim.enough;

/**
 * This class describes a field in the JavaME PIM API.
 * @author rickyn
 *
 */
class FieldInfo {
	public static final int DEFAULT_PREFERRED_INDEX = -1;
	public static final int DEFAULT_NUMBER_OF_ARRAYELEMENTS = 0;
	protected final int numberOfArrayElements;
	protected final int pimId;
	protected final int type;
	protected final String label;
	protected final int preferredIndex;
	protected final int[] supportedArrayElements;
	protected final int[] supportedAttributes;
	public FieldInfo (int pimId, int type, String label,int numberOfArrayElements, int preferredIndex,int[] supportedArrayElements, int[] supportedAttributes) {
		this.pimId = pimId;
		this.type = type;
		this.label = label;
		this.numberOfArrayElements = numberOfArrayElements;
		this.preferredIndex = preferredIndex;
		this.supportedArrayElements = supportedArrayElements;
		this.supportedAttributes = supportedAttributes;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FieldInfo other = (FieldInfo) obj;
		if (this.pimId != other.pimId) {
			return false;
		}
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.pimId;
		return result;
	}
	@Override
	public String toString() {
		return "FieldInfo:Id:"+this.pimId+".Type:"+this.type+".Label:"+this.label+".ArrayElements:"+this.numberOfArrayElements+".";
	}
}