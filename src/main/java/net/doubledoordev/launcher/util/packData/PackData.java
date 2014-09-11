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

package net.doubledoordev.launcher.util.packData;

import net.doubledoordev.launcher.Main;
import net.doubledoordev.launcher.util.Side;
import org.apache.maven.model.Dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author Dries007
 */
@SuppressWarnings("UnusedDeclaration")
public class PackData
{
    public String          id;
    public String          name;
    public String          mcversion;
    public String          forgeversion;
    public HashSet<String> mavenrepos;
    public HashSet<Mod>    mods;

    @Override
    public String toString()
    {
        return String.format("MC: %s Forge: %s Repos:%s Mods:%s", mcversion, forgeversion, mavenrepos, mods);
    }

    public List<Dependency> getAllModsAsDependencies()
    {
        ArrayList<Dependency> dependencies = new ArrayList<>();
        for (Mod mod : mods)
        {
            if (mod.side.appliesTo(Main.instance.currentSide)) dependencies.add(mod);
        }
        return dependencies;
    }

    public List<Dependency> getAllConfigsAsDependencies()
    {
        ArrayList<Dependency> dependencies = new ArrayList<>();
        for (Mod mod : mods)
        {
            if (mod.config != null) dependencies.add(mod.config);
        }
        return dependencies;
    }

    public File getInstanceFolder(Side side)
    {
        return new File(side.getInstancesFolder(), name);
    }
}
