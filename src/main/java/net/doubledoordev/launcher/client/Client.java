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

package net.doubledoordev.launcher.client;

import net.doubledoordev.launcher.Main;
import net.doubledoordev.launcher.client.gui.ConsoleWindow;
import net.doubledoordev.launcher.client.gui.MainGui;
import net.doubledoordev.launcher.util.MiscHelper;
import net.doubledoordev.launcher.util.Side;
import net.doubledoordev.launcher.util.packData.PackData;
import org.codehaus.plexus.util.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static net.doubledoordev.launcher.util.Constants.CLIENT_INSTANCES;
import static net.doubledoordev.launcher.util.Constants.GSON;

/**
 * @author Dries007
 */
public class Client extends Main
{
    public ArrayList<PackData> packs = new ArrayList<>();
    private final File packsFile = new File(CLIENT_INSTANCES, "packs.json");
    public static Client instance;

    public static void main(String[] args) throws Exception
    {
        new Client(args);
    }

    protected Client(String[] args) throws Exception
    {
        super(Side.CLIENT, args);
        instance = this;

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        ConsoleWindow.INSTANCE.init();

        if (packsFile.exists())
        {
            Collections.addAll(packs, GSON.fromJson(new FileReader(packsFile), PackData[].class));
        }

        MainGui.INSTANCE.updatePacks();
        MainGui.INSTANCE.show();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                savePackList();
            }
        }));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void makeFolders()
    {
        super.makeFolders();
        CLIENT_INSTANCES.mkdirs();
    }

    public void savePackList()
    {
        try
        {
            FileUtils.fileWrite(packsFile, GSON.toJson(packs));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void addPack(PackData packData) throws Exception
    {
        packs.add(packData);
        savePackList();
        MainGui.INSTANCE.updatePacks();
    }

    public void deletePack(PackData data)
    {
        packs.remove(data);
        MiscHelper.dieFolderDie(data.getInstanceFolder(Side.CLIENT));
        savePackList();
        MainGui.INSTANCE.updatePacks();
    }
}
