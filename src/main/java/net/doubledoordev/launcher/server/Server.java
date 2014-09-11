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

package net.doubledoordev.launcher.server;

import net.doubledoordev.launcher.Main;
import net.doubledoordev.launcher.util.PackBuilder;
import net.doubledoordev.launcher.util.Side;
import org.apache.logging.log4j.util.Strings;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static net.doubledoordev.launcher.util.Constants.LOGGER;
import static net.doubledoordev.launcher.util.Constants.SERVER_INSTANCES;

/**
 * @author Dries007
 */
public class Server extends Main
{
    public static void main(String[] args) throws Exception
    {
        new Server(args);
    }

    public String packID, name;
    public boolean headless = GraphicsEnvironment.isHeadless();
    public boolean ignoreIncomplete = false;
    public boolean override = false;

    protected Server(String[] args) throws Exception
    {
        super(Side.SERVER, args);

        // User input
        if (Strings.isBlank(packID))
        {
            LOGGER.info("No packid provided.");
            if (headless)
            {
                LOGGER.info("Please enter the required packid:");
                packID = consoleInput.readLine();
            }
            else
            {
                packID = JOptionPane.showInputDialog(null, "Please enter the required packid", "Packid?", JOptionPane.QUESTION_MESSAGE);
            }
        }
        if (Strings.isBlank(name)) name = packID;

        File instanceFolder = new File(SERVER_INSTANCES, name);
        if (instanceFolder.exists())
        {
            LOGGER.info("Instance folder '" + name + "' already exists!");
            if (!override)
            {
                if (headless)
                {
                    LOGGER.info("Want to continue?");
                    LOGGER.info("Please enter 'true' or 'false':");
                    if (!Boolean.parseBoolean(consoleInput.readLine())) return;
                }
                else
                {
                    if (JOptionPane.showConfirmDialog(null, "Instance folder '\" + name + \"' already exists!\nContinue?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
                }
            }

        }
        //noinspection ResultOfMethodCallIgnored
        instanceFolder.mkdirs();

        LOGGER.info("Making a pack builder...");
        PackBuilder packBuilder = new PackBuilder(instanceFolder, currentSide, name, packID);
        LOGGER.info("Initiate download...");
        boolean complete = packBuilder.download();

        // User input
        if (!complete)
        {
            LOGGER.warn("Missing mods: " + packBuilder.missingMods);
            LOGGER.warn("Missing configs: " + packBuilder.missingConfigs);

            if (!ignoreIncomplete)
            {
                if (headless)
                {
                    LOGGER.info("Want to continue?");
                    LOGGER.info("Please enter 'true' or 'false':");
                    if (!Boolean.parseBoolean(consoleInput.readLine())) return;
                }
                else
                {
                    if (JOptionPane.showConfirmDialog(null, "Some mods where missing. Do you want to continue?\nMissing mods: " + packBuilder.missingMods + "\nMissing configs: " + packBuilder.missingConfigs, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
                }
            }
        }

        LOGGER.info("Copying mod/config files to instance folder...");
        packBuilder.copy();

        LOGGER.info("Building the Minecraft instance...");
        packBuilder.buildMc();
    }

    @Override
    protected void parseArgs(String[] args)
    {
        for (int i = 0; i < args.length; i++)
        {
            switch (args[i])
            {
                case "nogui":
                    headless = true;
                    break;
                case "packid":
                    if (i + 1 < args.length) packID = args[++ i];
                    break;
                case "name":
                    if (i + 1 < args.length) name = args[++ i];
                    break;
                case "ignoreIncomplete":
                    ignoreIncomplete = true;
                    break;
                case "override":
                    override = true;
                    break;
                default:
                    LOGGER.warn(String.format("Unused argument: %s", args[i]));
                    // No break because we want the help to display.
                case "help":
                case "?":
                    LOGGER.warn("Proper arguments:");
                    LOGGER.warn("-----------------");
                    LOGGER.warn("nogiu: Do all interaction via command prompt");
                    LOGGER.warn("packid <id>: Set the packid. If not set, this is asked later.");
                    LOGGER.warn("name <name>: Override the default name (for the instance folder). Defaults to the packid.");
                    LOGGER.warn("ignoreIncomplete: Ignores mods that can't be found and make the server anyways. Experts only!");
                    LOGGER.warn("override: Override the files in the instance folder without asking. Experts only!");
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void makeFolders()
    {
        super.makeFolders();
        SERVER_INSTANCES.mkdir();
    }
}
