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
package io.oddsource.java.licensing.immutable;

import static org.junit.Assert.*;

import java.util.TreeSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for ImmutableIterator.
 */
@SuppressWarnings("EmptyMethod")
public class TestImmutableIterator
{
    final private ImmutableIterator<String> iterator;

    final private MockValidObject valid;

    public TestImmutableIterator()
    {
        TreeSet<String> temp = new TreeSet<>();
        temp.add("MyString1");
        temp.add("YourString2");
        temp.add("HisString3");
        temp.add("HerString4");

        this.valid = new MockValidObject();

        this.iterator = new ImmutableIterator<>(temp.iterator(), this.valid);
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {

    }

    @After
    public void tearDown()
    {

    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveNotAllowed()
    {
        this.iterator.remove();
    }

    @Test
    public void testNext01()
    {
        this.valid.valid = true;

        String[] array = new String[] {"HerString4", "HisString3", "MyString1", "YourString2"};
        int i = 0;

        while(this.iterator.hasNext())
        {
            String string = this.iterator.next();

            assertNotNull("This string should not be null " + i, string);
            assertEquals("This string is not correct " + i, array[i++], string);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = ImmutableModifiedThroughReflectionError.class)
    public void testNext02()
    {
        this.valid.valid = false;
        this.iterator.hasNext();
    }

    @Test(expected = ImmutableModifiedThroughReflectionError.class)
    public void testNext03()
    {
        this.valid.valid = false;
        this.iterator.next();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = ImmutableModifiedThroughReflectionError.class)
    public void testNext04()
    {
        try
        {
            this.valid.valid = true;
            this.iterator.hasNext();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            fail(t.toString());
        }
        this.valid.valid = false;
        this.iterator.next();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = ImmutableModifiedThroughReflectionError.class)
    public void testNext05()
    {
        try
        {
            this.valid.valid = true;
            this.iterator.hasNext();
            this.iterator.next();
            this.iterator.hasNext();
            this.iterator.next();
            this.iterator.hasNext();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            fail(t.toString());
        }
        this.valid.valid = false;
        this.iterator.next();
    }
}
