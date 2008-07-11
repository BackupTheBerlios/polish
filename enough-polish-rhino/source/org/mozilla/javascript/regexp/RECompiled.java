package org.mozilla.javascript.regexp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;
import de.enough.polish.util.Arrays;

public class RECompiled implements Externalizable
{
    //static final long serialVersionUID = -6144956577595844213L;

    char []source;          /* locked source string, sans // */
    int parenCount;         /* number of parenthesized submatches */
    int flags;              /* flags  */
    byte[] program;         /* regular expression bytecode */
    int classCount;         /* count [...] bitmaps */
    RECharSet[] classList;  /* list of [...] bitmaps */
    int anchorCh = -1;      /* if >= 0, then re starts with this literal char */
	
     
	public void read(DataInputStream in) throws IOException {
    	source = (char[]) Serializer.deserialize(in);
		parenCount = in.readInt();
		flags = in.readInt();
    	program = (byte[]) Serializer.deserialize(in);
		classCount = in.readInt();
		Object[] result = (Object[]) Serializer.deserialize(in);
		
    	classList = (RECharSet[]) Arrays.cast(result, new RECharSet[ result == null ? 0 : result.length ] );
		anchorCh = in.readInt();
	}
	
	public void write(DataOutputStream out) throws IOException {
		Serializer.serialize(source, out);
		out.writeInt(parenCount);
		out.writeInt(flags);
		Serializer.serialize(program, out);
		out.writeInt(classCount);
		Serializer.serialize(classList, out);
		out.writeInt(anchorCh);
	}
}

