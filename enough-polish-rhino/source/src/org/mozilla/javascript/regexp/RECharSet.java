package org.mozilla.javascript.regexp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

/*
 * This struct holds a bitmap representation of a class from a regexp.
 * There's a list of these referenced by the classList field in the NativeRegExp
 * struct below. The initial state has startIndex set to the offset in the
 * original regexp source of the beginning of the class contents. The first
 * use of the class converts the source representation into a bitmap.
 *
 */
final public class RECharSet implements Externalizable
{
    //static final long serialVersionUID = 7931787979395898394L;

    public RECharSet()
    {
    }
    
    RECharSet(int length, int startIndex, int strlength)
    {
        this.iLength = length;
        this.startIndex = startIndex;
        this.strlength = strlength;
    }

    int iLength;
    int startIndex;
    int strlength;

    volatile transient boolean converted;
    volatile transient boolean sense;
    volatile transient byte[] bits;

	
	public void read(DataInputStream in) throws IOException {
		iLength = in.readInt();
		startIndex = in.readInt();
		strlength = in.readInt();
		
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(iLength);
		out.writeInt(startIndex);
		out.writeInt(strlength);
		
	}
}
