package de.enough.polish.browser.rss;

public class RssItem
{
	private String title;
	private String description;
	private String link;
	
	public RssItem(String title, String description, String link)
	{
		this.title = title;
		this.description = description;
		this.link = link;
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getLink()
	{
		return this.link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}
}
