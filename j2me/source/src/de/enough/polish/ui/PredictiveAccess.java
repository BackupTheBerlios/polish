//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui && polish.TextField.usePredictiveInput

package de.enough.polish.ui;

import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.rms.RecordStoreException;

import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.predictive.TextBuilder;
import de.enough.polish.predictive.TextElement;
import de.enough.polish.predictive.array.ArrayReader;
import de.enough.polish.predictive.array.ArrayTextBuilder;
import de.enough.polish.predictive.trie.TrieProvider;
import de.enough.polish.predictive.trie.TrieReader;
import de.enough.polish.predictive.trie.TrieTextBuilder;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;

//#if polish.predictive.useLocalRMS
import de.enough.polish.predictive.trie.TrieSetup;
//#endif

//#if polish.blackberry
import de.enough.polish.blackberry.ui.PolishEditField;
//#endif

public class PredictiveAccess {
	private TextField parent;

	public static final int ORIENTATION_BOTTOM = 0;

	public static final int ORIENTATION_TOP = 1;

	public static final int TRIE = 0;

	public static final int ARRAY = 1;

	public static TrieProvider PROVIDER = new TrieProvider();

	static Command ENABLE_PREDICTIVE_CMD = new Command(Locale.get("polish.predictive.command.enable"), Command.ITEM, 10);

	private static Command INSTALL_PREDICTIVE_CMD = new Command(Locale.get("polish.predictive.command.install"), Command.ITEM, 10);

	static Command DISABLE_PREDICTIVE_CMD = new Command(Locale.get("polish.predictive.command.disable"), Command.ITEM, 10);

	static Command ADD_WORD_CMD = new Command(Locale.get("polish.predictive.registerNewWord.command"), Command.ITEM, 11);

	private static int SPACE_BUTTON = getSpaceKey();

	public static String INDICATOR = "\u00bb";

	Container choicesContainer;

	private int numberOfMatches;

	private boolean isInChoice;

	private int choicesYOffsetAdjustment;

	private boolean isOpen;

	Style choiceItemStyle;

	int choiceOrientation;

	private int predictiveType = TRIE;

	private TextBuilder builder = null;

	private int elementX = 0;

	private int elementY = 0;

	private boolean refreshChoices = true;

	private boolean predictiveInput;
	
	private String[] words;
	
	private ArrayList results;
	
	public void init(TextField parent) {
		this.parent = parent;

		initPredictiveInput(null);

		this.choicesContainer = new Container(false);

		if(this.builder != null)
		{
			this.parent.setInputMode(this.builder.getMode());
		}
	}

	public void initPredictiveInput(String[] words) {
		if(words != null)
		{
			this.builder = new ArrayTextBuilder(this.parent.getMaxSize());
			
			this.parent.removeCommand(ENABLE_PREDICTIVE_CMD);
			this.parent.removeCommand(INSTALL_PREDICTIVE_CMD);
			this.parent.removeCommand(INSTALL_PREDICTIVE_CMD);
			
			this.parent.addCommand(DISABLE_PREDICTIVE_CMD);

			this.parent.predictiveInput = true;
			this.predictiveType = ARRAY;
		}
		else
		{
			try {
				if (!PROVIDER.isInit()) {
					PROVIDER.init();
				}

				this.parent.addCommand(ADD_WORD_CMD);
	
				this.parent.removeCommand(ENABLE_PREDICTIVE_CMD);
				this.parent.removeCommand(INSTALL_PREDICTIVE_CMD);
	
				this.parent.addCommand(DISABLE_PREDICTIVE_CMD);
	
				this.parent.predictiveInput = true;
				this.predictiveType = TRIE;
								
				this.builder = new TrieTextBuilder(this.parent.getMaxSize());
	
			} catch (RecordStoreException e) {
				this.parent.removeCommand(DISABLE_PREDICTIVE_CMD);
				this.parent.addCommand(INSTALL_PREDICTIVE_CMD);
				this.parent.predictiveInput = false;
			} catch (Exception e) {
				//#debug error
				System.out.println("unable to load predictive dictionary " + e);
			}
		}
		
		this.words = words;
	}

