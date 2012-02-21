/*
 * RSAKeyPairGeneratorInterface.java from LicenseManager modified Monday, February 20, 2012 23:01:52 CST (-0600).
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

package net.nicholaswilliams.java.licensing.encryption;

import net.nicholaswilliams.java.licensing.exception.AlgorithmNotSupportedException;
import net.nicholaswilliams.java.licensing.exception.InappropriateKeyException;
import net.nicholaswilliams.java.licensing.exception.InappropriateKeySpecificationException;
import net.nicholaswilliams.java.licensing.exception.RSA2048NotSupportedException;

import java.io.IOException;
import java.security.KeyPair;

/**
 * An interface for the key pair generator to make unit testing possible. This interface is only implemented by
 * {@link RSAKeyPairGenerator}.
 *
 * @author Nick Williams
 * @version 1.0.0
 * @since 1.0.0
 */
public interface RSAKeyPairGeneratorInterface
{
	/**
	 * Checks whether the two passwords match.
	 *
	 * @param password1 The entered password
	 * @param password2 The confirmed password
	 * @return {@code true} if the passwords match, {@code false} otherwise.
	 */
	public boolean passwordsMatch(char[] password1, char[] password2);

	/**
	 * Generates a key pair with RSA 2048-bit security.
	 * @return a public/private key pair.
	 * @throws RSA2048NotSupportedException if RSA or 2048-bit encryption are not supported.
	 */
	public KeyPair generateKeyPair() throws RSA2048NotSupportedException;

	/**
	 * Saves the key pair specified to output files specified.
	 *
	 * @param keyPair The key pair to save to the files specified
	 * @param privateOutputFileName The name of the file to save the encrypted private key to
	 * @param publicOutputFileName The name of the file to save the encrypted public key to
	 * @param password The password to encrypt the keys with
	 * @throws IOException if an error occurs while writing to the files.
	 * @throws AlgorithmNotSupportedException If the encryption algorithm is not supported
	 * @throws InappropriateKeyException If the public or private keys are invalid
	 * @throws InappropriateKeySpecificationException If the public or private keys are invalid
	 */
	public void saveKeyPairToFiles(KeyPair keyPair,
								   String privateOutputFileName,
								   String publicOutputFileName,
								   char[] password)
			throws IOException, AlgorithmNotSupportedException, InappropriateKeyException,
				   InappropriateKeySpecificationException;

	/**
	 * Saves the public and private keys and password specified to the respective
	 * {@link RSAKeyPairGeneratorInterface.GeneratedClassDescriptor#getJavaFileContents() javaFileContents} fields in
	 * the provided {@link GeneratedClassDescriptor}s.
	 *
	 * @param keyPair The key pair to save
	 * @param privateKeyProvider An object describing the {@link net.nicholaswilliams.java.licensing.licensor.PrivateKeyDataProvider} class to generate, and into which the generated code will be saved
	 * @param publicKeyProvider An object describing the {@link PublicKeyDataProvider} class to generate, and into which the generated code will be saved
	 * @param keyPasswordProvider An object describing the {@link KeyPasswordProvider} class to generate, and into which the generated code will be saved
	 * @param password The password to encrypt the keys with, and to save into the generated {@link KeyPasswordProvider}
	 * @throws AlgorithmNotSupportedException If the encryption algorithm is not supported
	 * @throws InappropriateKeyException If the public or private keys are invalid
	 * @throws InappropriateKeySpecificationException If the public or private keys are invalid
	 */
	public void saveKeyPairToProviders(KeyPair keyPair, GeneratedClassDescriptor privateKeyProvider,
									   GeneratedClassDescriptor publicKeyProvider,
									   GeneratedClassDescriptor keyPasswordProvider, char[] password)
			throws AlgorithmNotSupportedException, InappropriateKeyException, InappropriateKeySpecificationException;

	public static class GeneratedClassDescriptor
	{
		private String packageName;

		private String className;

		private String javaFileContents;

		public String getPackageName()
		{
			return this.packageName;
		}

		public void setPackageName(String packageName)
		{
			this.packageName = packageName;
		}

		public String getClassName()
		{
			return this.className;
		}

		public void setClassName(String className)
		{
			this.className = className;
		}

		public String getJavaFileContents()
		{
			return javaFileContents;
		}

		public void setJavaFileContents(String javaFileContents)
		{
			this.javaFileContents = javaFileContents;
		}
	}
}
