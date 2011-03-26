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

package net.nicholaswilliams.java.licensing.samples;

import net.nicholaswilliams.java.licensing.encryption.KeyPasswordProvider;

/**
 * A sample implementation of the {@link KeyPasswordProvider} interface.
 *
 * @author Nick Williams
 * @since 1.0.0
 * @version 1.0.0
 * @see KeyPasswordProvider
 */
@SuppressWarnings("unused")
public class SampleKeyPasswordProvider implements KeyPasswordProvider
{
	public char[] getKeyPassword()
	{
		return new char[] {
			's', 'a', 'm', 'p', 'l', 'e', 'K', 'e', 'y', '1', '9', '8', '4'
		};
	}
}