	public static int getSpaceKey() {
		if (TextField.charactersKeyPound != null)
			if (TextField.charactersKeyPound.charAt(0) == ' ')
				return Canvas.KEY_POUND;

		if (TextField.charactersKeyStar != null)
			if (TextField.charactersKeyStar.charAt(0) == ' ')
				return Canvas.KEY_STAR;

		if (TextField.charactersKey0 != null)
			if (TextField.charactersKey0.charAt(0) == ' ')
				return Canvas.KEY_NUM0;

		return -1;
	}

	private void disablePredictive() {
		this.predictiveInput = false;
		this.parent.predictiveInput = false;

		this.parent.setText(this.builder.getText().toString());
		this.parent.setCaretPosition(this.builder.getCaretPosition());

		this.parent.updateInfo();

		openChoices(false);
	}

	private void enablePredictive() {
		this.predictiveInput = true;
		this.parent.predictiveInput = true;

		synchronize();
	}

	public void synchronize() {
		openChoices(false);

		while (this.builder.deleteCurrent());

		String text = this.parent.getText();

		if (text != null) {
			String[] elements = TextUtil.split(this.parent.getText(), ' ');

			for (int i = 0; i < elements.length; i++) {
				if (elements[i].length() > 0) {
					this.builder.addString(elements[i]);
					this.builder.addString(" ");
				}
			}

			if (this.parent.inputMode == TextField.MODE_NUMBERS) {
				this.parent.setInputMode(TextField.MODE_LOWERCASE);
				this.builder.setMode(TextField.MODE_LOWERCASE);
			}

			this.parent.updateInfo();

			this.builder.setCurrentElementNear(this.parent.getCaretPosition());
		}
	}

	
	private void setChoices(TextElement element) {
		this.choicesContainer.clear();
		if (element != null && element.getResults() != null
				&& element.getCustomResults() != null) {
			ArrayList trieResults = element.getResults();
			ArrayList customResults = element.getCustomResults();

			if (trieResults.size() == 0 && customResults.size() == 0) {
				openChoices(false);
				return;
			}

			this.numberOfMatches = trieResults.size() + customResults.size();

			for (int i = 0; i < trieResults.size(); i++) {
				String choiceText = (String)trieResults.get(i);
				Item item = new ChoiceItem(choiceText, null, Choice.IMPLICIT,
						this.choiceItemStyle);

				if (this.choiceOrientation == ORIENTATION_BOTTOM) {
					this.choicesContainer.add(item);
				} else {
					this.choicesContainer.add(0, item);
				}
			}

			for (int i = 0; i < customResults.size(); i++) {
				String choiceText = (String)customResults.get(i);
				Item item = new ChoiceItem(choiceText, null, Choice.IMPLICIT,
						this.choiceItemStyle);

				if (this.choiceOrientation == ORIENTATION_BOTTOM) {
					this.choicesContainer.add(item);
				} else {
					this.choicesContainer.add(0, item);
				}
			}
			if (!this.isOpen && this.predictiveType != ARRAY)
			{
				openChoices(this.numberOfMatches > 0);
			}
			
			this.results = element.getResults();
			
		} else
			openChoices(false);
	}

