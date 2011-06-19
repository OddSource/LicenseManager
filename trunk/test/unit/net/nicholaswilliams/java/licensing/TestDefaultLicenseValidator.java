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

import net.nicholaswilliams.java.licensing.exception.ExpiredLicenseException;
import net.nicholaswilliams.java.licensing.exception.InvalidLicenseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.security.auth.x500.X500Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Test class for DefaultLicenseValidator (restored).
 */
public class TestDefaultLicenseValidator
{
	private DefaultLicenseValidator validator;

	@Before
	public void setUp()
	{
		this.validator = new DefaultLicenseValidator();
	}

	@After
	public void tearDown()
	{

	}

	@Test
	public void testGetLicenseDescription()
	{
		License license = new License(
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

		assertEquals("The description is not correct.",
					 "Simple Product Name(TM) license for " + license.getHolder().toString(),
					 this.validator.getLicenseDescription(license));
	}

	@Test
	public void testGetFormattedDate()
	{
		Calendar calendar = Calendar.getInstance();

		assertEquals("The formatted date is not correct.",
					 new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z (Z)").format(new Date(calendar.getTimeInMillis())),
					 this.validator.getFormattedDate(calendar.getTimeInMillis()));
	}

	@Test(expected=InvalidLicenseException.class)
	public void testValidateLicense01()
	{
		Calendar calendar = Calendar.getInstance();

		long goodAfter = calendar.getTimeInMillis() + 1000;
		long goodBefore = calendar.getTimeInMillis() + 87400;

		License license = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324983L).
						withGoodAfterDate(goodAfter).
						withGoodBeforeDate(goodBefore).
						withNumberOfLicenses(57).
						withFeature("nickFeature1").withFeature("allisonFeature2")
		);

		this.validator.validateLicense(license);
	}

	@Test(expected=InvalidLicenseException.class)
	public void testValidateLicense02()
	{
		Calendar calendar = Calendar.getInstance();

		long goodAfter = calendar.getTimeInMillis() + 1000;
		long goodBefore = calendar.getTimeInMillis() - 1000;

		License license = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324983L).
						withGoodAfterDate(goodAfter).
						withGoodBeforeDate(goodBefore).
						withNumberOfLicenses(57).
						withFeature("nickFeature1").withFeature("allisonFeature2")
		);

		this.validator.validateLicense(license);
	}

	@Test(expected=ExpiredLicenseException.class)
	public void testValidateLicense03()
	{
		Calendar calendar = Calendar.getInstance();

		long goodAfter = calendar.getTimeInMillis() - 2000;
		long goodBefore = calendar.getTimeInMillis() - 1000;

		License license = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324983L).
						withGoodAfterDate(goodAfter).
						withGoodBeforeDate(goodBefore).
						withNumberOfLicenses(57).
						withFeature("nickFeature1").withFeature("allisonFeature2")
		);

		this.validator.validateLicense(license);
	}

	@Test
	public void testValidateLicense04()
	{
		Calendar calendar = Calendar.getInstance();

		long goodAfter = calendar.getTimeInMillis() - 1000;
		long goodBefore = calendar.getTimeInMillis() + 85400;

		License license = new License(
				new License.Builder().
						withIssuer(new X500Principal("CN=Nick Williams, C=US, ST=TN")).
						withHolder(new X500Principal("CN=Tim Williams, C=US, ST=AL")).
						withSubject("Simple Product Name(TM)").
						withIssueDate(2348907324983L).
						withGoodAfterDate(goodAfter).
						withGoodBeforeDate(goodBefore).
						withNumberOfLicenses(57).
						withFeature("nickFeature1").withFeature("allisonFeature2")
		);

		this.validator.validateLicense(license);
	}
}