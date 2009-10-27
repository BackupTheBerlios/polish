package de.enough.skylight.css;

/**
 * This interface is used by elements which are also a box. With the distinction between BoxHolder and Box,
 * the caller is not able to set properties directly like on a div.
 * @author rickyn
 *
 */
public interface BoxHolder {

	/**
	 * The box this object establish for itself and for child boxes.
	 * @return
	 */
	public Box getEstablishedBox();
}
