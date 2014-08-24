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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.doubledoordev.launcher.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author Dries007
 */
public class Constants
{
    public static final Logger LOGGER = LogManager.getLogger(Main.class.getSimpleName());
    public static final Gson   GSON   = new GsonBuilder().setPrettyPrinting().create();
    public static final String OPTIMAZATIONS = "-Xms1G -Xmx1G -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CICompilerCountPerCPU -XX:+TieredCompilation";

    /*
     * Files
     */
    public static final File ROOT           = getRootFile();
    public static final File INSTANCES      = new File(ROOT, "instances");
    public static final File MC_VERSIONS    = new File(ROOT, "mcVersions");
    public static final File FORGE_VERSIONS = new File(ROOT, "forgeVersions");
    public static final File MAVEN_CACHE    = new File(ROOT, "mavenCache");

    /*
     * URLs
     */
    public static final String BASEURL             = "http://dries007.net/ddd/modpacks/";
    public static final String JSONURL             = BASEURL + "json/%s.json";
    public static final String URL_ASSETS          = "https://s3.amazonaws.com/MinecraftResources/";
    public static final String URL_MC_CLIENT       = "http://s3.amazonaws.com/Minecraft.Download/versions/%s/%s.jar";
    public static final String URL_MC_SERVER       = "http://s3.amazonaws.com/Minecraft.Download/versions/%s/minecraft_server.%s.jar";
    public static final String URL_MC_JSON         = "http://s3.amazonaws.com/Minecraft.Download/versions/%s/%s.json";
    public static final String FORGEMAVEN          = "http://files.minecraftforge.net/maven/";
    public static final String URL_FORGE_INSTALLER = FORGEMAVEN + "net/minecraftforge/forge/%s/forge-%s-installer.jar";

    private static File getRootFile()
    {
        try
        {
            return new File(".").getCanonicalFile();
        }
        catch (IOException e)
        {
            return new File(".");
        }
    }
}
