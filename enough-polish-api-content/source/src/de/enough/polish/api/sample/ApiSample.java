package de.enough.polish.api.sample;

import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

import de.enough.polish.content.ContentDescriptor;
import de.enough.polish.content.ContentException;
import de.enough.polish.content.ContentListener;
import de.enough.polish.content.ContentLoader;
import de.enough.polish.content.source.impl.HttpContentSource;
import de.enough.polish.content.source.impl.RMSContentStorage;
import de.enough.polish.content.source.impl.RMSStorageIndex;
import de.enough.polish.content.transform.impl.ImageContentTransform;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Form;

public class ApiSample extends MIDlet implements ContentListener {

	Form form;
	
	ContentLoader loader;
	
    protected void destroyApp(boolean arg0) {}

    protected void pauseApp() {}

    protected void startApp(){
    	this.form = new Form(null);
    	Display.getDisplay(this).setCurrent(this.form);
    	
    	// prepare
    	HttpContentSource httpContentSource = new HttpContentSource("http");
    	RMSContentStorage rmsContentStorage = new RMSContentStorage("storage", new RMSStorageIndex(1000000));
    	this.loader = new ContentLoader();
    	
    	// create source hierachy
    	rmsContentStorage.attachSource(httpContentSource);
    	this.loader.attachSource(rmsContentStorage);
    	
    	// add a image transformation to the loader
    	this.loader.addContentTransform(new ImageContentTransform());
    	
    	// create the content descriptor
    	String url = "http://www.j2mepolish.org/cms/fileadmin/styles/images/logo-small.png";
    	ContentDescriptor descriptor = new ContentDescriptor(url,ImageContentTransform.ID);
    	
    	// load the content directly and append it to the form
    	try {
			Image image = (Image)this.loader.loadContent(descriptor);
			this.form.append(image);
		} catch (ContentException e) {
			e.printStackTrace();
		}

		// request the content and load it asynchronously
		this.loader.requestContent(descriptor, this);
    }

    public void onContentLoaded(ContentDescriptor descriptor, Object data) {
    	// append the loaded content to the form
    	Image image = (Image)data;
    	this.form.append(image);

    	// shutdown the loader after all work is one
    	this.loader.shutdown();
    }
    
	public void onContentError(ContentDescriptor descriptor, Exception exception) {
		exception.printStackTrace();
	}
}

