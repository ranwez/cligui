package _cli;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.junit.Test;

import cli.CLI_bundle;

public class CLI_bundleTest
{
	@Test
	public void initBundle() throws Exception
	{
		Constructor<?> constructor = CLI_bundle.class.getDeclaredConstructor();

		constructor.setAccessible(true);

		constructor.newInstance();
	}

	@Test
	public void readProperties_fileNotFound() throws Exception
	{
		Class<?> cls = CLI_bundle.class;

		Method method = cls.getDeclaredMethod("readProperties", String.class);

		method.setAccessible(true);

		method.invoke(null, "wrongBundlePath");
	}
}
