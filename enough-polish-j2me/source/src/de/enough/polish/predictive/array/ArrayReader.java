//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui && polish.TextField.usePredictiveInput
package de.enough.polish.predictive.array;

import javax.microedition.rms.RecordStoreException;

import de.enough.polish.predictive.PredictiveReader;
import de.enough.polish.util.ArrayList;

public class ArrayReader extends PredictiveReader {

	ArrayList results;

	ArrayList words;

	ArrayList keys;

	public ArrayReader() {
		super();
		this.keys = new ArrayList();
	}

	public ArrayList getResults() {
		return this.results;
	}

	public StringBuffer getSelectedWord() {
		if (this.results.size() > 0)
		{
			return (StringBuffer) this.results.get(this.selectedWord);
		}
		else
		{
			return null;
		}
	}

	public void keyClear() throws RecordStoreException {
		if(this.keys.size() > 0)
		{
			this.keys.remove(this.keys.size() - 1);
			this.keyCount = this.keys.size();
	
			this.results = null;
			setResults();
		}
		
		setEmpty(this.results.size() == 0);
	}

	public void keyNum(int keyCode) throws RecordStoreException {
		this.keys.add(new Integer(keyCode));
		this.keyCount = this.keys.size();
		
		setResults();
		
		if(!isWordFound())
		{
			this.keys.remove(this.keys.size() - 1);
		}

		setEmpty(this.results.size() == 0);
	}
	
	public void setResults()
	{	
		ArrayList buffer = new ArrayList();

		if (this.results == null) {
			this.results = new ArrayList();
			copyArrayList(this.words, this.results);
		}

		for (int k = 0; k < this.results.size(); k++) {
			boolean add = false;
			Object result = this.results.get(k);
			//System.out.println("got result: " + result + ", class=" + result.getClass() );
			String word = ((StringBuffer) this.results.get(k)).toString();
			for (int i = 0; i < this.keys.size(); i++) {
				int key = ((Integer) this.keys.get(i)).intValue();
				setLetters(key);

				if (i < word.length()) {
					char value = word.toLowerCase().charAt(i);

					for (int j = 0; j < this.letters.length(); j++) {
						if (value != this.letters.charAt(j)) {
							add = false;
						}	
						else
						{
							add = true;
							break;
						}
					}
				}
				else
				{
					add = false;
				}
				
				if(!add)
				{
					break;
				}
			}

			if (add) {
				buffer.add(new StringBuffer(((StringBuffer)this.results.get(k)).toString()));
			}
		}
		
		if(buffer.size() > 0)
		{
			copyArrayList(buffer, this.results);
		}

		setWordFound(buffer.size() > 0);
	}

	public void copyArrayList(ArrayList source, ArrayList dest) {
		dest.clear();
		for (int i = 0; i < source.size(); i++) {
			dest.add(i, source.get(i));
		}
	}

	public ArrayList getWords() {
		return this.words;
	}

	public void setWords(ArrayList words) {
		//TODO Andre: why is everything 1) an arraylist (instead: String[]), 2) a StringBuffer (instead: String) ?
		this.words = words;
	}
}
