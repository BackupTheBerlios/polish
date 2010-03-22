package de.enough.polish.api.test.impl;

import de.enough.polish.api.test.ApiTestCase;
import de.enough.polish.content.ContentDescriptor;
import de.enough.polish.content.ContentLoader;
import de.enough.polish.content.source.impl.HttpContentSource;
import de.enough.polish.content.source.impl.RMSContentStorage;
import de.enough.polish.content.storage.StorageIndex;

public class ApiTest extends ApiTestCase {

	public static String EXAMPLE_URL = "http://www.j2mepolish.org/cms/fileadmin/styles/images/logo-small.png";
	
	HttpContentSource http;
	RMSContentStorage rms;
	ContentLoader loader;
	ContentDescriptor descriptor;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		//TODO add rms testing
		this.http = new HttpContentSource("http");
		this.loader = new ContentLoader(this.http);
		this.descriptor = new ContentDescriptor(EXAMPLE_URL);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testApi() 
		throws Exception
	{
		httpLoadContent();
		
		sweepLoader();
		
		shutdown();
	}
	
	public void httpLoadContent() throws Exception {
		this.descriptor = new ContentDescriptor(EXAMPLE_URL);
		
		byte[] data = (byte[])this.loader.loadContent(descriptor);
		
		assertNotNull(data);
		assertEquals(data.length,2550);
	}
	
	public void sweepLoader() throws Exception {
		StorageIndex index = this.loader.getStorageIndex();
		
		assertEquals(index.getCacheSize(),2550);
		
		this.loader.sweep();
		
		assertEquals(index.getCacheSize(),0);
	}
	
	public void shutdown() {
		this.loader.shutdown();
	}
}