	void openChoices(boolean open) {
		//#debug
		System.out.println("open choices: " + open
				+ ", have been opened already:" + this.isOpen);
		this.choicesContainer.focus(-1);
		if (open) {
			if (this.parent.getParent() instanceof Container) {
				Container parentContainer = (Container) this.parent.getParent();
				if (parentContainer.enableScrolling) {
					int availableWidth = this.parent.itemWidth
							- (this.parent.marginLeft + this.parent.marginRight);
					int choicesHeight = this.choicesContainer.getItemHeight(
							availableWidth, availableWidth);
					int choicesBottomY = this.parent.contentY
							+ this.parent.contentHeight
							+ this.parent.paddingVertical + choicesHeight;
					//#debug
					System.out.println("choicesHeight " + choicesHeight
							+ ", choicesBottom=" + choicesBottomY
							+ ", parent.height="
							+ parentContainer.availableHeight);
					int parentYOffset = parentContainer.getScrollYOffset();
					int overlap = choicesBottomY
							- (parentContainer.getContentScrollHeight() - (this.parent.relativeY + parentYOffset));
					// System.out.println("overlap=" + overlap );
					if (overlap > 0) {
						// try to scroll up this item, so that the user sees all
						// matches:
						int yOffsetAdjustment = Math.min(this.parent.relativeY
								+ parentYOffset, overlap);
						this.choicesYOffsetAdjustment = yOffsetAdjustment;
						//#debug
						System.out.println("Adjusting yOffset of parent by "
								+ yOffsetAdjustment);
						parentContainer.setScrollYOffset(parentYOffset
								- yOffsetAdjustment, true);
						// System.out.println("choice.itemHeight=" +
						// this.choicesContainer.itemHeight + ",
						// parentContainer.availableHeight=" +
						// parentContainer.availableHeight + ", (this.contentY +
						// this.contentHeight + this.paddingVertical)=" +
						// (this.contentY + this.contentHeight +
						// this.paddingVertical) + ", children.relativeY=" +
						// this.choicesContainer.relativeY );
						// TODO this needs some finetuning!
						int itHeight = this.parent.itemHeight;
						int ctHeight = this.parent.contentY
								+ this.parent.contentHeight
								+ this.parent.paddingVertical;
						int max = Math.max(itHeight, ctHeight);

						this.choicesContainer.setScrollHeight(parentContainer
								.getContentScrollHeight()
								- max);
					} else {
						this.choicesYOffsetAdjustment = 0;
					}
				}
			}
		} else {
			this.choicesContainer.clear();
			if (this.choicesYOffsetAdjustment != 0
					&& this.parent.getParent() instanceof Container) {
				Container parentContainer = (Container) this.parent.getParent();
				parentContainer.setScrollYOffset(parentContainer
						.getScrollYOffset()
						+ this.choicesYOffsetAdjustment, true);
				this.choicesYOffsetAdjustment = 0;
			}
		}
		this.isOpen = open;
		this.refreshChoices = open;
	}

	private void enterChoices(boolean enter) {
		//#debug
		System.out.println("enter choices: " + enter
				+ ", have been entered already: " + this.isInChoice);
		if (enter) {

			if (this.choiceOrientation == ORIENTATION_BOTTOM)
				this.choicesContainer.focus(0);
			else
				this.choicesContainer.focus(this.choicesContainer.size() - 1);

			//#if polish.usePolishGui && !polish.LibraryBuild
			this.parent.showCaret = false;
			if (!this.isInChoice) {
				this.parent.getScreen().removeItemCommands( this.parent );
			}
			//#endif
			//#if polish.blackberry
			PolishEditField field = (PolishEditField) this.parent._bbField;
			field.processKeyEvents = false;
			//#endif
		} else {

			this.parent.showCaret = true;
			this.choicesContainer.yOffset = 0;
			this.choicesContainer.targetYOffset = 0;
			// move focus to TextField input again
			if (this.isInChoice) {
				//#if polish.usePolishGui && !polish.LibraryBuild
				this.parent.showCommands();
				//#endif
			}
			//#if polish.blackberry
			PolishEditField field = (PolishEditField) this.parent._bbField;
			field.processKeyEvents = true;
			//#endif
		}
		this.isInChoice = enter;
	}

	protected int getChoicesY(int paddingVertical, int borderWidth) {
		if (this.choiceOrientation == ORIENTATION_BOTTOM) {
			int resultY = (this.parent.contentHeight / this.parent.textLines.length)
					* (this.builder.getElementLine(this.parent.textLines) + 1);

			return (resultY + paddingVertical + borderWidth);
		} else {
			int resultY = (this.parent.contentHeight / this.parent.textLines.length)
					* (this.builder.getElementLine(this.parent.textLines));

			return (resultY - this.choicesContainer.itemHeight
					- paddingVertical - borderWidth);
		}
	}

