//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui && polish.TextField.usePredictiveInput 

package de.enough.polish.predictive.trie;

import javax.microedition.rms.RecordStoreException;

import de.enough.polish.predictive.Reader;
import de.enough.polish.predictive.TextBuilder;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class TrieTextBuilder extends TextBuilder {

	public TrieTextBuilder(int textSize) {
		super(textSize);
	}

	public boolean keyClear() throws RecordStoreException {
		if (this.align == ALIGN_LEFT)
			if (this.element != 0)
				decreaseCaret();
			else
				return false;

		if (isStringBuffer(0)) {
			if (!decreaseStringBuffer()) {
				deleteCurrent();
				return false;
			} else
				return true;
		} else {
			if (getTextElement().getKeyCount() == getReader().getKeyCount())
				getReader().keyClear();

			getTextElement().keyClear();
			getTextElement().setResults();

			if (getReader().isEmpty()) {
				deleteCurrent();
				return false;
			} else {
				setAlign(ALIGN_FOCUS);
				getTextElement().setSelectedWordIndex(0);
				return (getTextElement().getResults().size() > 0)
						|| (getTextElement().getCustomResults().size() > 0);
			}
		}
	}

	public void addWord(String string) {
		TextField.PROVIDER.getCustom().addWord(string);
	}

	/**
	 * Creates a new <code>TextElement</code> carrying <code>string</code>
	 * 
	 * @param string
	 *            the string the <code>TextElement</code> should carry
	 */
	public void addStringBuffer(String string) {
		addElement(new TrieTextElement(new StringBuffer(string)));
		this.align = ALIGN_RIGHT;
	}

	/**
	 * Creates a new <code>TextElement</code> carrying <code>reader</code>
	 * 
	 * @param reader
	 *            the instance of <code>TrieReader</code> the
	 *            <code>TextElement</code> should carry
	 */
	public void addReader(Reader reader) {
		addElement(new TrieTextElement(reader));
		this.align = ALIGN_FOCUS;
	}

	/**
	 * Deletes the current element
	 * 
	 * @return true, if an element was deleted, otherwise false
	 */
	public boolean deleteCurrent() {
		if (this.textElements.size() > 0) {
			int index = this.element;

			if (this.element == 0)
				this.align = ALIGN_LEFT;
			else {
				this.element--;
				this.align = ALIGN_RIGHT;
			}

			this.textElements.remove(index);

			TextField.PROVIDER.releaseRecords();

			return true;
		}

		return false;
	}
}
