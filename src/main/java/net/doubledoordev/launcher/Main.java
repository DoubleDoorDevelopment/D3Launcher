/*
 * Copyright (c) 2014, DoubleDoorDevelopment
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the project nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.doubledoordev.launcher;

import net.doubledoordev.launcher.util.PackDownloaded;
import net.doubledoordev.launcher.util.Side;
import org.apache.logging.log4j.util.Strings;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import static net.doubledoordev.launcher.util.Constants.*;

/**
 * @author Dries007
 */
public class Main
{
    public static Side side = Side.CLIENT;
    public static boolean ignoreIncomplete;

    private Main()
    {

    }

    public static void main(String[] args) throws Exception
    {
        String packID = null;
        for (int i = 0; i < args.length; i++)
        {
            switch (args[i])
            {
                case "server":
                    side = Side.SERVER;
                    break;
                case "packid":
                    if (i + 1 < args.length) packID = args[++ i];
                    break;
                case "ignoreIncomplete":
                    ignoreIncomplete = true;
                default:
                    LOGGER.warn(String.format("Unused argument: %s", args[i]));
            }
        }
        if (GraphicsEnvironment.isHeadless()) side = Side.SERVER;

        //noinspection ResultOfMethodCallIgnored
        INSTANCES.mkdir();
        //noinspection ResultOfMethodCallIgnored
        MC_VERSIONS.mkdir();
        //noinspection ResultOfMethodCallIgnored
        MAVEN_CACHE.mkdir();
        //noinspection ResultOfMethodCallIgnored
        FORGE_VERSIONS.mkdirs();

        if (side == Side.SERVER)
        {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            if (Strings.isBlank(packID))
            {
                LOGGER.info("No packid argument, please enter the required packid:");
                packID = input.readLine();
            }
            new PackDownloaded(packID);
        }
    }
}