	protected int getChoicesX(int leftBorder, int rightBorder, int itemWidth) {
		if (this.builder.getAlign() == TrieTextBuilder.ALIGN_FOCUS) {
			int line = this.builder.getElementLine(this.parent.textLines);
			int charsToLine = 0;

			for (int i = 0; i < line; i++)
				charsToLine += this.parent.textLines[i].length() + 1;

			TextElement element = this.builder.getTextElement();

			if (element != null) {
				int result = 0;

				int elementStart = this.builder.getCaret()
						- element.getLength();

				StringBuffer stringToLine = new StringBuffer();
				for (int i = charsToLine; i < elementStart; i++) {
					stringToLine.append(this.builder.getTextChar(i));
				}

				result += this.parent.stringWidth(stringToLine.toString());

				int overlap = (rightBorder) - (leftBorder + result + itemWidth);

				if (overlap < 0)
					result += overlap;

				return result;
			}
		}

		return 0;
	}

	protected void showWordNotFound() {
		Alert alert = new Alert(Locale.get("polish.predictive.title"));
		alert.setString(Locale.get("polish.predictive.wordNotFound"));
		alert.setTimeout(2000);
		StyleSheet.display.setCurrent(alert);
	}

	protected boolean keyInsert(int keyCode, int gameAction) {
		if ((keyCode >= Canvas.KEY_NUM0 && keyCode <= Canvas.KEY_NUM9)
				|| keyCode == Canvas.KEY_POUND || keyCode == Canvas.KEY_STAR) {
			if (this.isInChoice) {
				enterChoices(false);
				openChoices(false);
			}

			try {
				if (keyCode != SPACE_BUTTON) {
					if (this.predictiveType == TRIE) {
						this.builder.keyNum(keyCode, new TrieReader());
					} else {
						ArrayReader reader = new ArrayReader();
						reader.setWords(this.words);
						this.builder.keyNum(keyCode, reader);
					}

					this.parent.setInputMode(this.builder.getMode());
					this.parent.updateInfo();

					if (!this.builder.getTextElement().isWordFound())
					{
						if(this.predictiveType != ARRAY)
						{
							showWordNotFound();
						}
					}
					else {
						if (this.builder.getAlign() == TrieTextBuilder.ALIGN_FOCUS)
							this.setChoices(this.builder.getTextElement());
						else
							this.openChoices(false);
					}
				} else {
					this.builder.keySpace();
					openChoices(false);
				}

			} catch (Exception e) {
				//#debug error
				e.printStackTrace();
			}

			this.parent.setText(this.builder.getText().toString());
			this.parent.setCaretPosition(this.builder.getCaretPosition());
			this.parent.showCommands(); //getScreen().setItemCommands(this.parent);
			this.parent.notifyStateChanged();
			this.refreshChoices = true;

			return true;
		}

		return false;
	}

	protected boolean keyClear(int keyCode, int gameAction) {
		if (this.isInChoice) {
			enterChoices(false);
			openChoices(false);
		}

		try {
			this.builder.keyClear();

			if (!this.builder.isString(0)
					&& this.builder.getAlign() == TrieTextBuilder.ALIGN_FOCUS) {
				this.setChoices(this.builder.getTextElement());
			} else {
				this.openChoices(false);
			}
		} catch (RecordStoreException e) {
			//#debug error
			System.out.println("unable to load record store " + e);
		}

		this.parent.setText(this.builder.getText().toString());
		this.parent.setCaretPosition(this.builder.getCaretPosition());
		this.parent.showCommands();//this.parent.getScreen().setItemCommands(this.parent);
		this.refreshChoices = true;
		this.parent.notifyStateChanged();
		return true;
	}

	protected boolean keyMode(int keyCode, int gameAction) {

		//#if polish.key.ChangeNumericalAlphaInputModeKey:defined
		//#= if ( keyCode == ${polish.key.ChangeNumericalAlphaInputModeKey}
		//#else
		if (keyCode == TextField.KEY_CHANGE_MODE
		//#endif
		//#if polish.key.shift:defined
		//#= || keyCode == ${polish.key.shift}
		//#endif
		) {
			if (this.isInChoice) {
				enterChoices(false);
				openChoices(false);
			}

			this.parent.setInputMode((this.parent.getInputMode() + 1) % 3);
			this.builder.setMode(this.parent.getInputMode());

			this.parent.updateInfo();

			this.refreshChoices = true;
			this.parent.showCommands();//this.parent.getScreen().setItemCommands(this.parent);
			return true;
		}

		return false;
	}

