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

package net.doubledoordev.launcher.util;

import net.doubledoordev.launcher.util.winreg.JavaFinder;
import net.doubledoordev.launcher.util.winreg.JavaInfo;

import java.io.File;
import java.io.IOException;

import static net.doubledoordev.launcher.util.Constants.LOGGER;

/**
 * @author Dries007
 */
public class MiscHelper
{
    private static String javapath = getJavaPath();
    public static String getJavaPath()
    {
        if (javapath != null) return javapath;

        JavaInfo javaVersion;
        if (OSUtils.getCurrentOS() == OSUtils.OS.MACOSX)
        {
            javaVersion = JavaFinder.parseJavaVersion();

            if (javaVersion != null && javaVersion.path != null) return javaVersion.path;
        }
        else if (OSUtils.getCurrentOS() == OSUtils.OS.WINDOWS)
        {
            javaVersion = JavaFinder.parseJavaVersion();

            if (javaVersion != null && javaVersion.path != null) return javaVersion.path.replace(".exe", "w.exe");
        }

        // Windows specific code adds <java.home>/bin/java no need mangle javaw.exe here.
        return System.getProperty("java.home") + "/bin/java";
    }

    public static void checkLock(File folder)
    {
        if (!folder.isDirectory()) throw new IllegalArgumentException("Not valid " + folder + ". Must be folder.");

        final File lockFile = new File(folder, "lock");
        if (lockFile.exists()) throw new RuntimeException("Lock file in place.");
        try
        {
            //noinspection ResultOfMethodCallIgnored
            lockFile.createNewFile();
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run()
                {
                    //noinspection ResultOfMethodCallIgnored
                    lockFile.delete();
                }
            }));
        }
        catch (IOException e)
        {
            LOGGER.warn("Could not create lockfile! This might cause trouble!!");
            e.printStackTrace();
        }
    }
}
