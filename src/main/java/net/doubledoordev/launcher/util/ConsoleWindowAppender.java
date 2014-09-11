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
import net.doubledoordev.launcher.client.gui.ConsoleWindow;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

/**
 * @author Dries007
 */
@Plugin(name = "ConsoleWindowAppender", category = "Core", elementType = "appender", printObject = true)
public final class ConsoleWindowAppender extends AbstractAppender
{
    protected ConsoleWindowAppender(String name, Filter filter, Layout<? extends Serializable> layout)
    {
        super(name, filter, layout);
    }

    protected ConsoleWindowAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions)
    {
        super(name, filter, layout, ignoreExceptions);
    }

    @PluginFactory
    public static ConsoleWindowAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("ignoreExceptions") boolean ignoreExceptions, @PluginElement("Layout") Layout layout, @PluginElement("Filters") Filter filter)
    {

        if (name == null)
        {
            LOGGER.error("No name provided for StubAppender");
            return null;
        }

        if (layout == null)
        {
            layout = PatternLayout.createDefaultLayout();
        }
        //noinspection unchecked
        return new ConsoleWindowAppender(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event)
    {
        if (Main.instance.currentSide == Side.CLIENT) ConsoleWindow.INSTANCE.append(new String(getLayout().toByteArray(event)));
    }
}