	protected boolean keyNavigation(int keyCode, int gameAction) {
		if (this.isInChoice) {
			if ( this.choicesContainer.handleKeyPressed(keyCode, gameAction) ) {
				//#debug
				System.out.println("keyPressed handled by choices container");
				
				this.refreshChoices = true;
				return true;
			}
			// System.out.println("focusing textfield again, isFocused=" + this.isFocused);
			enterChoices( false );
			
			if (gameAction == Canvas.FIRE || gameAction == Canvas.RIGHT) {
				// option has been selected!
				if(!this.builder.isString(0))
				{
					if(this.choiceOrientation == ORIENTATION_BOTTOM)
					{
						this.builder.getTextElement().setSelectedWordIndex(this.choicesContainer.getFocusedIndex());
					}
					else
					{
						int index = (this.choicesContainer.size() - 1) - this.choicesContainer.getFocusedIndex();
						this.builder.getTextElement().setSelectedWordIndex(index);
					}
					
					this.builder.getTextElement().convertReader();
					this.builder.setAlign(TrieTextBuilder.ALIGN_RIGHT);
					
					openChoices( false );
					this.parent.notifyStateChanged();
					
					this.parent.setText(this.builder.getText().toString()); 
					this.parent.setCaretPosition(this.builder.getCaretPosition());
					this.refreshChoices = true;
				}
			}
			
			this.parent.notifyStateChanged();
			return true;
		}
		if ( (gameAction == Canvas.DOWN && keyCode != Canvas.KEY_NUM8)
				&& this.builder.getAlign() == TrieTextBuilder.ALIGN_FOCUS 
				&& this.choiceOrientation == ORIENTATION_BOTTOM)
		{
			if(!this.builder.isString(0))
			{
				this.setChoices(this.builder.getTextElement());
				
				if(this.numberOfMatches > 0)
					enterChoices( true );	
			}
			
			this.refreshChoices = true;
			this.parent.notifyStateChanged();
			return true;
		}
		else if ( (gameAction == Canvas.UP && keyCode != Canvas.KEY_NUM8)
				&& this.builder.getAlign() == TrieTextBuilder.ALIGN_FOCUS 
				&& this.choiceOrientation == ORIENTATION_TOP)
		{
			if(!this.builder.isString(0))
			{
				this.setChoices(this.builder.getTextElement());
				
				if(this.numberOfMatches > 0)
					enterChoices( true );	
			}
			
			this.refreshChoices = true;
			this.parent.notifyStateChanged();
			return true;
		}
		else if ( gameAction == Canvas.LEFT || gameAction == Canvas.RIGHT )
		{
			if(gameAction == Canvas.LEFT)
				this.builder.decreaseCaret();
			else if(gameAction == Canvas.RIGHT)
				this.builder.increaseCaret();
			
			if(this.builder.getAlign() == TrieTextBuilder.ALIGN_FOCUS)
			{
				this.setChoices(this.builder.getTextElement());
			}
			else
				openChoices(false);
			
			this.parent.setCaretPosition(this.builder.getCaretPosition());
		
			this.parent.notifyStateChanged();
			return true;
		}
		else if ( gameAction == Canvas.UP && !this.isInChoice)
		{
			int lineCaret = this.builder.getJumpPosition(TrieTextBuilder.JUMP_PREV, this.parent.textLines);
			
			if(lineCaret != -1)
			{
				this.builder.setCurrentElementNear(lineCaret);
				
				this.parent.setCaretPosition(this.builder.getCaretPosition());
								
				openChoices(false);

				this.parent.notifyStateChanged();
				return true;
			}
			
			this.parent.notifyStateChanged();
			return false;
		}
		else if ( gameAction == Canvas.DOWN && !this.isInChoice)
		{
			int lineCaret = this.builder.getJumpPosition(TrieTextBuilder.JUMP_NEXT, this.parent.textLines);
			
			if(lineCaret != -1)
			{
				this.builder.setCurrentElementNear(lineCaret);
				
				this.parent.setCaretPosition(this.builder.getCaretPosition());
								
				openChoices(false);
				
				this.parent.notifyStateChanged();
				return true;
			}
			
			this.parent.notifyStateChanged();
			return false;
		}
		else if (gameAction == Canvas.FIRE && keyCode != Canvas.KEY_NUM5) {
			
			openChoices( false );
			if(!this.builder.isString(0))
			{
				this.builder.setAlign(TrieTextBuilder.ALIGN_RIGHT);
				if(this.builder.getTextElement().isSelectedCustom())
					this.builder.getTextElement().convertReader();
			}
			
			this.parent.notifyStateChanged();
			return true;
		}
		
		this.parent.notifyStateChanged();
		return false;
	}

