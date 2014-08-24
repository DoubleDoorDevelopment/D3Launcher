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

import org.apache.maven.model.Dependency;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import static net.doubledoordev.launcher.util.Constants.LOGGER;

/**
 * @author Dries007
 */
public class MavenHelper
{
    private MavenHelper()
    {
    }

    public static boolean download(Set<String> repos, Dependency dependency) throws IOException
    {
        File file = getFile(dependency);

        checkHash(file, dependency);
        if (file.exists())
        {
            LOGGER.info("Skipped " + file);
        }
        else
        {
            LOGGER.info("Downloading " + file);
            for (String repo : repos)
            {
                URL url = new URL(repo + getPath(dependency));
                if (exists(url))
                {
                    File hashFile = getHashFile(dependency);
                    URL hashURL = new URL(repo + getHashPath(dependency));
                    if (!hashFile.exists() && exists(hashURL)) FileUtils.copyURLToFile(hashURL, getHashFile(dependency));

                    FileUtils.copyURLToFile(url, getFile(dependency));
                    break;
                }
            }
        }
        return file.exists();
    }

    public static File getFile(Dependency dependency)
    {
        File file = new File(Constants.MAVEN_CACHE, getPath(dependency));
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        return file;
    }

    public static String getPath(Dependency dependency)
    {
        return getPath(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency.getClassifier(), dependency.getType());
    }

    public static String getPath(String group, String name, String version, String classifier, String type)
    {
        return group.replace('.', '/') + '/' + name + '/' + version + '/' + name + '-' + version + (classifier != null ? "-" + classifier : "") + "." + type;
    }

    public static File getHashFile(Dependency dependency)
    {
        File file = new File(Constants.MAVEN_CACHE, getHashPath(dependency));
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        return file;
    }

    public static String getHashPath(Dependency dependency)
    {
        return getHashPath(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency.getClassifier(), dependency.getType());
    }

    public static String getHashPath(String group, String name, String version, String classifier, String type)
    {
        return group.replace('.', '/') + '/' + name + '/' + version + '/' + name + '-' + version + (classifier != null ? "-" + classifier : "") + "." + type + ".sha1";
    }

    public static void checkHash(File file, Dependency dependency)
    {
        File hashFile = getHashFile(dependency);
        if (!hashFile.exists()) return;

        try
        {
            if (!getChecksum(file).equals(FileUtils.fileRead(hashFile)))
            {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static String getChecksum(File file)
    {
        if (!file.exists()) return null;
        try
        {
            final MessageDigest md = MessageDigest.getInstance("SHA1");
            final FileInputStream fis = new FileInputStream(file);
            final byte[] dataBytes = new byte[1024];

            int nread;
            while ((nread = fis.read(dataBytes)) != -1)
            {
                md.update(dataBytes, 0, nread);
            }

            final byte[] mdbytes = md.digest();

            // convert the byte to hex format
            final StringBuilder sb = new StringBuilder("");
            for (byte mdbyte : mdbytes)
            {
                sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
            }
            fis.close();

            return sb.toString();
        }
        catch (final NoSuchAlgorithmException | IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean exists(URL url) throws IOException
    {
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        huc.setRequestMethod("HEAD");
        huc.connect();
        return (huc.getResponseCode() == HttpURLConnection.HTTP_OK);
    }
}
