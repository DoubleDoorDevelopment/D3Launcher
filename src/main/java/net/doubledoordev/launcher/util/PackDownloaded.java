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

import net.doubledoordev.launcher.Main;
import net.doubledoordev.launcher.util.packData.PackData;
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static net.doubledoordev.launcher.util.Constants.*;

/**
 * @author Dries007
 */
public class PackDownloaded
{
    public PackDownloaded(String id) throws IOException
    {
        File instanceFolder;
        File modsFolder;
        PackData packData;

        instanceFolder = new File(INSTANCES, id);
        //noinspection ResultOfMethodCallIgnored
        instanceFolder.mkdirs();
        modsFolder = new File(instanceFolder, "mods");
        //noinspection ResultOfMethodCallIgnored
        modsFolder.mkdir();

        File jsonFile = new File(instanceFolder, "pack.json");
        if (!jsonFile.exists()) FileUtils.copyURLToFile(new URL(String.format(JSONURL, id)), jsonFile);

        packData = GSON.fromJson(new FileReader(jsonFile), PackData.class);
        packData.mavenrepos.add(URL_ASSETS);

        LOGGER.info(String.format("Modpack %s, location: %s", id, instanceFolder));
        LOGGER.info(packData);

        List<Dependency> mods = packData.getAllModsAsDependencies();
        List<Dependency> configs = packData.getAllConfigsAsDependencies();
        HashSet<Dependency> missing = new HashSet<>();
        for (Dependency dependency : mods) if (!MavenHelper.download(packData.mavenrepos, dependency)) missing.add(dependency);
        for (Dependency dependency : configs) if (!MavenHelper.download(packData.mavenrepos, dependency)) missing.add(dependency);
        if (!missing.isEmpty())
        {
            LOGGER.warn("Missing mods! " + missing.toString());
            if (!Main.ignoreIncomplete)
            {
                LOGGER.warn("Too ignore this error, add 'ignoreIncomplete' to your program arguments.");
                throw new FileNotFoundException();
            }
            else
            {
                LOGGER.warn("Ignoring the missing mod files.");
            }
        }
        mods.removeAll(missing);
        for (Dependency mod : mods) FileUtils.copyFileToDirectory(MavenHelper.getFile(mod), modsFolder);
        for (Dependency config : configs)
        {
            File configFile = MavenHelper.getFile(config);
            if (config.getType().equals("zip"))
            {
                ZipFile zipFile = new ZipFile(configFile);
                Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
                while (enumeration.hasMoreElements())
                {
                    ZipEntry zipEntry = enumeration.nextElement();
                    LOGGER.info(zipEntry.getName());
                    if (zipEntry.isDirectory())
                        //noinspection ResultOfMethodCallIgnored
                        new File(instanceFolder, zipEntry.getName()).mkdir();
                    else IOUtil.copy(zipFile.getInputStream(zipEntry), new FileWriter(new File(instanceFolder, zipEntry.getName())));
                }
            }
            else FileUtils.copyFileToDirectory(configFile, instanceFolder);
        }

        Main.side.setupMinecraft(packData, instanceFolder);
    }
}