	public void paintChoices(int x, int y, int leftBorder, int rightBorder,
			Graphics g) {
		if (this.numberOfMatches > 0 && this.isOpen) {
			if (this.refreshChoices) {
				this.elementX = getChoicesX(leftBorder, rightBorder,
						this.choicesContainer.getItemWidth(
								this.parent.itemWidth, this.parent.itemWidth));
				this.elementY = getChoicesY(this.parent.paddingVertical,
						this.parent.borderWidth);
				this.refreshChoices = false;
			}
			this.choicesContainer.paint(x + this.elementX, y + this.elementY,
					leftBorder + this.elementX, rightBorder, g);
		}
	}

	public void animateChoices() {
		if (this.numberOfMatches > 0) {
			this.choicesContainer.animate();
		}
	}

	public boolean commandAction(Command cmd, Displayable box) {
		//#if tmp.supportsSymbolEntry
		if (box instanceof List) {
			if (cmd != StyleSheet.CANCEL_CMD) {
				int index = TextField.symbolsList.getSelectedIndex();

				this.builder.addString(TextField.definedSymbols[index]);

				this.parent.setText(this.builder.getText().toString());
				this.parent.setCaretPosition(this.builder.getCaretPosition());

				// insertCharacter( definedSymbols.charAt(index), true, true );
				StyleSheet.currentScreen = this.parent.screen;
				//#if tmp.updateDeleteCommand
				this.parent.updateDeleteCommand(this.parent.text);
				//#endif
			} else {
				StyleSheet.currentScreen = this.parent.screen;
			}
			StyleSheet.display.setCurrent(this.parent.screen);
			this.parent.notifyStateChanged();
			return true;
		} else
		//#endif
		if (box instanceof Form) {
			if (cmd == StyleSheet.OK_CMD) {
				this.builder.addWord(PROVIDER.getCustomField().getText());
			}
			StyleSheet.display.setCurrent(this.parent.getScreen());
			return true;
		} else if (box instanceof Alert) { // this is the installation dialog
											// for predictive text:
			if (cmd == StyleSheet.OK_CMD) {
				//#if polish.predictive.useLocalRMS
				DataInputStream stream = null;
				TrieSetup setup = null;

				stream = new DataInputStream(getClass().getResourceAsStream(
						"/predictive.trie"));
				setup = new TrieSetup(StyleSheet.midlet, this.parent.getScreen(),
						this, true, stream);

				Thread thread = new Thread(setup);
				thread.start();

				//#else
				try {
					StyleSheet.midlet
							.platformRequest("http://dl.j2mepolish.org/predictive/index.jsp?type=shared");
					StyleSheet.midlet.notifyDestroyed();
				} catch (ConnectionNotFoundException e) {
					//#debug error
					System.out.println("Unable to load dictionary app" + e);
				}
				StyleSheet.display.setCurrent(this.parent.getScreen());
				//#endif
				return true;
			}
		}

		return false;
	}

