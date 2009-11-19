package de.enough.skylight.dom;

public interface Attr extends DomNode{

	/**
     * Returns the name of this attribute. If <code>Node.localName</code> is 
     * different from <code>null</code>, this attribute is a qualified name.
     */
    public String getName();

    /**
     *  <code>True</code> if this attribute was explicitly given a value in 
     * the instance document, <code>false</code> otherwise. If the 
     * application changed the value of this attribute node (even if it ends 
     * up having the same value as the default value) then it is set to 
     * <code>true</code>. The implementation may handle attributes with 
     * default values from other schemas similarly but applications should 
     * use <code>Document.normalizeDocument()</code> to guarantee this 
     * information is up-to-date. 
     */
    public boolean getSpecified();

    /**
     * On retrieval, the value of the attribute is returned as a string. 
     * Character and general entity references are replaced with their 
     * values. See also the method <code>getAttribute</code> on the 
     * <code>Element</code> interface.
     * <br>On setting, this creates a <code>Text</code> node with the unparsed 
     * contents of the string, i.e. any characters that an XML processor 
     * would recognize as markup are instead treated as literal text. See 
     * also the method <code>Element.setAttribute()</code>.
     * <br> Some specialized implementations, such as some [<a href='http://www.w3.org/TR/2003/REC-SVG11-20030114/'>SVG 1.1</a>] 
     * implementations, may do normalization automatically, even after 
     * mutation; in such case, the value on retrieval may differ from the 
     * value on setting. 
     */
    public String getValue();
    /**
     * On retrieval, the value of the attribute is returned as a string. 
     * Character and general entity references are replaced with their 
     * values. See also the method <code>getAttribute</code> on the 
     * <code>Element</code> interface.
     * <br>On setting, this creates a <code>Text</code> node with the unparsed 
     * contents of the string, i.e. any characters that an XML processor 
     * would recognize as markup are instead treated as literal text. See 
     * also the method <code>Element.setAttribute()</code>.
     * <br> Some specialized implementations, such as some [<a href='http://www.w3.org/TR/2003/REC-SVG11-20030114/'>SVG 1.1</a>] 
     * implementations, may do normalization automatically, even after 
     * mutation; in such case, the value on retrieval may differ from the 
     * value on setting. 
     * @exception DOMException
     *   NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
     */
    public void setValue(String value)
                            throws DomException;

    /**
     * The <code>Element</code> node this attribute is attached to or 
     * <code>null</code> if this attribute is not in use.
     * @since DOM Level 2
     */
    public DomNode getOwnerElement();
}
