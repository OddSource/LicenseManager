/*
 * TestConsoleLicenseGenerator.java from LicenseManager modified Monday, May 21, 2012 22:09:11 CDT (-0500).
 *
 * Copyright 2010-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.nicholaswilliams.java.licensing.licensor.interfaces.text;

import net.nicholaswilliams.java.licensing.License;
import net.nicholaswilliams.java.licensing.MockLicenseHelper;
import net.nicholaswilliams.java.licensing.ObjectSerializer;
import net.nicholaswilliams.java.licensing.SignedLicense;
import net.nicholaswilliams.java.licensing.encryption.Encryptor;
import net.nicholaswilliams.java.licensing.encryption.PasswordProvider;
import net.nicholaswilliams.java.licensing.exception.AlgorithmNotSupportedException;
import net.nicholaswilliams.java.licensing.exception.InappropriateKeyException;
import net.nicholaswilliams.java.licensing.exception.InappropriateKeySpecificationException;
import net.nicholaswilliams.java.licensing.exception.KeyNotFoundException;
import net.nicholaswilliams.java.licensing.exception.ObjectSerializationException;
import net.nicholaswilliams.java.licensing.licensor.FilePrivateKeyDataProvider;
import net.nicholaswilliams.java.licensing.licensor.LicenseCreator;
import net.nicholaswilliams.java.licensing.licensor.LicenseCreatorProperties;
import net.nicholaswilliams.java.licensing.licensor.PrivateKeyDataProvider;
import net.nicholaswilliams.java.licensing.licensor.interfaces.text.abstraction.TextInterfaceDevice;
import net.nicholaswilliams.java.licensing.samples.SampleEmbeddedPrivateKeyDataProvider;
import net.nicholaswilliams.java.licensing.samples.SampleFilePrivateKeyDataProvider;
import net.nicholaswilliams.java.licensing.samples.SamplePasswordProvider;
import net.nicholaswilliams.java.mock.MockPermissiveSecurityManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Test class for ConsoleLicenseGenerator.
 */
public class TestConsoleLicenseGenerator
{
	private static final String LF = System.getProperty("line.separator");

	private ConsoleLicenseGenerator console;

	private TextInterfaceDevice device;

	@Before
	public void setUp()
	{
		this.device = EasyMock.createMock(TextInterfaceDevice.class);

		this.console = new ConsoleLicenseGenerator(this.device, new GnuParser()) {
			@Override
			protected void finalize()
			{

			}
		};
	}

	@After
	public void tearDown()
	{
		EasyMock.verify(this.device);
	}

	@Test
	public void testProcessCommandLineOptions01() throws ParseException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		PrintStream printer = new PrintStream(stream);

		EasyMock.expect(this.device.out()).andReturn(printer);
		this.device.exit(0);
		EasyMock.expectLastCall();

		EasyMock.replay(this.device);

		this.console.processCommandLineOptions(new String[] { "-help" });

		String output = stream.toString();

