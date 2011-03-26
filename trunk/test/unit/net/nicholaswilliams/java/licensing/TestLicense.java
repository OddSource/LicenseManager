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

package net.nicholaswilliams.java.licensing;

import net.nicholaswilliams.java.licensing.immutable.ImmutableLinkedHashSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.security.auth.x500.X500Principal;

import static org.junit.Assert.*;

/**
 * Test class for License.
 */
public class TestLicense
{
	private License license;

	@Before
	public void setUp()
	{
		this.license = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324983L).
						withGoodAfterDate(2348907325000L).
						withGoodBeforeDate(2348917325000L).
						withNumberOfLicenses(57).
						withFeature("nickFeature1").withFeature("allisonFeature2")
		);
	}

	@After
	public void tearDown()
	{

	}

	@Test
	public void testIssuer01()
	{
		assertEquals("The license is not correct.", "CN=Nick Williams, C=US, ST=TN",
					 this.license.getIssuer().toString());
	}

	@Test
	public void testHolder01()
	{
		assertEquals("The holder is not correct.", "CN=Tim Williams, C=US, ST=AL", this.license.getHolder().toString());
	}

	@Test
	public void testSubject01()
	{
		assertEquals("The subject is not correct.", "Simple Product Name(TM)", this.license.getSubject());
	}

	@Test
	public void testIssueDate01()
	{
		assertEquals("The issue date is not correct.", 2348907324983L, this.license.getIssueDate());
	}

	@Test
	public void testGoodAfterDate01()
	{
		assertEquals("The issue date is not correct.", 2348907325000L, this.license.getGoodAfterDate());
	}

	@Test
	public void testGoodBeforeDate01()
	{
		assertEquals("The issue date is not correct.", 2348917325000L, this.license.getGoodBeforeDate());
	}

	@Test
	public void testNumberOfLicenses01()
	{
		assertEquals("The number of licenses is not correct.", 57, this.license.getNumberOfLicenses());
	}

	@Test
	public void testFeatures01()
	{
		ImmutableLinkedHashSet<String> features = this.license.getFeatures();

		assertEquals("The size of the features is not correct.", 2, features.size());
		assertTrue("Feature 1 is missing.", features.contains("nickFeature1"));
		assertTrue("Feature 2 is missing.", features.contains("allisonFeature2"));
	}

	@Test
	public void testFeatures02()
	{
		assertTrue("Feature 1 is missing.", this.license.hasLicenseForAllFeatures("nickFeature1"));
		assertTrue("Feature 2 is missing.", this.license.hasLicenseForAllFeatures("allisonFeature2"));
	}

	@Test
	public void testFeatures03()
	{
		assertTrue("Result 1 is incorrect.", this.license.hasLicenseForAllFeatures("nickFeature1", "allisonFeature2"));
		assertFalse("Result 2 is incorrect.", this.license.hasLicenseForAllFeatures("timFeature1", "allisonFeature2"));
		assertFalse("Result 3 is incorrect.", this.license.hasLicenseForAllFeatures("nickFeature1", "timFeature2"));
		assertFalse("Result 4 is incorrect.", this.license.hasLicenseForAllFeatures("nickFeature1", "allisonFeature2", "timFeature3"));
		assertFalse("Result 5 is incorrect.", this.license.hasLicenseForAllFeatures("jeffFeature1", "timFeature2"));
		assertFalse("Result 6 is incorrect.", this.license.hasLicenseForAllFeatures("dogFeature1"));
	}

	@Test
	public void testFeatures04()
	{
		assertTrue("Result 1 is incorrect.", this.license.hasLicenseForAnyFeature("nickFeature1", "allisonFeature2"));
		assertTrue("Result 2 is incorrect.", this.license.hasLicenseForAnyFeature("timFeature1", "allisonFeature2"));
		assertTrue("Result 3 is incorrect.", this.license.hasLicenseForAnyFeature("nickFeature1", "timFeature2"));
		assertTrue("Result 4 is incorrect.", this.license.hasLicenseForAnyFeature("nickFeature1", "allisonFeature2", "timFeature3"));
		assertFalse("Result 5 is incorrect.", this.license.hasLicenseForAnyFeature("jeffFeature1", "timFeature2"));
		assertFalse("Result 6 is incorrect.", this.license.hasLicenseForAnyFeature("dogFeature1"));
		assertTrue("Result 7 is incorrect.", this.license.hasLicenseForAnyFeature("nickFeature1"));
		assertTrue("Result 8 is incorrect.", this.license.hasLicenseForAnyFeature("allisonFeature2"));
	}

	@Test
	public void testEqualsWithClone01()
	{
		License clone = this.license.clone();

		assertNotSame("The objects should not be the same.", this.license, clone);
		assertEquals("The objects should be equal.", this.license, clone);
		assertEquals("The hash codes should match.", this.license.hashCode(), clone.hashCode());
	}

	@Test
	public void testEqualsWithDuplicate01()
	{
		License duplicate = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324983L).
						withGoodAfterDate(2348907325000L).
						withGoodBeforeDate(2348917325000L).
						withNumberOfLicenses(57).
						withFeature("nickFeature1").withFeature("allisonFeature2")
		);

		assertNotSame("The objects should not be the same.", this.license, duplicate);
		assertEquals("The objects should be equal.", this.license, duplicate);
		assertEquals("The hash codes should match.", this.license.hashCode(), duplicate.hashCode());
	}

	@Test
	public void testEqualsWithDuplicate02()
	{
		License duplicate = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324983L).
						withGoodAfterDate(2348907325000L).
						withGoodBeforeDate(2348917325000L).
						withNumberOfLicenses(57).
						withFeature("allisonFeature2").withFeature("nickFeature1")
		);

		assertNotSame("The objects should not be the same.", this.license, duplicate);
		assertTrue("The objects should be equal.", this.license.equals(duplicate));
		assertEquals("The hash codes should match.", this.license.hashCode(), duplicate.hashCode());
	}

	@Test
	public void testEqualsWithDuplicate03()
	{
		License duplicate = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name").
						withIssueDate(2348907324983L).
						withGoodAfterDate(2348907325000L).
						withGoodBeforeDate(2348917325000L).
						withNumberOfLicenses(57).
						withFeature("allisonFeature2").withFeature("nickFeature1")
		);

		assertNotSame("The objects should not be the same.", this.license, duplicate);
		assertFalse("The objects should not be equal.", this.license.equals(duplicate));
		assertFalse("The hash codes should not match.", this.license.hashCode() == duplicate.hashCode());
	}

	@Test
	public void testEqualsWithDuplicate04()
	{
		License duplicate = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324983L).
						withGoodAfterDate(2348907325000L).
						withGoodBeforeDate(2348917325001L).
						withNumberOfLicenses(57).
						withFeature("allisonFeature2").withFeature("nickFeature1")
		);

		assertNotSame("The objects should not be the same.", this.license, duplicate);
		assertFalse("The objects should not be equal.", this.license.equals(duplicate));
		assertFalse("The hash codes should not match.", this.license.hashCode() == duplicate.hashCode());
	}

	@Test
	public void testEqualsWithDuplicate05()
	{
		License duplicate = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324983L).
						withGoodAfterDate(2348907325000L).
						withGoodBeforeDate(2348917325000L).
						withNumberOfLicenses(56).
						withFeature("allisonFeature2").withFeature("nickFeature1")
		);

		assertNotSame("The objects should not be the same.", this.license, duplicate);
		assertFalse("The objects should not be equal.", this.license.equals(duplicate));
		assertFalse("The hash codes should not match.", this.license.hashCode() == duplicate.hashCode());
	}

	@Test
	public void testEqualsWithDuplicate06()
	{
		License duplicate = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324983L).
						withGoodAfterDate(2348907324999L).
						withGoodBeforeDate(2348917325000L).
						withNumberOfLicenses(57).
						withFeature("allisonFeature2").withFeature("nickFeature1")
		);

		assertNotSame("The objects should not be the same.", this.license, duplicate);
		assertFalse("The objects should not be equal.", this.license.equals(duplicate));
		assertFalse("The hash codes should not match.", this.license.hashCode() == duplicate.hashCode());
	}

	@Test
	public void testEqualsWithDuplicate07()
	{
		License duplicate = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324984L).
						withGoodAfterDate(2348907325000L).
						withGoodBeforeDate(2348917325000L).
						withNumberOfLicenses(57).
						withFeature("allisonFeature2").withFeature("nickFeature1")
		);

		assertNotSame("The objects should not be the same.", this.license, duplicate);
		assertFalse("The objects should not be equal.", this.license.equals(duplicate));
		assertFalse("The hash codes should not match.", this.license.hashCode() == duplicate.hashCode());
	}

	@Test
	public void testEqualsWithDuplicate08()
	{
		License duplicate = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=TN")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324983L).
						withGoodAfterDate(2348907325000L).
						withGoodBeforeDate(2348917325000L).
						withNumberOfLicenses(57).
						withFeature("allisonFeature2").withFeature("nickFeature1")
		);

		assertNotSame("The objects should not be the same.", this.license, duplicate);
		assertFalse("The objects should not be equal.", this.license.equals(duplicate));
		assertFalse("The hash codes should not match.", this.license.hashCode() == duplicate.hashCode());
	}

	@Test
	public void testEqualsWithDuplicate09()
	{
		License duplicate = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=AL")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324983L).
						withGoodAfterDate(2348907325000L).
						withGoodBeforeDate(2348917325000L).
						withNumberOfLicenses(57).
						withFeature("allisonFeature2").withFeature("nickFeature1")
		);

		assertNotSame("The objects should not be the same.", this.license, duplicate);
		assertFalse("The objects should not be equal.", this.license.equals(duplicate));
		assertFalse("The hash codes should not match.", this.license.hashCode() == duplicate.hashCode());
	}

	@Test
	public void testEqualsWithDuplicate10()
	{
		License duplicate = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324983L).
						withGoodAfterDate(2348907325000L).
						withGoodBeforeDate(2348917325000L).
						withNumberOfLicenses(57).
						withFeature("allisonFeature3").withFeature("nickFeature1")
		);

		assertNotSame("The objects should not be the same.", this.license, duplicate);
		assertFalse("The objects should not be equal.", this.license.equals(duplicate));
		assertEquals("The hash codes should match.", this.license.hashCode(), duplicate.hashCode());
	}

	@Test
	public void testEqualsWithDuplicate11()
	{
		License duplicate = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324983L).
						withGoodAfterDate(2348907325000L).
						withGoodBeforeDate(2348917325000L).
						withNumberOfLicenses(57).
						withFeature("allisonFeature2").withFeature("nickFeature4")
		);

		assertNotSame("The objects should not be the same.", this.license, duplicate);
		assertFalse("The objects should not be equal.", this.license.equals(duplicate));
		assertEquals("The hash codes should match.", this.license.hashCode(), duplicate.hashCode());
	}


	@Test
	public void testToString()
	{
		assertEquals("The string was not correct.",
					 "[CN=Tim Williams, C=US, ST=AL][CN=Nick Williams, C=US, ST=TN][Simple Product Name(TM)][2348907324983][2348907325000][2348917325000][57][nickFeature1, allisonFeature2]",
					 this.license.toString());
	}

	@Test
	public void testSerialization()
	{
		assertEquals("The serialization was not correct.",
					 "[CN=Tim Williams, C=US, ST=AL][CN=Nick Williams, C=US, ST=TN][Simple Product Name(TM)][2348907324983][2348907325000][2348917325000][57][nickFeature1, allisonFeature2]",
					 new String(this.license.serialize()));
	}

	@Test
	public void testDeserialization()
	{
		License license = License.deserialize("[CN=John E. Smith, C=CA, ST=QE][CN=OurCompany, C=US, ST=KY][Cool Product, by Company][14429073214631][1443907325000][1443917325000][12][fordFeature1, chevyFeature2, hondaFeature3, toyotaFeature4]".getBytes());

		assertEquals("The holder is not correct.", "CN=John E. Smith, C=CA, ST=QE", license.getHolder().toString());
		assertEquals("The issuer is not correct.", "CN=OurCompany, C=US, ST=KY", license.getIssuer().toString());
		assertEquals("The company is not correct.", "Cool Product, by Company", license.getSubject());
		assertEquals("The issue date is not correct.", 14429073214631L, license.getIssueDate());
		assertEquals("The good after date is not correct.", 1443907325000L, license.getGoodAfterDate());
		assertEquals("The good before date is not correct.", 1443917325000L, license.getGoodBeforeDate());
		assertEquals("The number of licenses is not correct.", 12, license.getNumberOfLicenses());
		assertEquals("The number of features is not correct.", 4, license.getFeatures().size());
		assertTrue("Feature 1 is missing.", license.hasLicenseForAllFeatures("fordFeature1"));
		assertTrue("Feature 2 is missing.", license.hasLicenseForAllFeatures("chevyFeature2"));
		assertTrue("Feature 3 is missing.", license.hasLicenseForAllFeatures("hondaFeature3"));
		assertTrue("Feature 4 is missing.", license.hasLicenseForAllFeatures("toyotaFeature4"));
	}
}