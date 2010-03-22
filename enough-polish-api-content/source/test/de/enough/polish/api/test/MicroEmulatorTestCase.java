package de.enough.polish.api.test;

import org.microemu.microedition.ImplFactory;
import org.microemu.microedition.io.ConnectorImpl;

import junit.framework.TestCase;

public abstract class MicroEmulatorTestCase
		extends TestCase
{
	static
	{
		ImplFactory.registerGCF(ImplFactory.DEFAULT, new ConnectorImpl());
	}
}
