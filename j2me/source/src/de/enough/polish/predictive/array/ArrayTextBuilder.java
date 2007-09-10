//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui && polish.TextField.usePredictiveInput 

package de.enough.polish.predictive.array;

import javax.microedition.rms.RecordStoreException;

import de.enough.polish.predictive.Reader;
import de.enough.polish.predictive.TextBuilder;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class ArrayTextBuilder extends TextBuilder {

	public ArrayTextBuilder(int textSize) {
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

	public void addWord(String string) {}

	public void addStringBuffer(String string) {
		addElement(new ArrayTextElement(new StringBuffer(string)));
		this.align = ALIGN_RIGHT;
	}

	public void addReader(Reader reader) {
		addElement(new ArrayTextElement(reader));
		this.align = ALIGN_FOCUS;
	}

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

			return true;
		}

		return false;
	}
}
