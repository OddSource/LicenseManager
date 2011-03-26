/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.nicholaswilliams.java.licensing.encryption;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Hasher.
 */
public class TestHasher
{
	@Before
	public void setUp()
	{

	}

	@After
	public void tearDown()
	{

	}

	@Test
	public void testHashSameStrings() throws Exception
	{
		String unhashed = "myteststring";

		String enc1 = Hasher.hash(unhashed);
		String enc2 = Hasher.hash(unhashed);
		String enc3 = Hasher.hash(unhashed);

		assertNotNull("The first encrypted string was null.", enc1);
		assertNotNull("The second encrypted string was null.", enc2);
		assertNotNull("The third encrypted string was null.", enc3);

		assertFalse("The first encrypted string was not encrypted properly.", enc1.equals(unhashed));
		assertFalse("The second encrypted string was not encrypted properly.", enc2.equals(unhashed));
		assertFalse("The third encrypted string was not encrypted properly.", enc3.equals(unhashed));

		assertEquals("The first and second encrypted strings do not match.", enc1, enc2);
		assertEquals("The first and third encrypted strings do not match.", enc1, enc3);
		assertEquals("The second and third encrypted strings do not match.", enc2, enc3);
	}

	@Test
	public void testHashDifferentString() throws Exception
	{
		String unhashed1 = "myteststring1";
		String unhashed2 = "myteststring2";

		String encrypted1 = Hasher.hash(unhashed1);
		String encrypted2 = Hasher.hash(unhashed2);

		assertNotNull("The first encrypted string was null.", encrypted1);
		assertNotNull("The second encrypted string was null.", encrypted2);

		assertFalse("The first encrypted string matches the second encrypted string, and it shouldn't.", encrypted1.equals(encrypted2));
	}
}
