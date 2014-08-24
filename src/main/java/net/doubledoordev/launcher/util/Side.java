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

import net.doubledoordev.launcher.util.packData.PackData;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static net.doubledoordev.launcher.util.Constants.*;

/**
 * @author Dries007
 */
public enum Side
{
    SERVER("minecraft_server.%s.jar", URL_MC_SERVER),
    CLIENT("%s.jar", URL_MC_CLIENT),
    BOTH(null, null);

    private String filename;
    private String url;

    Side(String filename, String url)
    {
        this.filename = filename;
        this.url = url;
    }

    public boolean appliesTo(Side side)
    {
        return this == side && this == BOTH;
    }

    public void setupMinecraft(PackData packData, File instanceFolder) throws IOException
    {
        if (this == SERVER)
        {
            String version = packData.mcversion + "-" + packData.forgeversion;
            String filename = String.format("forge-%s-installer.jar", version);
            File forge = new File(instanceFolder, filename);
            File cache = new File(FORGE_VERSIONS, filename);
            if (!cache.exists())
            {
                URL url = new URL(String.format(URL_FORGE_INSTALLER, version, version));
                LOGGER.info("Download " + url);
                FileUtils.copyURLToFile(url, cache);
            }
            if (!forge.exists()) FileUtils.copyFile(cache, forge);

            List<String> arguments = new ArrayList<>();

            arguments.add(MiscHelper.getJavaPath());
            arguments.add("-Xmx1G");

            arguments.add("-jar");
            arguments.add(filename);

            arguments.add("--installServer");

            ProcessBuilder builder = new ProcessBuilder(arguments);
            builder.inheritIO();
            builder.directory(instanceFolder);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            try
            {
                process.waitFor();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            //noinspection ResultOfMethodCallIgnored
            forge.delete();

            String startupline = String.format("\"%s\" %s -server -jar forge-%s-universal.jar nogui", MiscHelper.getJavaPath().replace("javaw.exe", "java.exe"), OPTIMAZATIONS, version);
            switch (OSUtils.getCurrentOS())
            {
                case WINDOWS:
                    FileUtils.fileWrite(new File(instanceFolder, "start.cmd"), FileUtils.fileRead(this.getClass().getResource("/start.cmd").getFile()).replace("%command%", startupline));
                    break;
                default:
                    FileUtils.fileWrite(new File(instanceFolder, "start.sh"), FileUtils.fileRead(this.getClass().getResource("/start.sh").getFile()).replace("%command%", startupline));
                    break;
            }
        }
        else if (this == CLIENT)
        {
            String filename = String.format(this.filename, packData.mcversion);
            File mc = new File(instanceFolder, filename);
            File cache = new File(MC_VERSIONS, filename);
            if (!cache.exists())
            {
                URL url = new URL(String.format(this.url, packData.mcversion, packData.mcversion));
                LOGGER.info("Download " + url);
                FileUtils.copyURLToFile(url, cache);
            }
            if (!mc.exists()) FileUtils.copyFile(cache, mc);
        }
        else throw new IllegalArgumentException("Invalid side " + this);
    }
}