	public boolean commandAction(Command cmd, Item item) {
		if (cmd == TextField.CLEAR_CMD) {
			while (this.builder.deleteCurrent());
			openChoices(false);

			this.parent.setString(null);
			this.parent.notifyStateChanged();
			return true;
		} else if (cmd == INSTALL_PREDICTIVE_CMD) {
			showPredictiveInstallDialog();
			return true;
		} else if (cmd == ENABLE_PREDICTIVE_CMD) {
			try {
				if (!PROVIDER.isInit()) {
					PROVIDER.init();
				}

				this.predictiveInput = true;
			} catch (RecordStoreException e)
			//#if (polish.Bugs.sharedRmsRequiresSigning || polish.predictive.useLocalRMS)
			{
				DataInputStream stream = null;
				TrieSetup setup = null;
				//#if !polish.predictive.useLocalRMS && polish.Bugs.sharedRmsRequiresSigning
				RedirectHttpConnection connection = new RedirectHttpConnection(
						"http://dl.j2mepolish.org/predictive/index.jsp?type=local&lang=en");
				try {
					stream = connection.openDataInputStream();
				} catch (IOException ioEx) {
					ioEx.printStackTrace();
				}

				setup = new TrieSetup(StyleSheet.midlet, null, null, false,
						stream);
				//#else
				stream = new DataInputStream(getClass().getResourceAsStream(
						"/predictive.trie"));
				setup = new TrieSetup(StyleSheet.midlet, this.parent.getScreen(),
						this, true, stream);
				//#endif

				Thread thread = new Thread(setup);
				thread.start();
				//# return true;
			}
			//#else
			{
				showPredictiveInstallDialog();
				//# return true;
			}
			//#endif

			if (!this.predictiveInput) {
				return true;
			} else {
				enablePredictive();
			}

			this.parent.removeCommand(ENABLE_PREDICTIVE_CMD);
			this.parent.addCommand(DISABLE_PREDICTIVE_CMD);
			this.parent.addCommand(ADD_WORD_CMD);
			return true;
		} else if (cmd == DISABLE_PREDICTIVE_CMD) {
			disablePredictive();

			this.parent.removeCommand(DISABLE_PREDICTIVE_CMD);
			this.parent.removeCommand(ADD_WORD_CMD);
			this.parent.addCommand(ENABLE_PREDICTIVE_CMD);
			return true;
		} else if (cmd == ADD_WORD_CMD) {
			if (PROVIDER.getCustomField() == null) {
				TextField field = new TextField(Locale.get("polish.predictive.registerNewWord.label"), "",50, TextField.ANY);
				field.disablePredictiveInput();
				PROVIDER.setCustomField(field);
				PROVIDER.getCustomForm().append(PROVIDER.getCustomField());
			} else {
				PROVIDER.getCustomField().setText("");
			}
			PROVIDER.getCustomForm().setCommandListener(this.parent);
			StyleSheet.display.setCurrent(PROVIDER.getCustomForm());
			return true;
		}

		return false;
	}

	private void showPredictiveInstallDialog() {
		//#if polish.predictive.useLocalRMS
		//#style predictiveInstallDialog?
		Alert predictiveDowload = new Alert(Locale.get("polish.predictive.local.title"));
		predictiveDowload.setString(Locale.get("polish.predictive.local.message"));
		//#else
		//#style predictiveInstallDialog?
		//# Alert predictiveDowload = new Alert(Locale.get("polish.predictive.download.title"));
		//# predictiveDowload.setString(Locale.get("polish.predictive.download.message") );
		//#endif

		predictiveDowload.addCommand(StyleSheet.CANCEL_CMD);
		predictiveDowload.addCommand(StyleSheet.OK_CMD);

		predictiveDowload.setCommandListener(this.parent);

		StyleSheet.display.setCurrent(predictiveDowload);
	}

	public TextBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(TextBuilder builder) {
		this.builder = builder;
	}

	public int getPredictiveType() {
		return predictiveType;
	}

	public void setPredictiveType(int predictiveType) {
		this.predictiveType = predictiveType;
	}

	public Container getChoicesContainer() {
		return choicesContainer;
	}

	public void setChoicesContainer(Container choicesContainer) {
		this.choicesContainer = choicesContainer;
	}
	
	public ArrayList getResults()
	{
		return this.results;
	}
}
