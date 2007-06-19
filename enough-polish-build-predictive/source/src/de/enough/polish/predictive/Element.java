package de.enough.polish.predictive;

import java.util.Vector;

public class Element {

	public String parents;

	public Element parent;

	public char value;

	public Vector children;

	public int referenceID;

	Element() {
		this.parents = "";
		this.value = '\0';
		this.children = new Vector();
	}

	public Vector findChildren(Vector words) {
		for (int i = 0; i < words.size(); i++) {
			String word = (String) words.elementAt(i);
			if (isChild(word)) {
				Element child = new Element();

				child.parent = this;
				child.parents = word.substring(0, this.parents.length() + 1);
				child.value = word.charAt(this.parents.length());
				child.referenceID = 0;

				if (!hasChild(child.value)) {
					children.add(child);
				}
			}
		}

		return children;
	}

	private boolean isChild(String word) {
		if (word.length() > this.parents.length())
			return word.substring(0, this.parents.length()).equals(this.parents);
		else
			return false;
	}

	private boolean hasChild(char letter) {
		for (int i = 0; i < this.children.size(); i++) {
			Element child = (Element) this.children.elementAt(i);
			if (child.value == letter)
				return true;
		}

		return false;
	}
}
