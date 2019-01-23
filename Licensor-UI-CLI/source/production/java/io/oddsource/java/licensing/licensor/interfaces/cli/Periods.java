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
package io.oddsource.java.licensing.licensor.interfaces.cli;

import java.io.PrintStream;

/**
 * Displays periods on the text while an operation completes in a separate
 * thread.
 *
 * @author Nick Williams
 * @version 1.0.0
 * @since 1.0.0
 */
class Periods implements Runnable
{
    private boolean run;

    private final long timeBetweenPeriods;

    private final PrintStream outputStream;

    public Periods(final long timeBetweenPeriods, final PrintStream outputStream)
    {
        this.run = true;
        this.timeBetweenPeriods = timeBetweenPeriods;
        this.outputStream = outputStream;
    }

    @Override
    public void run()
    {
        while(this.run)
        {
            this.outputStream.print('.');
            try
            {
                Thread.sleep(this.timeBetweenPeriods);
            }
            catch(final InterruptedException e)
            {
                break;
            }
        }
    }

    public void stop()
    {
        this.run = false;
    }
}
