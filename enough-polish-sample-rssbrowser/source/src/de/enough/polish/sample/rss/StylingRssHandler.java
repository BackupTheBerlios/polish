package de.enough.polish.sample.rss;

import java.io.IOException;

import de.enough.polish.browser.rss.RssBrowser;
import de.enough.polish.browser.rss.RssTagHandler;
import de.enough.polish.io.RmsStorage;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ArrayList;

public class StylingRssHandler extends RssTagHandler {
	
	private ArrayList visitedUrls;
	private RmsStorage urlsStorage;
	private boolean visitedUrlsReset;

	/**
	 * Creates a new RSS handler
	 * @param browser the original RSS browser
	 */
	public StylingRssHandler(RssBrowser browser) {
		super(browser.getLinkCommand(), browser.getRssItemCommandListener());
		try {
			this.urlsStorage = new RmsStorage(null);
			this.visitedUrls = (ArrayList) this.urlsStorage.read("_urls");
		} catch (Exception e) {
			//#debug info
			System.out.println("Unable to load urls, probably first start" + e);
		}
		if (this.visitedUrls == null) {
			this.visitedUrls = new ArrayList();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.browser.rss.RssTagHandler#applyStylingForRssLink(de.enough.polish.ui.Item, int, java.lang.String)
	 */
	protected void applyStylingForRssLink(Item item, int index, String rssUrl) {
		// use a gray background for every second item:
		if ((index & 1) == 1) {
			//#style browserLinkOdd
			UiAccess.setStyle(item);
		}
		//#if polish.css.visited-style
			if (this.visitedUrls.contains(rssUrl) && item.getStyle() != null) {
				Style visitedStyle = (Style) item.getStyle().getObjectProperty("visited-style");
				if (visitedStyle != null) {
					UiAccess.setStyle(item, visitedStyle);
				}
			}
		//#endif
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.browser.rss.RssTagHandler#onViewUrl(java.lang.String)
	 */
	protected void onViewUrl(String rssUrl) {
		if (!this.visitedUrlsReset) {
			this.visitedUrlsReset = true;
			if (this.visitedUrls.size() > 30) {
				// clear up old entries:
				this.visitedUrls.clear();
			}
		}
		//#debug
		System.out.println("adding URL " + rssUrl);
		this.visitedUrls.add(rssUrl);
		super.onViewUrl(rssUrl);
	}

	/**
	 * Retrieves the URLs that have been visited during this run of the application
	 * @return a list of URL, may be empty but not null
	 */
	public ArrayList getVisitedUrls() {
		return this.visitedUrls;
	}
	
	/**
	 * Saves the visited URLs upon application exit.
	 * @throws IOException when saving fails
	 */
	public void saveVisitedUrls() throws IOException {
		this.urlsStorage.save(this.visitedUrls, "_urls");
	}
	

}
