package ch.cyberduck.core.local;

/*
 * Copyright (c) 2002-2014 David Kocher. All rights reserved.
 * http://cyberduck.io/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Bug fixes, suggestions and comments should be sent to:
 * feedback@cyberduck.io
 */

import ch.cyberduck.core.Local;
import ch.cyberduck.core.threading.DefaultThreadPool;
import ch.cyberduck.core.threading.ThreadPool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.Callable;

public class ExecApplicationLauncher implements ApplicationLauncher {
    private static final Logger log = LogManager.getLogger(ExecApplicationLauncher.class);

    private final Runtime runtime = Runtime.getRuntime();

    private final ThreadPool pool
            = new DefaultThreadPool("process", 1);

    @Override
    public boolean open(final Local file) {
        try {
            runtime.exec(String.format("xdg-open %s", file.getAbsolute()));
            return true;
        }
        catch(IOException e) {
            log.warn("Failure launching application {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean open(final Local file, final Application application) {
        try {
            final Process process = runtime.exec(String.format("%s %s", application.getIdentifier(), file.getAbsolute()));
            pool.execute(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    try {
                        process.waitFor();
                        return true;
                    }
                    catch(InterruptedException e) {
                        log.warn("Failure waiting for application {} to exit", process);
                        return false;
                    }
                }
            });
            return true;
        }
        catch(IOException e) {
            log.warn("Failure launching application {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean open(final Application application, final String args) {
        try {
            runtime.exec(String.format("%s %s", application.getIdentifier(), args));
            return true;
        }
        catch(IOException e) {
            log.warn("Failure launching application {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void bounce(final Local file) {
        //
    }
}
