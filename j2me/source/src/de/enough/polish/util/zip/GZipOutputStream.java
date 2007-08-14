/*
 * Created on Jun 28, 2007 at 8:01:42 AM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.util.zip;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Jun 28, 2007 - Simon creation
 * </pre>
 * @author Simon Schmitt, simon.schmitt@enough.de
 */
public class GZipOutputStream extends OutputStream {
	private OutputStream outStream;
	
	private byte[] outputWindow;
	private byte[] plainDataWindow;
	private int outProcessed;
	private int plainPointer;
	
	private final static int HASHMAP_COUNT=4;
	ZipIntMultShortHashMap[] HM	= new ZipIntMultShortHashMap[HASHMAP_COUNT+1];
	
	private byte[] inputBuffer;
	private int inEnd;
	private int inStart;
	
	private int[] smallCodeBuffer;
	int[] huffmanCode;
	byte[] huffmanCodeLength;
	int[] distHuffCode;
	byte[] distHuffCodeLength;
	
	private int[] litCount;		// the # of each processed bytes
	private int[] distCount;
	
	private int isize;
	private int crc32;
	public int[] crc32Table=new int[256];
	
	private int status;
	private boolean lastBlock;
	private boolean lz77active;
	
	private int BTYPE;
	
	private final static int STREAM_INIT=0;
	//private final static int NEW_BLOCK=1;
	//private final static int COLLECTING_DATA=2;
	private final static int STREAMING=4;
	
	public int lazy_matching;
	
	/**
	 * 
	 * @param outputStream
	 * @param size
	 * @param compressionType
	 * @param plainWindowSize
	 * @param huffmanWindowSize
	 * @param lazy_matching
	 * @throws IOException
	 */
	public GZipOutputStream(OutputStream outputStream, int size, int compressionType, int plainWindowSize, int huffmanWindowSize, int lazy_matching) throws IOException {
		this.outStream = outputStream;

		this.inputBuffer=new byte[size+300];
		this.litCount=new int[286];
		this.distCount=new int[30];
		this.smallCodeBuffer = new int[2];
		
		// check plainWindowSize; this triggers the LZ77 compression
		if (plainWindowSize > 32768){
			throw new IOException("plainWindowSize > 32768");
		}
		if (plainWindowSize>=100){
			this.plainDataWindow = new byte[(plainWindowSize/HASHMAP_COUNT)*HASHMAP_COUNT]; // %HASHMAP_COUNT =0
			this.lz77active=true;
		} else {
			this.plainDataWindow=null;
			this.lz77active=false;
		}
		
		// check the huffmanWindowSize; this also triggers dynamic/fixed trees
		if (huffmanWindowSize > 32768){
			throw new IOException("plainWindowSize > 32768");
		}
		if (huffmanWindowSize<1024 && huffmanWindowSize>0){
			huffmanWindowSize=1024;
		}
		this.outputWindow = new byte[huffmanWindowSize];
		if(huffmanWindowSize==0){
			this.lastBlock=true;
			// fixed tree: write header, generate huffman codes
			this.BTYPE=1;
			newBlock();
			this.status=GZipOutputStream.STREAMING;
		} else {
			this.BTYPE=2;
			this.status=GZipOutputStream.STREAM_INIT;
		}
		
		this.lazy_matching = lazy_matching;
		
		for (int i = 0; i < HASHMAP_COUNT; i++) {
			this.HM[i] = new ZipIntMultShortHashMap(2*1024);
		}
		
		// write GZIP header, if wanted 
		if (compressionType==ZipHelper.TYPE_GZIP){
			/*
	         +---+---+---+---+---+---+---+---+---+---+
	         |ID1|ID2|CM |FLG|     MTIME     |XFL|OS | 
	         +---+---+---+---+---+---+---+---+---+---+*/
			this.outStream.write(31);
			this.outStream.write(139);
			this.outStream.write(8);
			this.outStream.write(new byte[6]);
			this.outStream.write(255);
		}
		
	}
	public void close() throws IOException{
		
		this.flush();
		
		// append the final tree, in case of dynamic trees
		if (this.BTYPE==2){
			
			// TODO this solution is uncool for small amounts of data
			// ok lets prepare the last block
			// TODO remove flushFinal ... is just read here and set=true
			// TODO set huffmanwindowsize > 300+inputBuffer?
			// empty the huffmanwindow
			compileOutput(); // TODO estimate, if this is really necessary,
							 // 	because we want to avoid too many new blocks
			
			// compile the few remaining bits into the final block
			// TODO fixed Tree??
			LZ77(true);
			this.lastBlock=true;
			compileOutput();
		} else {
			// no final tree, just flush
			LZ77(true);
			compileOutput();
		}
		
		writeFooter();
		
		this.outStream=null;
		
		this.outputWindow=null;
		this.inputBuffer=null;
		this.litCount=null;
		
	}
	public void flush() throws IOException{
		// flush inputBuffer -> LZ77
		
		LZ77(false); // do not set to true, since we still need sth. for the last block
		
		
	}
	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		// TODO test case
		
