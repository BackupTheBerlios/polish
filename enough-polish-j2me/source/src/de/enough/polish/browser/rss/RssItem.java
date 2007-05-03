package de.enough.polish.browser.rss;

public class RssItem
{
	private String title;
	private String description;
	
	public RssItem(String title, String description)
	{
		this.title = title;
		this.description = description;
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
}
