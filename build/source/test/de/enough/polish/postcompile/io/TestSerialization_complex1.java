package de.enough.polish.postcompile.io;

import de.enough.polish.io.Serializable;

public class TestSerialization_complex1
	implements Serializable
{
	private final int rgb;
  private int[] buffer;
  private int width;
  
  public TestSerialization_complex1()
  {
    this.rgb = 1;
  }
}
