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

package net.doubledoordev.launcher.client.gui;

import com.google.common.base.Strings;
import net.doubledoordev.launcher.client.Client;
import net.doubledoordev.launcher.util.Constants;
import net.doubledoordev.launcher.util.MiscHelper;
import net.doubledoordev.launcher.util.PackBuilder;
import net.doubledoordev.launcher.util.Side;
import net.doubledoordev.launcher.util.packData.PackData;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static net.doubledoordev.launcher.util.Constants.*;

/**
 * @author Dries007
 */
public class MainGui
{
    public static final MainGui INSTANCE = new MainGui();

    public static JFrame getFrame()
    {
        return INSTANCE.frame;
    }

    private JFrame frame;

    private MainGui()
    {
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    LOGGER.debug("Fetching news...");
                    newspane.setText(IOUtils.toString(new URL(Constants.NEWSURL)));
                    newspane.setSelectionStart(0);
                    newspane.setSelectionEnd(0);
                    LOGGER.debug("News updated.");
                }
                catch (IOException e)
                {
                    LOGGER.warn("Error getting news...");
                }
            }
        });
        addNewPackButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                LOGGER.info("Add pack...");
                String packid = JOptionPane.showInputDialog(frame, "Packid?", "Add a pack", JOptionPane.QUESTION_MESSAGE);
                try
                {
                    LOGGER.info("Grabbing pack data with id %s", packid);
                    PackData packData = GSON.fromJson(IOUtils.toString(new URL(String.format(JSONURL, packid))), PackData.class);
                    packData.id = packid;
                    packData.name = packid;

                    File instanceFolder = packData.getInstanceFolder(Side.CLIENT);
                    LOGGER.info("Folder check %s ...", instanceFolder.getAbsolutePath());
                    while (instanceFolder.exists())
                    {
                        switch (JOptionPane.showConfirmDialog(frame,
                                "The instance folder '" + packData.getInstanceFolder(Side.CLIENT).getName() + "' already exist.\nDo you want to rename the folder?\nIf not, all data in the current folder will be deleted!",
                                "Duplicate pack",
                                JOptionPane.YES_NO_CANCEL_OPTION))
                        {
                            case JOptionPane.YES_OPTION:
                                packData.name = JOptionPane.showInputDialog(frame, "Provide name please", "New name", JOptionPane.QUESTION_MESSAGE);
                                if (Strings.isNullOrEmpty(packData.name)) packData.name = packData.id;
                                instanceFolder = packData.getInstanceFolder(Side.CLIENT);
                                break;
                            case JOptionPane.NO_OPTION:
                                LOGGER.info("Deleting folder... " + MiscHelper.dieFolderDie(instanceFolder));
                                //noinspection ResultOfMethodCallIgnored
                                break;
                            case JOptionPane.CANCEL_OPTION:
                                LOGGER.info("Canceled adding new pack");
                                return;
                        }
                    }
                    //noinspection ResultOfMethodCallIgnored
                    instanceFolder.mkdir();
                    Client.instance.addPack(packData);
                }
                catch (FileNotFoundException e1)
                {
                    LOGGER.warn("Packid %s invalid", e1);
                    JOptionPane.showMessageDialog(frame, "Packid '" + packid + "' does not exist.", "Packid invalid", JOptionPane.ERROR_MESSAGE);
                }
                catch (Exception e1)
                {
                    LOGGER.warn("Something wend wrong.", e1);
                    JOptionPane.showMessageDialog(frame, e1.toString(), "Something went wrong.", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void show()
    {
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                frame = new JFrame(NAME);
                frame.setContentPane(MainGui.INSTANCE.$$$getRootComponent$$$());
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setPreferredSize(new Dimension(800, 500));
                frame.pack();
                frame.setVisible(true);
                LOGGER.debug("Frame show");
            }
        });
    }

    public void updatePacks()
    {
        LOGGER.debug("Updating packs...");
        packsPanel.removeAll();
        LOGGER.debug("Removed all packs");
        int i = 0;
        for (PackData packData : Client.instance.packs)
        {
            LOGGER.debug("Adding panel for %s", packData.name);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = i++;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(5, 5, 5, 5);
            packsPanel.add(getModpackPanel(packData), gbc);
        }

        LOGGER.debug("Added blank.");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = i;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        packsPanel.add(new JPanel(), gbc);
    }

    public JPanel getModpackPanel(final PackData data)
    {
        final JPanel packPanel = new JPanel();
        packPanel.setLayout(new GridBagLayout());

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        packPanel.add(btnPanel, gbc);
        JButton playButton = new JButton();
        playButton.setText("Play");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btnPanel.add(playButton, gbc);
        JButton installServerButton = new JButton();
        installServerButton.setText("Install server");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        installServerButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            LOGGER.info("Making a pack builder...");
                            PackBuilder packBuilder = new PackBuilder(data, Side.SERVER);
                            LOGGER.info("Initiate download...");
                            boolean complete = packBuilder.download();
                            if (!complete)
                            {
                                LOGGER.warn("Missing mods: " + packBuilder.missingMods);
                                LOGGER.warn("Missing configs: " + packBuilder.missingConfigs);
                                if (JOptionPane.showConfirmDialog(null, "Some mods where missing. Do you want to continue?\nMissing mods: " + packBuilder.missingMods + "\nMissing configs: " + packBuilder.missingConfigs, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
                            }
                            LOGGER.info("Copying mod/config files to instance folder...");
                            packBuilder.copy();

                            LOGGER.info("Building the Minecraft instance...");
                            packBuilder.buildMc();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(frame, e.toString(), "Something went wrong.", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }).start();
            }
        });
        btnPanel.add(installServerButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btnPanel.add(spacer1, gbc);
        JButton deleteButton = new JButton();
        deleteButton.setText("Delete");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        deleteButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to remove the client pack '" + data.name + "'\nThis will remove all related files including the worlds!", "Please confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
                {
                    Client.instance.deletePack(data);
                }
            }
        });
        btnPanel.add(deleteButton, gbc);
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        packPanel.add(infoPanel, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Packid: " + data.id);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        infoPanel.add(label1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(spacer2, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Name: " + data.name);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        infoPanel.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("MC: " + data.mcversion);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        infoPanel.add(label3, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(spacer3, gbc);

        return packPanel;
    }

    private JTabbedPane rootTabbedPane;
    private JPanel      newsPanel;
    private JPanel      packsPanel;
    private JButton     addNewPackButton;
    private JEditorPane newspane;

    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$()
    {
        rootTabbedPane = new JTabbedPane();
        newsPanel = new JPanel();
        newsPanel.setLayout(new GridBagLayout());
        rootTabbedPane.addTab("News", newsPanel);
        final JScrollPane scrollPane1 = new JScrollPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 1;
        gbc.ipady = 1;
        newsPanel.add(scrollPane1, gbc);
        newspane = new JEditorPane();
        newspane.setContentType("text/html");
        newspane.setText("<html>\r\n  <head>\r\n    \r\n  </head>\r\n  <body>\r\n    <p style=\"margin-top: 0\">\r\n      Something went wrong when connecting to the news page...\r\n    </p>\r\n  </body>\r\n</html>\r\n");
        scrollPane1.setViewportView(newspane);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        rootTabbedPane.addTab("Packs", panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setHorizontalScrollBarPolicy(31);
        scrollPane2.setVerticalScrollBarPolicy(22);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(scrollPane2, gbc);
        packsPanel = new JPanel();
        packsPanel.setLayout(new GridBagLayout());
        scrollPane2.setViewportView(packsPanel);
        addNewPackButton = new JButton();
        addNewPackButton.setText("Add new pack!");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(addNewPackButton, gbc);
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$()
    { return rootTabbedPane; }
}
