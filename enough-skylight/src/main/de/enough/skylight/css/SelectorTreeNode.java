package de.enough.skylight.css;


public class SelectorTreeNode{
	/**
	 * Universal selector transition.
	 */
	public static final int TRANSITION_CONDITION_IS_UNIVERSAL_TYPE = 1;
	/**
	 * Descendant selector transition.
	 */
	public static final int TRANSITION_CONDITION_IS_DESCENDANT = 2;
	/**
	 * Child selector transition.
	 */
	public static final int TRANSITION_CONDITION_IS_CHILD = 3;
	/**
	 * Type selector transition.
	 */
	public static final int TRANSITION_CONDITION_IS_TYPE = 4;
	/**
	 * Preceding adjacent Sibling selector transition.
	 */
	public static final int TRANSITION_CONDITION_PRECEDING_ADJACENT_SIBLING = 5;
	
	private SelectorTreeNode parent;
	private int childCount;
	private Properties properties;
	private int transitionCondition;
}