		assertNotNull("There should be output.", output);
		assertTrue("The output should have length.", output.length() > 0);
		assertEquals("The output is not correct.",
					 "usage:  ConsoleLicenseGenerator -help" + LF +
					 "        ConsoleLicenseGenerator" + LF +
					 "        ConsoleLicenseGenerator -config <file>" + LF +
					 "        ConsoleLicenseGenerator -license <file>" + LF +
					 "        ConsoleLicenseGenerator -config <file> -license <file>" + LF +
					 "        The ConsoleLicenseGenerator expects to be passed the path to two properties files, or one of" + LF +
					 "        them, or neither. The \"config\" properties file contains information necessary to generate all" + LF +
					 "        licenses (key paths, passwords, etc.) and generally will not need to change. The \"license\"" + LF +
					 "        properties file contains all of the information you need to generate this particular license. See" + LF +
					 "        the Javadoc API documentation for information about the contents of these two files." + LF +
					 "        If you do not specify the \"config\" properties file, you will be prompted to provide the values" + LF +
					 "        that were expected in that file. Likewise, if you do not specify the \"license\" properties file," + LF +
					 "        you will be prompted to provide the values that were expected in that file." + LF +
					 " -config <file>    Specify the .properties file that configures this generator" + LF +
					 " -help             Display this help message" + LF +
					 " -license <file>   Specify the .properties file that contains the data for this license" + LF,
					 output);
	}

	@Test
	public void testProcessCommandLineOptions02() throws ParseException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		PrintStream printer = new PrintStream(stream);

		this.device.printErrLn("Unrecognized option: -badOption");
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.out()).andReturn(printer);
		this.device.exit(1);
		EasyMock.expectLastCall();

		EasyMock.replay(this.device);

		this.console.processCommandLineOptions(new String[] { "-badOption" });

		String output = stream.toString();

		assertNotNull("There should be output.", output);
		assertTrue("The output should have length.", output.length() > 0);
		assertEquals("The output is not correct.",
					 "usage:  ConsoleLicenseGenerator -help" + LF +
					 "        ConsoleLicenseGenerator" + LF +
					 "        ConsoleLicenseGenerator -config <file>" + LF +
					 "        ConsoleLicenseGenerator -license <file>" + LF +
					 "        ConsoleLicenseGenerator -config <file> -license <file>" + LF +
					 "        The ConsoleLicenseGenerator expects to be passed the path to two properties files, or one of" + LF +
					 "        them, or neither. The \"config\" properties file contains information necessary to generate all" + LF +
					 "        licenses (key paths, passwords, etc.) and generally will not need to change. The \"license\"" + LF +
					 "        properties file contains all of the information you need to generate this particular license. See" + LF +
					 "        the Javadoc API documentation for information about the contents of these two files." + LF +
					 "        If you do not specify the \"config\" properties file, you will be prompted to provide the values" + LF +
					 "        that were expected in that file. Likewise, if you do not specify the \"license\" properties file," + LF +
					 "        you will be prompted to provide the values that were expected in that file." + LF +
					 " -config <file>    Specify the .properties file that configures this generator" + LF +
					 " -help             Display this help message" + LF +
					 " -license <file>   Specify the .properties file that contains the data for this license" + LF,
					 output);
	}

	@Test
	public void testProcessCommandLineOptions03() throws ParseException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		PrintStream printer = new PrintStream(stream);

		this.device.printErrLn("Missing argument for option: config");
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.out()).andReturn(printer);
		this.device.exit(1);
		EasyMock.expectLastCall();

		EasyMock.replay(this.device);

		this.console.processCommandLineOptions(new String[] { "-config" });

		String output = stream.toString();

		assertNotNull("There should be output.", output);
		assertTrue("The output should have length.", output.length() > 0);
		assertEquals("The output is not correct.",
					 "usage:  ConsoleLicenseGenerator -help" + LF +
					 "        ConsoleLicenseGenerator" + LF +
					 "        ConsoleLicenseGenerator -config <file>" + LF +
					 "        ConsoleLicenseGenerator -license <file>" + LF +
					 "        ConsoleLicenseGenerator -config <file> -license <file>" + LF +
					 "        The ConsoleLicenseGenerator expects to be passed the path to two properties files, or one of" + LF +
					 "        them, or neither. The \"config\" properties file contains information necessary to generate all" + LF +
					 "        licenses (key paths, passwords, etc.) and generally will not need to change. The \"license\"" + LF +
					 "        properties file contains all of the information you need to generate this particular license. See" + LF +
					 "        the Javadoc API documentation for information about the contents of these two files." + LF +
					 "        If you do not specify the \"config\" properties file, you will be prompted to provide the values" + LF +
					 "        that were expected in that file. Likewise, if you do not specify the \"license\" properties file," + LF +
					 "        you will be prompted to provide the values that were expected in that file." + LF +
					 " -config <file>    Specify the .properties file that configures this generator" + LF +
					 " -help             Display this help message" + LF +
					 " -license <file>   Specify the .properties file that contains the data for this license" + LF,
					 output);
	}

	@Test
	public void testProcessCommandLineOptions04() throws ParseException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		PrintStream printer = new PrintStream(stream);

		this.device.printErrLn("Missing argument for option: license");
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.out()).andReturn(printer);
		this.device.exit(1);
		EasyMock.expectLastCall();

		EasyMock.replay(this.device);

		this.console.processCommandLineOptions(new String[] { "-license" });

		String output = stream.toString();

		assertNotNull("There should be output.", output);
		assertTrue("The output should have length.", output.length() > 0);
		assertEquals("The output is not correct.",
					 "usage:  ConsoleLicenseGenerator -help" + LF +
					 "        ConsoleLicenseGenerator" + LF +
					 "        ConsoleLicenseGenerator -config <file>" + LF +
					 "        ConsoleLicenseGenerator -license <file>" + LF +
					 "        ConsoleLicenseGenerator -config <file> -license <file>" + LF +
					 "        The ConsoleLicenseGenerator expects to be passed the path to two properties files, or one of" + LF +
					 "        them, or neither. The \"config\" properties file contains information necessary to generate all" + LF +
					 "        licenses (key paths, passwords, etc.) and generally will not need to change. The \"license\"" + LF +
					 "        properties file contains all of the information you need to generate this particular license. See" + LF +
					 "        the Javadoc API documentation for information about the contents of these two files." + LF +
					 "        If you do not specify the \"config\" properties file, you will be prompted to provide the values" + LF +
					 "        that were expected in that file. Likewise, if you do not specify the \"license\" properties file," + LF +
					 "        you will be prompted to provide the values that were expected in that file." + LF +
					 " -config <file>    Specify the .properties file that configures this generator" + LF +
					 " -help             Display this help message" + LF +
					 " -license <file>   Specify the .properties file that contains the data for this license" + LF,
					 output);
	}

	@Test
	public void testProcessCommandLineOptions05() throws ParseException
	{
		EasyMock.replay(this.device);

		this.console.processCommandLineOptions(new String[] { "-config", "config.properties" });

		assertNotNull("There should be a cli value.", this.console.cli);
	}

	@Test
	public void testProcessCommandLineOptions06() throws ParseException
	{
		EasyMock.replay(this.device);

		this.console.processCommandLineOptions(new String[] { "-license", "license.properties" });

		assertNotNull("There should be a cli value.", this.console.cli);
	}

	@Test
	public void testProcessCommandLineOptions07() throws ParseException
	{
		EasyMock.replay(this.device);

		this.console.processCommandLineOptions(
				new String[] { "-config", "config.properties", "-license", "license.properties" }
		);

		assertNotNull("There should be a cli value.", this.console.cli);
	}

	private void resetLicenseCreator()
	{
		try
		{
			Field field = LicenseCreator.class.getDeclaredField("instance");
			field.setAccessible(true);
			field.set(null, null);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private PrivateKeyDataProvider getPrivateKeyDataProvider()
	{
		try
		{
			Field field = LicenseCreatorProperties.class.getDeclaredField("privateKeyDataProvider");
			field.setAccessible(true);
			return (PrivateKeyDataProvider)field.get(null);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private PasswordProvider getPasswordProvider()
	{
		try
		{
			Field field = LicenseCreatorProperties.class.getDeclaredField("privateKeyPasswordProvider");
			field.setAccessible(true);
			return (PasswordProvider)field.get(null);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testInitializeLicenseCreator01() throws Exception
	{
		this.resetLicenseCreator();

		String fileName = "testInitializeLicenseCreator01.properties";
		File file = new File(fileName);
		if(file.exists())
			FileUtils.forceDelete(file);

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("config")).andReturn(true);
		EasyMock.expect(this.console.cli.getOptionValue("config")).andReturn(fileName);

		EasyMock.replay(this.console.cli, this.device);

		try
		{
			this.console.initializeLicenseCreator();
			fail("Expected exception FileNotFoundException.");
		}
		catch(FileNotFoundException ignore) { }
		finally
		{
			this.resetLicenseCreator();

			EasyMock.verify(this.console.cli);
		}
	}

	@Test
	public void testInitializeLicenseCreator02() throws Exception
	{
		this.resetLicenseCreator();

		String fileName = "testInitializeLicenseCreator02.properties";
		File file = new File(fileName);
		if(file.exists())
			FileUtils.forceDelete(file);

		FileUtils.writeStringToFile(file, "test");

		assertTrue("Setting the file to not readable should have returned true.", file.setReadable(false));
		assertTrue("The file should be writable.", file.canWrite());
		assertFalse("The file should not be readable.", file.canRead());

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("config")).andReturn(true);
		EasyMock.expect(this.console.cli.getOptionValue("config")).andReturn(fileName);

		EasyMock.replay(this.console.cli, this.device);

		try
		{
			this.console.initializeLicenseCreator();
			fail("Expected exception IOException.");
		}
		catch(IOException ignore) { }
		finally
		{
			this.resetLicenseCreator();

			FileUtils.forceDelete(file);

			EasyMock.verify(this.console.cli);
		}
	}

	@Test
	public void testInitializeLicenseCreator03() throws Exception
	{
		this.resetLicenseCreator();

		String fileName = "testInitializeLicenseCreator03.properties";
		File file = new File(fileName);
		if(file.exists())
			FileUtils.forceDelete(file);

		FileUtils.writeStringToFile(
				file,
				"net.nicholaswilliams.java.licensing.privateKeyFile=testInitializeLicenseCreator03.key\r\n" +
				"net.nicholaswilliams.java.licensing.privateKeyPassword=testPassword03"
								   );

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("config")).andReturn(true);
		EasyMock.expect(this.console.cli.getOptionValue("config")).andReturn(fileName);

		EasyMock.replay(this.console.cli, this.device);

		try
		{
			this.console.initializeLicenseCreator();
			fail("Expected exception FileNotFoundException.");
		}
		catch(FileNotFoundException e) { }
		finally
		{
			this.resetLicenseCreator();

			FileUtils.forceDelete(file);

			EasyMock.verify(this.console.cli);
		}
	}

	@Test
	public void testInitializeLicenseCreator04() throws Exception
	{
		this.resetLicenseCreator();

		String fileName = "testInitializeLicenseCreator04.properties";
		File file = new File(fileName);
		if(file.exists())
			FileUtils.forceDelete(file);

		File keyFile = new File("testInitializeLicenseCreator04.key");
		FileUtils.writeStringToFile(keyFile, "aKey");

		FileUtils.writeStringToFile(
				file,
				"net.nicholaswilliams.java.licensing.privateKeyFile=testInitializeLicenseCreator04.key\r\n" +
				"net.nicholaswilliams.java.licensing.privateKeyPassword=testPassword04"
		);

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("config")).andReturn(true);
		EasyMock.expect(this.console.cli.getOptionValue("config")).andReturn(fileName);

		EasyMock.replay(this.console.cli, this.device);

		try
		{
			this.console.initializeLicenseCreator();

			PrivateKeyDataProvider key = this.getPrivateKeyDataProvider();
			assertNotNull("The key provider should not be null.", key);
			assertSame("The key provider is not correct.", FilePrivateKeyDataProvider.class, key.getClass());
			assertEquals("The file is not correct.", keyFile.getAbsolutePath(),
						 ((FilePrivateKeyDataProvider)key).getPrivateKeyFile().getAbsolutePath());

			PasswordProvider password = this.getPasswordProvider();
			assertNotNull("The password provider should not be null.", password);
			assertArrayEquals("The password is not correct.", "testPassword04".toCharArray(), password.getPassword());
		}
		finally
		{
			LicenseCreatorProperties.setPrivateKeyDataProvider(null);
			LicenseCreatorProperties.setPrivateKeyPasswordProvider(null);

			this.resetLicenseCreator();

			FileUtils.forceDelete(file);
			FileUtils.forceDelete(keyFile);

			EasyMock.verify(this.console.cli);
		}
	}

	@Test
	public void testInitializeLicenseCreator05() throws Exception
	{
		this.resetLicenseCreator();

		String fileName = "testInitializeLicenseCreator05.properties";
		File file = new File(fileName);
		if(file.exists())
			FileUtils.forceDelete(file);

		File keyFile = new File("testInitializeLicenseCreator05.key");
		FileUtils.writeStringToFile(keyFile, "aKey");

		FileUtils.writeStringToFile(
				file,
				"net.nicholaswilliams.java.licensing.privateKeyFile=testInitializeLicenseCreator05.key"
		);

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("config")).andReturn(true);
		EasyMock.expect(this.console.cli.getOptionValue("config")).andReturn(fileName);

		EasyMock.replay(this.console.cli, this.device);

		try
		{
			this.console.initializeLicenseCreator();
			fail("Expected exception RuntimeException.");
		}
		catch(RuntimeException ignore) { }
		finally
		{
			LicenseCreatorProperties.setPrivateKeyDataProvider(null);
			LicenseCreatorProperties.setPrivateKeyPasswordProvider(null);

			this.resetLicenseCreator();

			FileUtils.forceDelete(file);
			FileUtils.forceDelete(keyFile);

			EasyMock.verify(this.console.cli);
		}
	}

	@Test
	public void testInitializeLicenseCreator06() throws Exception
	{
		this.resetLicenseCreator();

		String fileName = "testInitializeLicenseCreator06.properties";
		File file = new File(fileName);
		if(file.exists())
			FileUtils.forceDelete(file);

		File keyFile = new File("testInitializeLicenseCreator06.key");
		FileUtils.writeStringToFile(keyFile, "aKey");

		FileUtils.writeStringToFile(
				file,
				"net.nicholaswilliams.java.licensing.privateKeyPassword=testPassword06"
		);

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("config")).andReturn(true);
		EasyMock.expect(this.console.cli.getOptionValue("config")).andReturn(fileName);

		EasyMock.replay(this.console.cli, this.device);

		try
		{
			this.console.initializeLicenseCreator();
			fail("Expected exception RuntimeException.");
		}
		catch(RuntimeException ignore) { }
		finally
		{
			LicenseCreatorProperties.setPrivateKeyDataProvider(null);
			LicenseCreatorProperties.setPrivateKeyPasswordProvider(null);

			this.resetLicenseCreator();

			FileUtils.forceDelete(file);
			FileUtils.forceDelete(keyFile);

			EasyMock.verify(this.console.cli);
		}
	}

	@Test
	public void testInitializeLicenseCreator07() throws Exception
	{
		this.resetLicenseCreator();

		String fileName = "testInitializeLicenseCreator07.properties";
		File file = new File(fileName);
		if(file.exists())
			FileUtils.forceDelete(file);

		FileUtils.writeStringToFile(
				file,
				"net.nicholaswilliams.java.licensing.privateKeyProvider=net.nicholaswilliams.java.licensing.samples.SampleFilePrivateKeyDataProvider\r\n" +
				"net.nicholaswilliams.java.licensing.privateKeyPasswordProvider=net.nicholaswilliams.java.licensing.samples.SamplePasswordProvider"
		);

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("config")).andReturn(true);
		EasyMock.expect(this.console.cli.getOptionValue("config")).andReturn(fileName);

		EasyMock.replay(this.console.cli, this.device);

		try
		{
			this.console.initializeLicenseCreator();

			PrivateKeyDataProvider key = this.getPrivateKeyDataProvider();
			assertNotNull("The key provider should not be null.", key);
			assertSame("The key provider is not correct.", SampleFilePrivateKeyDataProvider.class, key.getClass());

			PasswordProvider password = this.getPasswordProvider();
			assertNotNull("The password provider should not be null.", password);
			assertSame("The password provider is not correct.", SamplePasswordProvider.class, password.getClass());
		}
		finally
		{
			LicenseCreatorProperties.setPrivateKeyDataProvider(null);
			LicenseCreatorProperties.setPrivateKeyPasswordProvider(null);

			this.resetLicenseCreator();

			FileUtils.forceDelete(file);

			EasyMock.verify(this.console.cli);
		}
	}

	@Test
	public void testInitializeLicenseCreator08() throws Exception
	{
		this.resetLicenseCreator();

		File keyFile = new File("testInitializeLicenseCreator08.key");
		FileUtils.writeStringToFile(keyFile, "aKey");

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("config")).andReturn(true);
		EasyMock.expect(this.console.cli.getOptionValue("config")).andReturn("  ");

		this.device.printOutLn("Would you like to...");
		EasyMock.expectLastCall();
		this.device.printOutLn("    (1) Read the private key from a file?");
		EasyMock.expectLastCall();
		this.device.printOutLn("    (2) Use a PrivateKeyDataProvider implementation from the classpath?");
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readLine("Your selection (default 1)? ")).andReturn(" ");
		this.device.printOutLn();
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readLine("Please enter the name of the private key file to use: ")).andReturn(null);
		this.device.printOutLn();
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readLine("Invalid or non-existent file. Please enter the name of the private " +
											 "key file to use: ")).andReturn("  ");
		this.device.printOutLn();
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readLine("Invalid or non-existent file. Please enter the name of the private " +
											 "key file to use: ")).andReturn("testInitializeLicenseCreator08-bad.key");
		this.device.printOutLn();
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readLine("Invalid or non-existent file. Please enter the name of the private " +
											 "key file to use: ")).andReturn("testInitializeLicenseCreator08.key");
		this.device.printOutLn();
		EasyMock.expectLastCall();

		this.device.printOutLn("Would you like to...");
		EasyMock.expectLastCall();
		this.device.printOutLn("    (1) Type the private key password in manually?");
		EasyMock.expectLastCall();
		this.device.printOutLn("    (2) Use a PasswordProvider implementation from the classpath?");
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readLine("Your selection (default 1)? ")).andReturn("2");
		this.device.printOutLn();
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readLine("Please enter the fully-qualified class name for the " +
											 "PasswordProvider implementation: ")).andReturn(null);
		this.device.printOutLn();
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readLine("Please enter the fully-qualified class name for the " +
											 "PasswordProvider implementation: ")).andReturn("  ");
		this.device.printOutLn();
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readLine("Please enter the fully-qualified class name for the " +
											 "PasswordProvider implementation: ")).
				andReturn("net.nicholaswilliams.java.licensing.samples.SamplePasswordProvider");
		this.device.printOutLn();
		EasyMock.expectLastCall();

		EasyMock.replay(this.console.cli, this.device);

		try
		{
			this.console.initializeLicenseCreator();

			PrivateKeyDataProvider key = this.getPrivateKeyDataProvider();
			assertNotNull("The key provider should not be null.", key);
			assertSame("The key provider is not correct.", FilePrivateKeyDataProvider.class, key.getClass());
			assertEquals("The file is not correct.", keyFile.getAbsolutePath(),
						 ((FilePrivateKeyDataProvider)key).getPrivateKeyFile().getAbsolutePath());

			PasswordProvider password = this.getPasswordProvider();
			assertNotNull("The password provider should not be null.", password);
			assertSame("The password provider is not correct.", SamplePasswordProvider.class, password.getClass());
		}
		finally
		{
			this.resetLicenseCreator();

			FileUtils.forceDelete(keyFile);

			EasyMock.verify(this.console.cli);
		}
	}

	@Test
	public void testInitializeLicenseCreator09() throws Exception
	{
		this.resetLicenseCreator();

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("config")).andReturn(false);

		this.device.printOutLn("Would you like to...");
		EasyMock.expectLastCall();
		this.device.printOutLn("    (1) Read the private key from a file?");
		EasyMock.expectLastCall();
		this.device.printOutLn("    (2) Use a PrivateKeyDataProvider implementation from the classpath?");
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readLine("Your selection (default 1)? ")).andReturn("2");
		this.device.printOutLn();
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readLine("Please enter the fully-qualified class name for the " +
											 "PrivateKeyDataProvider implementation: ")).andReturn(null);
		this.device.printOutLn();
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readLine("Please enter the fully-qualified class name for the " +
											 "PrivateKeyDataProvider implementation: ")).andReturn("  ");
		this.device.printOutLn();
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readLine("Please enter the fully-qualified class name for the " +
											 "PrivateKeyDataProvider implementation: ")).
				andReturn("net.nicholaswilliams.java.licensing.samples.SampleEmbeddedPrivateKeyDataProvider");
		this.device.printOutLn();
		EasyMock.expectLastCall();

		this.device.printOutLn("Would you like to...");
		EasyMock.expectLastCall();
		this.device.printOutLn("    (1) Type the private key password in manually?");
		EasyMock.expectLastCall();
		this.device.printOutLn("    (2) Use a PasswordProvider implementation from the classpath?");
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readLine("Your selection (default 1)? ")).andReturn(" ");
		this.device.printOutLn();
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readPassword("Please type the password for the private key: ")).andReturn(null);
		this.device.printOutLn();
		EasyMock.expectLastCall();
		EasyMock.expect(this.device.readPassword("Invalid password. Please type the password for the private key: ")).
				andReturn("testPassword09".toCharArray());
		this.device.printOutLn();
		EasyMock.expectLastCall();

		EasyMock.replay(this.console.cli, this.device);

		try
		{
			this.console.initializeLicenseCreator();

			PrivateKeyDataProvider key = this.getPrivateKeyDataProvider();
			assertNotNull("The key provider should not be null.", key);
			assertSame("The key provider is not correct.", SampleEmbeddedPrivateKeyDataProvider.class, key.getClass());

			PasswordProvider password = this.getPasswordProvider();
			assertNotNull("The password provider should not be null.", password);
			assertArrayEquals("The password is not correct.", "testPassword09".toCharArray(), password.getPassword());
		}
		finally
		{
			this.resetLicenseCreator();

			EasyMock.verify(this.console.cli);
		}
	}

	@Test
	public void testGenerateLicense01() throws Exception
	{
		this.resetLicenseCreator();

		String fileName = "testGenerateLicense01.properties";
		File file = new File(fileName);
		if(file.exists())
			FileUtils.forceDelete(file);

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("license")).andReturn(true);
		EasyMock.expect(this.console.cli.getOptionValue("license")).andReturn(fileName);

		EasyMock.replay(this.console.cli, this.device);

		try
		{
			this.console.generateLicense();
			fail("Expected exception FileNotFoundException.");
		}
		catch(FileNotFoundException ignore) { }
		finally
		{
			this.resetLicenseCreator();

			EasyMock.verify(this.console.cli);
		}
	}

	@Test
	public void testGenerateLicense02() throws Exception
	{
		this.resetLicenseCreator();

		String fileName = "testGenerateLicense02.properties";
		File file = new File(fileName);
		if(file.exists())
			FileUtils.forceDelete(file);

		FileUtils.writeStringToFile(file, "test");

		assertTrue("Setting the file to not readable should have returned true.", file.setReadable(false));
		assertTrue("The file should be writable.", file.canWrite());
		assertFalse("The file should not be readable.", file.canRead());

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("license")).andReturn(true);
		EasyMock.expect(this.console.cli.getOptionValue("license")).andReturn(fileName);

		EasyMock.replay(this.console.cli, this.device);

		try
		{
			this.console.generateLicense();
			fail("Expected exception IOException.");
		}
		catch(IOException ignore) { }
		finally
		{
			this.resetLicenseCreator();

			FileUtils.forceDelete(file);

			EasyMock.verify(this.console.cli);
		}
	}

	@Test
	public void testGenerateLicense03() throws Exception
	{
		this.resetLicenseCreator();

		SamplePasswordProvider passwordProvider = new SamplePasswordProvider();

		String fileName = "testGenerateLicense03.properties";
		File file = new File(fileName);
		if(file.exists())
			FileUtils.forceDelete(file);

		FileUtils.writeStringToFile(file, "");

		Capture<String> capture = new Capture<String>();

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("license")).andReturn(true);
		EasyMock.expect(this.console.cli.getOptionValue("license")).andReturn(fileName);

		this.device.printOut(EasyMock.capture(capture));
		EasyMock.expectLastCall();

		EasyMock.replay(this.console.cli, this.device);

		LicenseCreatorProperties.setPrivateKeyDataProvider(new SampleEmbeddedPrivateKeyDataProvider());
		LicenseCreatorProperties.setPrivateKeyPasswordProvider(passwordProvider);

		try
		{
			this.console.generateLicense();

			assertNotNull("The encoded license data should not be null.", capture.getValue());

			byte[] data = Base64.decodeBase64(capture.getValue());

			assertNotNull("The license data should not be null.", data);
			assertTrue("The license data should not be empty.", data.length > 0);

			SignedLicense signed = (new ObjectSerializer()).readObject(SignedLicense.class, data);

			assertNotNull("The signed license should not be null.", signed);

			License license = MockLicenseHelper.deserialize(Encryptor.decryptRaw(
					signed.getLicenseContent(), passwordProvider.getPassword()
			));

			assertNotNull("The license is not correct.", license);

			assertEquals("The product key is not correct.", "", license.getProductKey());
			assertEquals("The holder is not correct.", "", license.getHolder());
			assertEquals("The issuer is not correct.", "", license.getIssuer());
			assertEquals("The subject is not correct.", "", license.getSubject());
			assertEquals("The issue date is not correct.", 0L, license.getIssueDate());
			assertEquals("The good after date is not correct.", 0L, license.getGoodAfterDate());
			assertEquals("The good before date is not correct.", 0L, license.getGoodBeforeDate());
			assertEquals("The number of licenses is not correct.", 0, license.getNumberOfLicenses());
			assertEquals("The number of features is not correct.", 0, license.getFeatures().size());
		}
		finally
		{
			this.resetLicenseCreator();

			FileUtils.forceDelete(file);

			EasyMock.verify(this.console.cli);
		}
	}

	@Test
	public void testGenerateLicense04() throws Exception
	{
		this.resetLicenseCreator();

		SamplePasswordProvider passwordProvider = new SamplePasswordProvider();

		String fileName = "testGenerateLicense04.properties";
		File file = new File(fileName);
		if(file.exists())
			FileUtils.forceDelete(file);

		FileUtils.writeStringToFile(file, "net.nicholaswilliams.java.licensing.password=somePassword04\r\n" +
										  "net.nicholaswilliams.java.licensing.issueDate=abcdefg\r\n" +
										  "net.nicholaswilliams.java.licensing.numberOfLicenses=gfedcba");

		Capture<String> capture = new Capture<String>();

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("license")).andReturn(true);
		EasyMock.expect(this.console.cli.getOptionValue("license")).andReturn(fileName);

		this.device.printOut(EasyMock.capture(capture));
		EasyMock.expectLastCall();

		EasyMock.replay(this.console.cli, this.device);

		LicenseCreatorProperties.setPrivateKeyDataProvider(new SampleEmbeddedPrivateKeyDataProvider());
		LicenseCreatorProperties.setPrivateKeyPasswordProvider(passwordProvider);

		try
		{
			this.console.generateLicense();

			assertNotNull("The encoded license data should not be null.", capture.getValue());

			byte[] data = Base64.decodeBase64(capture.getValue());

			assertNotNull("The license data should not be null.", data);
			assertTrue("The license data should not be empty.", data.length > 0);

			SignedLicense signed = (new ObjectSerializer()).readObject(SignedLicense.class, data);

			assertNotNull("The signed license should not be null.", signed);

			License license = MockLicenseHelper.deserialize(Encryptor.decryptRaw(
					signed.getLicenseContent(), "somePassword04".toCharArray()
			));

			assertNotNull("The license is not correct.", license);

			assertEquals("The product key is not correct.", "", license.getProductKey());
			assertEquals("The holder is not correct.", "", license.getHolder());
			assertEquals("The issuer is not correct.", "", license.getIssuer());
			assertEquals("The subject is not correct.", "", license.getSubject());
			assertEquals("The issue date is not correct.", 0L, license.getIssueDate());
			assertEquals("The good after date is not correct.", 0L, license.getGoodAfterDate());
			assertEquals("The good before date is not correct.", 0L, license.getGoodBeforeDate());
			assertEquals("The number of licenses is not correct.", 0, license.getNumberOfLicenses());
			assertEquals("The number of features is not correct.", 0, license.getFeatures().size());
		}
		finally
		{
			this.resetLicenseCreator();

			FileUtils.forceDelete(file);

			EasyMock.verify(this.console.cli);
		}
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testGenerateLicense05() throws Exception
	{
		this.resetLicenseCreator();

		SamplePasswordProvider passwordProvider = new SamplePasswordProvider();

		String fileName = "testGenerateLicense05.properties";
		File file = new File(fileName);
		if(file.exists())
			FileUtils.forceDelete(file);

		FileUtils.writeStringToFile(file, "net.nicholaswilliams.java.licensing.password=anotherPassword05\r\n" +
										  "net.nicholaswilliams.java.licensing.productKey=6575-TH0T-SNL5-7XGG-1099-1040\r\n" +
										  "net.nicholaswilliams.java.licensing.holder=myHolder01\r\n" +
										  "net.nicholaswilliams.java.licensing.issuer=yourIssuer02\r\n" +
										  "net.nicholaswilliams.java.licensing.subject=aSubject03\r\n" +
										  "net.nicholaswilliams.java.licensing.issueDate=2012-05-01 22:21:20\r\n" +
										  "net.nicholaswilliams.java.licensing.goodAfterDate=2012-06-01 00:00:00\r\n" +
										  "net.nicholaswilliams.java.licensing.goodBeforeDate=2012-06-30 23:59:59\r\n" +
										  "net.nicholaswilliams.java.licensing.numberOfLicenses=83\r\n" +
										  "net.nicholaswilliams.java.licensing.features.MY_FEATURE_01=\r\n" +
										  "net.nicholaswilliams.java.licensing.features.ANOTHER_FEATURE_02=2012-06-15 23:59:59\r\n");

		Capture<String> capture = new Capture<String>();

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("license")).andReturn(true);
		EasyMock.expect(this.console.cli.getOptionValue("license")).andReturn(fileName);

		this.device.printOut(EasyMock.capture(capture));
		EasyMock.expectLastCall();

		EasyMock.replay(this.console.cli, this.device);

		LicenseCreatorProperties.setPrivateKeyDataProvider(new SampleEmbeddedPrivateKeyDataProvider());
		LicenseCreatorProperties.setPrivateKeyPasswordProvider(passwordProvider);

		try
		{
			this.console.generateLicense();

			assertNotNull("The encoded license data should not be null.", capture.getValue());

			byte[] data = Base64.decodeBase64(capture.getValue());

			assertNotNull("The license data should not be null.", data);
			assertTrue("The license data should not be empty.", data.length > 0);

			SignedLicense signed = (new ObjectSerializer()).readObject(SignedLicense.class, data);

			assertNotNull("The signed license should not be null.", signed);

			License license = MockLicenseHelper.deserialize(Encryptor.decryptRaw(
					signed.getLicenseContent(), "anotherPassword05".toCharArray()
			));

			assertNotNull("The license is not correct.", license);

			assertEquals("The product key is not correct.", "6575-TH0T-SNL5-7XGG-1099-1040", license.getProductKey());
			assertEquals("The holder is not correct.", "myHolder01", license.getHolder());
			assertEquals("The issuer is not correct.", "yourIssuer02", license.getIssuer());
			assertEquals("The subject is not correct.", "aSubject03", license.getSubject());
			assertEquals("The issue date is not correct.", new Date(112, 4, 1, 22, 21, 20).getTime(), license.getIssueDate());
			assertEquals("The good after date is not correct.", new Date(112, 5, 1, 0, 0, 0).getTime(), license.getGoodAfterDate());
			assertEquals("The good before date is not correct.", new Date(112, 5, 30, 23, 59, 59).getTime(), license.getGoodBeforeDate());
			assertEquals("The number of licenses is not correct.", 83, license.getNumberOfLicenses());
			assertEquals("The number of features is not correct.", 2, license.getFeatures().size());

			HashMap<String, License.Feature> map = new HashMap<String, License.Feature>();
			for(License.Feature feature : license.getFeatures())
				map.put(feature.getName(), feature);

			assertNotNull("Feature 1 should not be null.", map.get("MY_FEATURE_01"));
			assertEquals("Feature 1 is not correct.", -1L, map.get("MY_FEATURE_01").getGoodBeforeDate());

			assertNotNull("Feature 2 should not be null.", map.get("ANOTHER_FEATURE_02"));
			assertEquals("Feature 2 is not correct.", new Date(112, 5, 15, 23, 59, 59).getTime(),
						 map.get("ANOTHER_FEATURE_02").getGoodBeforeDate());
		}
		finally
		{
			this.resetLicenseCreator();

			FileUtils.forceDelete(file);

			EasyMock.verify(this.console.cli);
		}
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testGenerateLicense06() throws Exception
	{
		this.resetLicenseCreator();

		SamplePasswordProvider passwordProvider = new SamplePasswordProvider();

		String fileName = "testGenerateLicense06.properties";
		File file = new File(fileName);
		if(file.exists())
			FileUtils.forceDelete(file);

		String licenseFileName = "testGenerateLicense06.license";
		File licenseFile = new File(licenseFileName);
		if(licenseFile.exists())
			FileUtils.forceDelete(licenseFile);

		FileUtils.writeStringToFile(file, "net.nicholaswilliams.java.licensing.password=finalPassword06\r\n" +
										  "net.nicholaswilliams.java.licensing.productKey=5565-1039-AF89-GGX7-TN31-14AL\r\n" +
										  "net.nicholaswilliams.java.licensing.holder=someHolder01\r\n" +
										  "net.nicholaswilliams.java.licensing.issuer=coolIssuer02\r\n" +
										  "net.nicholaswilliams.java.licensing.subject=lameSubject03\r\n" +
										  "net.nicholaswilliams.java.licensing.issueDate=2011-07-15 15:17:19\r\n" +
										  "net.nicholaswilliams.java.licensing.goodAfterDate=2011-09-01 00:00:00\r\n" +
										  "net.nicholaswilliams.java.licensing.goodBeforeDate=2011-12-31 23:59:59\r\n" +
										  "net.nicholaswilliams.java.licensing.numberOfLicenses=21\r\n" +
										  "net.nicholaswilliams.java.licensing.features.FINAL_FEATURE_03=\r\n" +
										  "net.nicholaswilliams.java.licensing.licenseFile=" + licenseFileName + "\r\n");

		this.console.cli = EasyMock.createMockBuilder(CommandLine.class).withConstructor().
				addMockedMethod("hasOption", String.class).
				addMockedMethod("getOptionValue", String.class).
				createStrictMock();

		EasyMock.expect(this.console.cli.hasOption("license")).andReturn(true);
		EasyMock.expect(this.console.cli.getOptionValue("license")).andReturn(fileName);

		EasyMock.replay(this.console.cli, this.device);

		LicenseCreatorProperties.setPrivateKeyDataProvider(new SampleEmbeddedPrivateKeyDataProvider());
		LicenseCreatorProperties.setPrivateKeyPasswordProvider(passwordProvider);

		try
		{
			this.console.generateLicense();

			assertTrue("The license file should exist.", licenseFile.exists());

			byte[] data = FileUtils.readFileToByteArray(licenseFile);

			assertNotNull("The license data should not be null.", data);
			assertTrue("The license data should not be empty.", data.length > 0);

			SignedLicense signed = (new ObjectSerializer()).readObject(SignedLicense.class, data);

			assertNotNull("The signed license should not be null.", signed);

			License license = MockLicenseHelper.deserialize(Encryptor.decryptRaw(
					signed.getLicenseContent(), "finalPassword06".toCharArray()
			));

			assertNotNull("The license is not correct.", license);

			assertEquals("The product key is not correct.", "5565-1039-AF89-GGX7-TN31-14AL", license.getProductKey());
			assertEquals("The holder is not correct.", "someHolder01", license.getHolder());
			assertEquals("The issuer is not correct.", "coolIssuer02", license.getIssuer());
			assertEquals("The subject is not correct.", "lameSubject03", license.getSubject());
			assertEquals("The issue date is not correct.", new Date(111, 6, 15, 15, 17, 19).getTime(), license.getIssueDate());
			assertEquals("The good after date is not correct.", new Date(111, 8, 1, 0, 0, 0).getTime(), license.getGoodAfterDate());
			assertEquals("The good before date is not correct.", new Date(111, 11, 31, 23, 59, 59).getTime(), license.getGoodBeforeDate());
			assertEquals("The number of licenses is not correct.", 21, license.getNumberOfLicenses());
			assertEquals("The number of features is not correct.", 1, license.getFeatures().size());

			HashMap<String, License.Feature> map = new HashMap<String, License.Feature>();
			for(License.Feature feature : license.getFeatures())
				map.put(feature.getName(), feature);

			assertNotNull("Feature 1 should not be null.", map.get("FINAL_FEATURE_03"));
			assertEquals("Feature 1 is not correct.", -1L, map.get("FINAL_FEATURE_03").getGoodBeforeDate());
		}
		finally
		{
			this.resetLicenseCreator();

			FileUtils.forceDelete(file);
			FileUtils.forceDelete(licenseFile);

			EasyMock.verify(this.console.cli);
		}
	}



	@Test
	public void testRun01() throws Exception
	{
		this.console = EasyMock.createMockBuilder(ConsoleLicenseGenerator.class).
				withConstructor(TextInterfaceDevice.class, CommandLineParser.class).
				withArgs(this.device, new GnuParser()).
				addMockedMethod("processCommandLineOptions").
				addMockedMethod("initializeLicenseCreator").
				addMockedMethod("generateLicense").
				createStrictMock();

		String[] arguments = new String[] { "help" };

		this.console.processCommandLineOptions(arguments);
		EasyMock.expectLastCall();
		this.console.initializeLicenseCreator();
		EasyMock.expectLastCall().andThrow(new KeyNotFoundException("message01."));

		this.device.printErrLn("message01. Correct the error and try again.");
		EasyMock.expectLastCall();
		this.device.exit(51);
		EasyMock.expectLastCall();

		EasyMock.replay(this.device, this.console);

		try
		{
			this.console.run(arguments);
		}
		finally
		{
			EasyMock.verify(this.console);
		}
	}

	@Test
	public void testRun02() throws Exception
	{
		this.console = EasyMock.createMockBuilder(ConsoleLicenseGenerator.class).
				withConstructor(TextInterfaceDevice.class, CommandLineParser.class).
				withArgs(this.device, new GnuParser()).
				addMockedMethod("processCommandLineOptions").
				addMockedMethod("initializeLicenseCreator").
				addMockedMethod("generateLicense").
				createStrictMock();

		String[] arguments = new String[] { "help" };

		this.console.processCommandLineOptions(arguments);
		EasyMock.expectLastCall();
		this.console.initializeLicenseCreator();
		EasyMock.expectLastCall().andThrow(new ObjectSerializationException("message02."));

		this.device.printErrLn("message02. Correct the error and try again.");
		EasyMock.expectLastCall();
		this.device.exit(52);
		EasyMock.expectLastCall();

		EasyMock.replay(this.device, this.console);

		try
		{
			this.console.run(arguments);
		}
		finally
		{
			EasyMock.verify(this.console);
		}
	}

	@Test
	public void testRun03() throws Exception
	{
		this.console = EasyMock.createMockBuilder(ConsoleLicenseGenerator.class).
				withConstructor(TextInterfaceDevice.class, CommandLineParser.class).
				withArgs(this.device, new GnuParser()).
				addMockedMethod("processCommandLineOptions").
				addMockedMethod("initializeLicenseCreator").
				addMockedMethod("generateLicense").
				createStrictMock();

		String[] arguments = new String[] { "help" };

		this.console.processCommandLineOptions(arguments);
		EasyMock.expectLastCall();
		this.console.initializeLicenseCreator();
		EasyMock.expectLastCall().andThrow(new AlgorithmNotSupportedException("message03."));

		this.device.printErrLn("The algorithm \"message03.\" is not supported on this system. " +
							   "Contact your system administrator for assistance.");
		EasyMock.expectLastCall();
		this.device.exit(41);
		EasyMock.expectLastCall();

		EasyMock.replay(this.device, this.console);

		try
		{
			this.console.run(arguments);
		}
		finally
		{
			EasyMock.verify(this.console);
		}
	}

	@Test
	public void testRun04() throws Exception
	{
		this.console = EasyMock.createMockBuilder(ConsoleLicenseGenerator.class).
				withConstructor(TextInterfaceDevice.class, CommandLineParser.class).
				withArgs(this.device, new GnuParser()).
				addMockedMethod("processCommandLineOptions").
				addMockedMethod("initializeLicenseCreator").
				addMockedMethod("generateLicense").
				createStrictMock();

		String[] arguments = new String[] { "help" };

		this.console.processCommandLineOptions(arguments);
		EasyMock.expectLastCall();
		this.console.initializeLicenseCreator();
		EasyMock.expectLastCall().andThrow(new InappropriateKeyException("message04."));

		this.device.printErrLn("message04. Contact your system administrator for assistance.");
		EasyMock.expectLastCall();
		this.device.exit(42);
		EasyMock.expectLastCall();

		EasyMock.replay(this.device, this.console);

		try
		{
			this.console.run(arguments);
		}
		finally
		{
			EasyMock.verify(this.console);
		}
	}

	@Test
	public void testRun05() throws Exception
	{
		this.console = EasyMock.createMockBuilder(ConsoleLicenseGenerator.class).
				withConstructor(TextInterfaceDevice.class, CommandLineParser.class).
				withArgs(this.device, new GnuParser()).
				addMockedMethod("processCommandLineOptions").
				addMockedMethod("initializeLicenseCreator").
				addMockedMethod("generateLicense").
				createStrictMock();

		String[] arguments = new String[] { "help" };

		this.console.processCommandLineOptions(arguments);
		EasyMock.expectLastCall();
		this.console.initializeLicenseCreator();
		EasyMock.expectLastCall().andThrow(new InappropriateKeySpecificationException("message05."));

		this.device.printErrLn("message05. Contact your system administrator for assistance.");
		EasyMock.expectLastCall();
		this.device.exit(43);
		EasyMock.expectLastCall();

		EasyMock.replay(this.device, this.console);

		try
		{
			this.console.run(arguments);
		}
		finally
		{
			EasyMock.verify(this.console);
		}
	}

	@Test
	public void testRun06() throws Exception
	{
		this.console = EasyMock.createMockBuilder(ConsoleLicenseGenerator.class).
				withConstructor(TextInterfaceDevice.class, CommandLineParser.class).
				withArgs(this.device, new GnuParser()).
				addMockedMethod("processCommandLineOptions").
				addMockedMethod("initializeLicenseCreator").
				addMockedMethod("generateLicense").
				createStrictMock();

		String[] arguments = new String[] { "help" };

		this.console.processCommandLineOptions(arguments);
		EasyMock.expectLastCall();
		this.console.initializeLicenseCreator();
		EasyMock.expectLastCall();
		this.console.generateLicense();
		EasyMock.expectLastCall().andThrow(new InterruptedException("message06."));

		this.device.printErrLn("The system was interrupted while waiting for events to complete.");
		EasyMock.expectLastCall();
		this.device.exit(44);
		EasyMock.expectLastCall();

		EasyMock.replay(this.device, this.console);

		try
		{
			this.console.run(arguments);
		}
		finally
		{
			EasyMock.verify(this.console);
		}
	}

	@Test
	public void testRun07() throws Exception
	{
		this.console = EasyMock.createMockBuilder(ConsoleLicenseGenerator.class).
				withConstructor(TextInterfaceDevice.class, CommandLineParser.class).
				withArgs(this.device, new GnuParser()).
				addMockedMethod("processCommandLineOptions").
				addMockedMethod("initializeLicenseCreator").
				addMockedMethod("generateLicense").
				createStrictMock();

		String[] arguments = new String[] { "help" };

		this.console.processCommandLineOptions(arguments);
		EasyMock.expectLastCall();
		this.console.initializeLicenseCreator();
		EasyMock.expectLastCall();
		this.console.generateLicense();
		EasyMock.expectLastCall().andThrow(new IOException("message07."));

		this.device.printErrLn("An error occurred writing or reading files from the system. Analyze the error " +
					"below to determine what went wrong and fix it!");
		EasyMock.expectLastCall();
		this.device.printErrLn("java.io.IOException: message07.");
		EasyMock.expectLastCall();
		this.device.exit(21);
		EasyMock.expectLastCall();

		EasyMock.replay(this.device, this.console);

		try
		{
			this.console.run(arguments);
		}
		finally
		{
			EasyMock.verify(this.console);
		}
	}

	@Test
	public void testRun08() throws Exception
	{
		this.console = EasyMock.createMockBuilder(ConsoleLicenseGenerator.class).
				withConstructor(TextInterfaceDevice.class, CommandLineParser.class).
				withArgs(this.device, new GnuParser()).
				addMockedMethod("processCommandLineOptions").
				addMockedMethod("initializeLicenseCreator").
				addMockedMethod("generateLicense").
				createStrictMock();

		String[] arguments = new String[] { "testOption08" };

		this.console.processCommandLineOptions(arguments);
		EasyMock.expectLastCall();
		this.console.initializeLicenseCreator();
		EasyMock.expectLastCall();
		this.console.generateLicense();
		EasyMock.expectLastCall().andThrow(new RuntimeException("message08."));

		this.device.printErrLn("java.lang.RuntimeException: message08.");
		EasyMock.expectLastCall();
		this.device.exit(-1);
		EasyMock.expectLastCall();

		EasyMock.replay(this.device, this.console);

		try
		{
			this.console.run(arguments);
		}
		finally
		{
			EasyMock.verify(this.console);
		}
	}

	@Test
	public void testRun09() throws Exception
	{
		this.console = EasyMock.createMockBuilder(ConsoleLicenseGenerator.class).
				withConstructor(TextInterfaceDevice.class, CommandLineParser.class).
				withArgs(this.device, new GnuParser()).
				addMockedMethod("processCommandLineOptions").
				addMockedMethod("initializeLicenseCreator").
				addMockedMethod("generateLicense").
				createStrictMock();

		String[] arguments = new String[] { "lastOption09" };

		this.console.processCommandLineOptions(arguments);
		EasyMock.expectLastCall();
		this.console.initializeLicenseCreator();
		EasyMock.expectLastCall();
		this.console.generateLicense();
		EasyMock.expectLastCall();

		this.device.exit(0);
		EasyMock.expectLastCall();

		EasyMock.replay(this.device, this.console);

		try
		{
			this.console.run(arguments);
		}
		finally
		{
			EasyMock.verify(this.console);
		}
	}

	private static class ThisExceptionMeansTestSucceededException extends SecurityException
	{

	}

	@Test(expected=ThisExceptionMeansTestSucceededException.class)
	public void testMain01()
	{
		SecurityManager securityManager = new MockPermissiveSecurityManager() {
			private boolean active = true;

			@Override
			public void checkExit(int status)
			{
				if(this.active)
				{
					this.active = false;
					assertEquals("The exit status is not correct.", 0, status);
					throw new ThisExceptionMeansTestSucceededException();
				}
			}
		};

		EasyMock.replay(this.device);

		System.setSecurityManager(securityManager);

		ConsoleLicenseGenerator.main(new String[] { "-help" });

		System.setSecurityManager(null);
	}

	@Test(expected=ThisExceptionMeansTestSucceededException.class)
	public void testMain02()
	{
		SecurityManager securityManager = new MockPermissiveSecurityManager() {
			private boolean active = true;

			@Override
			public void checkExit(int status)
			{
				if(this.active)
				{
					this.active = false;
					assertEquals("The exit status is not correct.", 1, status);
					throw new ThisExceptionMeansTestSucceededException();
				}
			}
		};

		EasyMock.replay(this.device);

		System.setSecurityManager(securityManager);

		ConsoleLicenseGenerator.main(new String[] { "-badOption" });

		System.setSecurityManager(null);
	}
}