		//shift buffer to the beginning is done in LZ77
		/*if(this.inStart!=0){
			System.arraycopy(this.inputBuffer, this.inStart, this.inputBuffer, 0, this.inEnd-this.inStart);
			this.inEnd-=this.inStart;
			this.inStart=0;
		}*/
		
		if(this.inputBuffer.length == this.inEnd ){
    		// we need space
			// LZ77 the inputBuffer
    		LZ77(false);
		}
		
		// append the byte to the input buffer
		this.inputBuffer[this.inEnd++]=(byte) b;

		// ... and refresh the isize as well as crc32
		this.isize++;
		byte[] bb = new byte[1];
		bb[0]=(byte)b;
		this.crc32 = ZipHelper.crc32(this.crc32Table, this.crc32, bb, 0, 1);
		
	}
    public void write(byte b[]) throws IOException {
    	write(b, 0, b.length);
    }
    public void write(byte b[], int off, int len) throws IOException {
    	int processed=0; 

    	// refresh checksum
    	this.crc32 = ZipHelper.crc32(this.crc32Table, this.crc32, b, off, len);
    	this.isize+=len;
    	
    	while (processed!=len){
    		// TODO is done in LZ77
    		// shift buffer to the beginning
    		/*if(this.inStart!=0){
    			System.arraycopy(this.inputBuffer, this.inStart, this.inputBuffer, 0, this.inEnd-this.inStart);
    			this.inEnd-=this.inStart;
    			this.inStart=0;
    		}*/
    		
    		// fill data in 
    		if(this.inputBuffer.length - this.inEnd>=len-processed){
    			// if more than necessary fits in
    			System.arraycopy(b, processed+off, this.inputBuffer, this.inEnd, len-processed);
    			this.inEnd+=len-processed;
    			processed=len;
    		} else{
    			System.arraycopy(b, processed+off, this.inputBuffer, this.inEnd, this.inputBuffer.length-this.inEnd);
    			processed+=this.inputBuffer.length-this.inEnd;
    			this.inEnd=this.inputBuffer.length;
    		}
    		
    		// LZ77 the inputBuffer
    		LZ77(false);
    	}
    }
    
    
    private boolean search4LZ77(int[] bestPointer, int position) throws IOException{
    	ZipIntMultShortHashMap.Element found = null;

    	int[] pointer = new int[2];
    	bestPointer[1]=0;
    	
    	// TODO die kürzeren zuerst!!! ... ist derzeit egal, da ohnehin alle verglichen werden
    	for (int i = 0; i < HASHMAP_COUNT; i++) {
    		
    		// retrieve and compare the best pointers out of each hashmap
    		found=null;
    		found = this.HM[i].get( (128+this.inputBuffer[position])<<16 | (128+this.inputBuffer[position+1])<<8 | (128+this.inputBuffer[position+2]) );
    		
    		if (found!=null &&  found.size!=0){
    			searchHM4LZ77(found, pointer, position);
    			
    			if (pointer[1]> bestPointer[1]){
    				bestPointer[0]=pointer[0];
    				bestPointer[1]=pointer[1];
    			}
    			
    		}
    	}
    	
    	// was a pointer found?
    	return bestPointer[1]!=0;
    }
    
    /**
     *  This function takes the result element of a hashmap to look for previous reoccurrences of the data in the inputbuffer
     *  starting at position. The best matching distance and length pair is return in
     *  pointer. 
     *  
     * @param found the hashmap result of the thee chars
     * @param pointer  distance and lenth pair {distance, length}
     * @param position  position in the inputbuffer
     * @throws IOException
     */
    private void searchHM4LZ77(ZipIntMultShortHashMap.Element found, int[] pointer, int position) throws IOException{
    	int length;
    	
		// check this.plainPointer-found0.values[k] from the end since shortest pointers are at the end
		int bestK=0, bestLength=0;
		
		for (int k = found.size-1; k >=0 ;k--) {
			
			//length=3; //TODO we use 0 for safety
			length=0;
			
			int comparePointer=100000;
			
			// deal with index out of bounds in case of finish
			while(length<258 && position+length < this.inputBuffer.length){
				if (found.values[k]<this.plainPointer){
					comparePointer=(found.values[k]+ ( length % (this.plainPointer - found.values[k]) ) ) % this.plainDataWindow.length;
				} else {
					comparePointer=(found.values[k]+ ( length % (this.plainPointer + this.plainDataWindow.length - found.values[k]) ) ) % this.plainDataWindow.length;
				}
				
				// break, if the compared chars differ
				if (this.inputBuffer[position+length] == this.plainDataWindow[ comparePointer ] ){
					length++;
				} else {
					break;
				}
				
			}
			if (length<3){
				System.out.println("Error wrong pointer generated k="+k);
				throw new IOException();
			}
			// compare the recently found pointer pair with the currently best
			if (length>bestLength){
				bestK=k;
				bestLength=length;
				if (length==258){
					break;
				}
			}
		}
		
		// encode the pointer
		pointer[0]= /*distance=*/ (this.plainPointer-found.values[bestK] + this.plainDataWindow.length )% this.plainDataWindow.length;
		pointer[1]=/*length=*/bestLength;

		if (pointer[0]<0){
			System.out.println("xxxxxxxxxxxxxxxxxxxxx dist <0");
		}
    }
    
    /**
     * This function puts the pointer into the outputwindow 
     * 
     * @param distance
     * @param length
     * @throws IOException 
     */
    private void encodePointer(int distance, int length) throws IOException{
    	int litlen;
    	byte litextra;
    	int di;
    	int distExtra;
    	
		//System.out.println(length +  "-Tupel found  at " + distance + "back   EXTplainPointer=" + (plainPointer+debugWraps*this.plainDataWindow.length) + "  debugStorepointer==" + (debugStorepointer+2));
		
		// compute length information
		di = ZipHelper.encodeCode(ZipHelper.LENGTH_CODE, length);
		litlen=257+di;//+ZipUtil.LENGTH_CODE[di*2+1];
		litextra=(byte) (length - ZipHelper.LENGTH_CODE[di*2+1]);
		
		//compute distance information
		di = ZipHelper.encodeCode(ZipHelper.DISTANCE_CODE, distance);
		if (di<0){
			System.out.println("   zzzzzzzzzzzzzzzzzzzzz  distance could not be encoded");
		}
		distExtra = distance -ZipHelper.DISTANCE_CODE[di*2+1];					
		
		if (distExtra + ZipHelper.DISTANCE_CODE[di*2+1] != distance){
			System.out.println("dist encoding error");
		}
		
		
		// write buffer information for compiler
		if(this.outputWindow.length!=0){
			
			this.outputWindow[this.outProcessed]=(byte)255; // special distance stuff
			this.outputWindow[this.outProcessed+1]= (byte) (litlen -255); // 0 is reserved!!
			if (this.outputWindow[this.outProcessed+1]<0){
				System.out.println("  EOORORO");
			}
			
			this.outputWindow[this.outProcessed+2]= litextra;// value of extra bits
			this.outputWindow[this.outProcessed+3]=(byte)(di);// id of Table == code
			this.outputWindow[this.outProcessed+4]=(byte)(distExtra&0xFF);//
			this.outputWindow[this.outProcessed+5]=(byte)(distExtra>>8 &0xFF);//
			this.outputWindow[this.outProcessed+6]=(byte)(distExtra>>16 &0xFF);//
			
			this.outProcessed+=6; // 7-1
			
			// increase distCount
			this.litCount[litlen]++;
			this.distCount[di]++;
			
		} else { // write direct
			
			// write litlen + extra bytes
			pushSmallBuffer(this.huffmanCode[litlen],this.huffmanCodeLength[litlen]);
			pushSmallBuffer(litextra, (byte) ZipHelper.LENGTH_CODE[2*(litlen-257)]);
			
			// write distance code + extra info
			pushSmallBuffer(this.distHuffCode[di],this.distHuffCodeLength[di], false);
			pushSmallBuffer(distExtra, (byte) ZipHelper.DISTANCE_CODE[di*2],false);
			
		}
		
    }
    
    private void encodeChar(int position) throws IOException {
    	int val = (this.inputBuffer[position] + 256)%256;
    	
    	if(this.outputWindow.length!=0){
			// and increase the literal Count
			this.litCount[val]++;
	
			// fill the plain char in the output window ... encode 255
			this.outputWindow[this.outProcessed] = (byte)val;
			if(val==255){
				//special encoding for 255
				this.outProcessed++;
				this.outputWindow[this.outProcessed]=(byte)0;
			}
    	} else{
    		pushSmallBuffer(this.huffmanCode[val],this.huffmanCodeLength[val],false);
    	}
    }
    
    
    /**
     * This function applies the LZ77 algorithm on the inputBuffer.
     * The inputBuffer is cleared completely and filled into 
     * outputWindow. If outputWindow is full, compileOutput is called.
     * @throws IOException 
     */
    //public int l=0;
    private void LZ77(boolean finish) throws IOException{
    	
    	// prepate the inputbuffer
    	if (this.inStart!=0){
			System.arraycopy(this.inputBuffer, this.inStart, this.inputBuffer, 0, this.inEnd-this.inStart);
			this.inEnd-=this.inStart;
			this.inStart=0;
		}
    	// do not leave sth. behind if finish is enabled
    	int upTo;
    	if(finish){
    		upTo=this.inEnd;
    	} else {
    		upTo=this.inEnd-300;
    	}
    	
    	// process the inputbuffer
    	int pointer[] = new int[2];
    	int lastpointer[] = new int[2];
    	
    	int distance;
    	int length;
    	
    	int i;
    	for (i = 0; i < upTo; ) { 
    		
    		// TODO add new values in hashmap again ... good way to set a MAX for found.values[]
    		length=1;
    		distance=0;
    		
    		// search for reoccurrence, if there is enough input data left
    		if(this.lz77active && i < upTo-2 && search4LZ77(pointer, i)){
    			
    			if (pointer[1]>lastpointer[1] &&  pointer[1] < this.lazy_matching){
    				// put single char of (i-1) and keep searching
    				lastpointer[0]=pointer[0];
    				lastpointer[1]=pointer[1];
    			} else {
    				distance=pointer[0];
    				length=pointer[1];
    			}
    			
    		}
    		
    		// TODO not perfect implemented ... happens just a few times so we need no real optimization
    		// the pointer is not allowed to exceed the inputBuffer!!
    		if (finish && upTo-i<length){
    			length=upTo-i;
    		}
    		
    		// encode pointer or char
    		if( length>2 ){
    			encodePointer(distance, length);
    		}else{
    			encodeChar(i);
    		}
    		
    		
    		// check if Huffman compression is requested
    		if (this.outputWindow.length!=0){
	    		// check, if we have to compile the output
	    		this.outProcessed++;
	    		// there has to be enough space for a length, distance pair
	    		if (this.outProcessed+8>this.outputWindow.length){
	    			// if outputWindow full : call compileOutput
	    			compileOutput();
	    			// TODO sicherstellen, dass wirklich ein lastBlock geschrieben wird!!!
	    			//this.outProcessed=0;
	    		}
	    		
    		}
    		
			// refresh the plain window, if lz77 is active
    		if (this.lz77active){
				for (int k = 0; k < length; k++) {
					this.plainDataWindow[this.plainPointer] = this.inputBuffer[i+k];
					
					// add the bytes to the hashmap
		    		this.HM[this.plainPointer/(this.plainDataWindow.length/HASHMAP_COUNT)].put( (128+this.inputBuffer[i+k])<<16 | (128+this.inputBuffer[i+k+1])<<8 | (128+this.inputBuffer[i+k+2]) , (short) this.plainPointer);
	
					
					// clear hashmap
					if (++this.plainPointer%(this.plainDataWindow.length/HASHMAP_COUNT)==0){
						// wrap around
						if (this.plainPointer==this.plainDataWindow.length){
							this.plainPointer=0;
						}
						
						this.HM[(this.plainPointer/(this.plainDataWindow.length/HASHMAP_COUNT)) % HASHMAP_COUNT].clear();
					}
					
				}
    		}
			// i is just increased here
			i+=length;
    		
		}
    	
    	this.inStart=i;//upTo;
    }
    
    private void newBlock() throws IOException{
    	if(this.status==GZipOutputStream.STREAM_INIT){
			this.status=GZipOutputStream.STREAMING;
		} else {
			pushSmallBuffer(this.huffmanCode[256],this.huffmanCodeLength[256]);
			// write old 256, write blockheader
		}
		// if status==FLUSH_FINAL : set final flag
		if(this.lastBlock){
			pushSmallBuffer(1, (byte)1);
			System.out.println("final block");
		} else{
			pushSmallBuffer(0, (byte)1);
		}
		
		pushSmallBuffer(this.BTYPE, (byte)2);
		
		this.huffmanCode = new int[286];
		this.huffmanCodeLength  = new byte[286];
		this.distHuffCode  = new int[30];
		this.distHuffCodeLength  = new byte[30];
		
		if (this.BTYPE==1){
			// TODO genFixed Tree ohne  data=new int[32]; aufrufen
			ZipHelper.genFixedTree(this.huffmanCode, this.huffmanCodeLength, this.distHuffCode, this.distHuffCodeLength);
		} else if (this.BTYPE==2) {
			
			
	    	// fake two distance codes, if there are none
	    	for (int i = 0; i < 2; i++) {
	    		if (this.distCount[i]==0){
	    			this.distCount[i]=1;
				}
			}
			
			// generate dynamic Huffmantree for Literals + Length
			this.litCount[256]=1; // theoretisch kann man es auch auf 2 setzen, was das Problem jedoch nicht löst
			ZipHelper.genTreeLength(this.litCount, this.huffmanCodeLength,15);
			ZipHelper.genHuffTree(this.huffmanCode, this.huffmanCodeLength);
			ZipHelper.revHuffTree(this.huffmanCode, this.huffmanCodeLength);
			
			for (int i = 0; i < this.huffmanCodeLength.length; i++) {
				if (this.huffmanCodeLength[i]>15){
					System.out.println("Error: wrong tree length at " + i +" generated");
					if (true) throw new IOException();
				}
			}
			
			// generate dynamic Huffmantree for Distances
			ZipHelper.genTreeLength(this.distCount, this.distHuffCodeLength,15);
			ZipHelper.genHuffTree(this.distHuffCode, this.distHuffCodeLength);
			ZipHelper.revHuffTree(this.distHuffCode, this.distHuffCodeLength);
			
			// save tree
			//System.out.println("compressing tree");
			compressTree(this.huffmanCodeLength, this.distHuffCodeLength);
			
			// clear the counter
			for (int i = 0; i < 286; i++) {
				this.litCount[i]=0;
			}
			for (int i = 0; i < 30; i++) {
				this.distCount[i]=0;
			}
			
			System.out.println(" pushCount: " + pushCount);
		}

    }
    
	/**
	 * This function applies the huffmanencoding on the collected data
	 * from outputWindow.
	 * @throws IOException 
	 */
	private void compileOutput() throws IOException{
		System.out.println("  compile Output  debugStorepointerCompile=" + "   pushCount: " + pushCount);
		System.out.println(" new Block");

		// generate & store the tree
		newBlock();			
		
		int litlen,	litextra,di,distExtra;
		
		// write the data
		int val=0;
		for (int i = 0; i < this.outProcessed; i++) {
			
			val=this.outputWindow[i];
			if(val<0){
				val+=256;
			} 
			if(val!=255){
				pushSmallBuffer(this.huffmanCode[val],this.huffmanCodeLength[val],false);
			} else {
				if (val==255){
					i++;
					if (this.outputWindow[i]==0){
						// 255 char
						pushSmallBuffer(this.huffmanCode[255],this.huffmanCodeLength[255],false);
						
					} else if(this.outputWindow[i]>0) {
						// compile encoded pointer
						
						// length information
						litlen=255+this.outputWindow[i];
						i++;
						litextra=this.outputWindow[i];
						i++;
						
						// distance information
						di=this.outputWindow[i];
						i++;
						// TODO maybe optimize using if statements ...??
						distExtra= ((this.outputWindow[i]+256) % 256) | ((this.outputWindow[i+1]+256) % 256)<<8 | ((this.outputWindow[i+2]+256) % 256) <<16;
						i+=3;
						//distance=distExtra + ZipUtil.DISTANCE_CODE[di*2+1];

						// write litlen + extra bytes
						pushSmallBuffer(this.huffmanCode[litlen],this.huffmanCodeLength[litlen],false);
						pushSmallBuffer(litextra, (byte) ZipHelper.LENGTH_CODE[2*(litlen-257)],false);
						
						// write distance code + extra info
						pushSmallBuffer(this.distHuffCode[di],this.distHuffCodeLength[di], false);
						pushSmallBuffer(distExtra, (byte) ZipHelper.DISTANCE_CODE[di*2],false);
						
						i--;
					} else {
						System.out.println("Error");
					}
					
				}
				
			}
		}
		this.outProcessed=0;
		/*if(this.flushFinal){
			writeFooter();	
		}*/
		
	}
	
	private void writeFooter() throws IOException{
		// write current 256
		pushSmallBuffer(this.huffmanCode[256],this.huffmanCodeLength[256],false);
		System.out.println(" wrote final 256; pushcount: " + pushCount);
		
		// flush the upto the byte boundrary
		if ((this.smallCodeBuffer[1]&7) != 0){
			pushSmallBuffer(0, (byte) (8-(this.smallCodeBuffer[1]&7)),false);
		}
		
		//write CRC, count
		this.outStream.write(this.crc32 & 255);
		this.outStream.write((this.crc32>>>8) & 255);
		this.outStream.write((this.crc32>>>16) & 255);
		this.outStream.write((this.crc32>>>24) & 255);
		
		this.outStream.write(this.isize & 255);
		this.outStream.write((this.isize>>>8) & 255);
		this.outStream.write((this.isize>>>16) & 255);
		this.outStream.write((this.isize>>>24) & 255);
		
		// close the stream
		this.outStream.flush();
		this.outStream.close();
		
		System.out.println(" flush finish; pushcount: " + pushCount);
	}

    private void compressTree(byte[] huffmanCodeLength, byte[] distHuffCodeLength) throws IOException{
    	
    	int HLIT=285;
    	int HDIST=29;
    	
    	while(huffmanCodeLength[HLIT]==0 && HLIT>29){HLIT--;}
    	HLIT++;
    	
    	// dont worry: empty distance tree workaround is in newBlock();
    	while(distHuffCodeLength[HDIST]==0 && HDIST>0){HDIST--;}
    	HDIST++; // # of all Distance Symbols
    	
    	// merge Hlit + Hdist
    	byte[] len=new byte[HLIT+HDIST];
    	int j=0;
    	for (int i = 0; i < HLIT; i++) {
    		len[j]=huffmanCodeLength[i];
    		//System.out.println("  " + j + " " + huffmanCode[j] + ", " + huffmanCodeLength[j]);
    		j++;
		}
    	for (int i = 0; i < HDIST; i++) {
    		len[j]=distHuffCodeLength[i];
    		//System.out.println("  " + j + " " + distHuffCode[i] + ", " + distHuffCodeLength[i]);
    		j++;
		}
    	
    	int[] miniHuffData= { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15};
    	
    	// fill repeatcodes in and count them
    	byte[] outLitLenDist=new byte[HLIT+HDIST];
    	int outCount=0;
    	int[] miniCodeCount=new int[19]; // count of used miniCodes
		short k;
    	for (int i = 0; i < len.length; i++) {
    		
			if( i+3< len.length && len[i]==len[i+1] && len[i]==len[i+2] && len[i]==len[i+3]){
				if(len[i]==0){
					outLitLenDist[outCount]=0; // ausgliedern
					System.out.println("wrinting outCount="+outCount + "   =  0");
					k=4;
					
					while(i+k<len.length && len[i]==len[i+k] && k<139){k++;}

					
					if (k<11+1) {
						//System.out.println("writing outCount="+outCount + "   =  17");
						outLitLenDist[outCount+1]=17;
						outLitLenDist[outCount+2]=(byte) (k-3-1);
					} else{
						//System.out.println("writing outCount="+outCount + "   =  18");
						//System.out.println(" repeat count = " + (k-1));
						outLitLenDist[outCount+1]=18;
						outLitLenDist[outCount+2]=(byte) (k-11-1);
					}
					
					i+=k-1;
				}else{
					// encode repeat code ; char * n -> char, 16, n
					//System.out.println("writing outCount="+outCount + "   =  "+len[i]);
					outLitLenDist[outCount]=len[i];
					outLitLenDist[outCount+1]=16;
					//System.out.println("writing outCount="+outCount + "   =  16");
					
					// repeat count
					k=4;
					while(i+k<len.length && len[i]==len[i+k] && k<7){k++;}
					outLitLenDist[outCount+2]= (byte) (k-4);
					i+=k-1;
				}
				// add value to statistic
				miniCodeCount[outLitLenDist[outCount]]++;
				// add repeat code to statistic
				miniCodeCount[outLitLenDist[outCount+1]]++;
				// +3  == 2 +1
				outCount+=2;
				
			} else{
				//System.out.println("wrinting outCount="+outCount + "   =  "+ len[i]);
				outLitLenDist[outCount]=len[i];
				miniCodeCount[outLitLenDist[outCount]]++;
			}

			outCount++;
		}
    	
    	
    	// generate mini Tree
		byte[] miniHuffCodeLength=new byte[19];
		int[] miniHuffCode=new int[19];
		
		int i=0;
		
		ZipHelper.genTreeLength(miniCodeCount, miniHuffCodeLength,7);
		
		/*
		for (i = 0; i < miniHuffCodeLength.length; i++) {
			if (miniHuffCodeLength[i]>7){
				//break;
				throw new IOException(" error in fixing tree");
			}
		}
		*/
		
		ZipHelper.genHuffTree(miniHuffCode, miniHuffCodeLength);
		ZipHelper.revHuffTree(miniHuffCode, miniHuffCodeLength);
		
		// write Header-Header
    	pushSmallBuffer(HLIT-257, (byte)5);
    	pushSmallBuffer(HDIST-1, (byte)5); 
    	
    	
    	int HCLEN=19-1;
    	while(miniHuffCodeLength[miniHuffData[HCLEN]]==0 && HCLEN>0){HCLEN--;}
    	HCLEN++;
    	
       	pushSmallBuffer(HCLEN-4, (byte)4);
		
       	// write Mini-Tree
       	System.out.println("writing miniTree");
       	for (i = 0; i < HCLEN; i++) {
       		//System.out.println("" + i + "=" +miniHuffData[i] + ":\t" + miniHuffCodeLength[miniHuffData[i]]);
       		pushSmallBuffer(miniHuffCodeLength[miniHuffData[i]], (byte)3);
       		
		}
       	
       	System.out.println("writing compressed miniTree");
    	System.out.println(" HLIT: " + HLIT);
    	System.out.println(" HDIST: " + HDIST);
    	System.out.println(" HCLEN: " + HCLEN);
       	
		// write Huffmanntree
    	for (i = 0; i < outCount; i++) {
    		// write code
    		//System.out.println("wrinting i="+i + "   =  " + outLitLenDist[i]);
    		pushSmallBuffer(miniHuffCode[outLitLenDist[i]], miniHuffCodeLength[outLitLenDist[i]]);

    		// add special treatment
    		if (outLitLenDist[i]>15){
    			switch (outLitLenDist[i]) {
				case 16:
					pushSmallBuffer(outLitLenDist[i+1], (byte)2);
					i++;
					break;
				case 17:
					pushSmallBuffer(outLitLenDist[i+1], (byte)3);
					i++;
					break;
				default: // 18
					pushSmallBuffer(outLitLenDist[i+1], (byte)7);
					i++;
				}
    			
    		}
    		
    		//System.out.println(outLitLenDist[i] + " == code: " + miniHuffCode[outLitLenDist[i]]);
		}
    	
    }
	
	
    private int pushCount=0;
    private int pushCountTimes;
	/**
	 * This function is able to write bits into the outStream. Please
	 * mind that is uses a buffer. You should flush it, by giving zeros.
	 *   
	 * @param val The bitsequence as an integer.
	 * @param len The number of bits to process.
	 * @throws IOException in case Errors within the outputStream ocurred
	 */
    private void pushSmallBuffer(int val, byte len) throws IOException{
    	pushSmallBuffer(val,len,true);
    }
	private void pushSmallBuffer(int val, byte len, boolean log) throws IOException{
		
		pushCount+=len;
		pushCountTimes++;
		// add the given data
		this.smallCodeBuffer[0]&= ~( ((1<<len)-1) << this.smallCodeBuffer[1]);
		this.smallCodeBuffer[0]|= (val<<this.smallCodeBuffer[1]);
		this.smallCodeBuffer[1]+=len;
		
		// clear the buffer except for a fraction of a reamining byte
		while(this.smallCodeBuffer[1]>=8){
			this.outStream.write(this.smallCodeBuffer[0]&255); //TODO langsam!! aber die HM ist wesentlich schlimmer
			this.smallCodeBuffer[0]>>>=8;
			this.smallCodeBuffer[1]-=8;
		}
		
	}
	
}
