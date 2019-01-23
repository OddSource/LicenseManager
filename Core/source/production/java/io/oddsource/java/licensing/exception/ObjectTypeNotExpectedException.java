/*
 * Copyright © 2010-2019 OddSource Code (license@oddsource.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.oddsource.java.licensing.exception;

/**
 * This class class is thrown when an object is deserialized that does not match the type that was expected.
 *
 * @author Nick Williams
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ObjectTypeNotExpectedException extends ObjectDeserializationException
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public ObjectTypeNotExpectedException()
    {
        super("The type of object read did not match the type expected.");
    }

    /**
     * Constructor.
     *
     * @param message The message
     */
    public ObjectTypeNotExpectedException(final String message)
    {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param expectedType The expected type
     * @param encounteredType The encountered type
     */
    public ObjectTypeNotExpectedException(final String expectedType, final String encounteredType)
    {
        super("While deserializing an object of expected type \"" + expectedType + "\", got an object of type \"" +
              encounteredType + "\" instead.");
    }

    /**
     * Constructor.
     *
     * @param cause The cause.
     */
    public ObjectTypeNotExpectedException(final Throwable cause)
    {
        super("The type of object read did not match the type expected.", cause);
    }

    /**
     * Constructor.
     *
     * @param message The message
     * @param cause The cause
     */
    public ObjectTypeNotExpectedException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param expectedType The expected type
     * @param encounteredType The encountered type
     * @param cause The cause
     */
    public ObjectTypeNotExpectedException(
        final String expectedType,
        final String encounteredType,
        final Throwable cause
    )
    {
        super("While deserializing an object of expected type \"" + expectedType + "\", got an object of type \"" +
              encounteredType + "\" instead.", cause);
    }
}
