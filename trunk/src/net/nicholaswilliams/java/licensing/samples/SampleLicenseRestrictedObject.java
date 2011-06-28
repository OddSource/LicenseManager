/*
 * SampleLicenseRestrictedObject.java from LicenseManager modified Tuesday, June 28, 2011 11:34:10 CDT (-0500).
 *
 * Copyright 2010-2011 the original author or authors.
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

package net.nicholaswilliams.java.licensing.samples;

import net.nicholaswilliams.java.licensing.FeatureRestriction;
import net.nicholaswilliams.java.licensing.FeatureRestrictionOperand;

/**
 * 
 * @author Nicholas
 */
@FeatureRestriction("FEATURE1")
@SuppressWarnings("unused")
public class SampleLicenseRestrictedObject
{
	public SampleLicenseRestrictedObject()
	{
		
	}

	@FeatureRestriction({"FEATURE2", "FEATURE3"})
	public void furtherRestrictedFeature()
	{

	}

	@FeatureRestriction(value={"FEATURE2", "FEATURE3"}, operand=FeatureRestrictionOperand.OR)
	public void furtherLessRestrictedFeature()
	{

	}
}
