package ch.cyberduck.core.exception;

/*
 * Copyright (c) 2002-2014 David Kocher. All rights reserved.
 * http://cyberduck.ch/
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
 * Bug fixes, suggestions and comments should be sent to feedback@cyberduck.ch
 */

import ch.cyberduck.core.LocaleFactory;

public class ResolveFailedException extends BackgroundException {
    private static final long serialVersionUID = 5213700700233301556L;

    public ResolveFailedException() {
        super(LocaleFactory.localizedString("Connection failed", "Error"), (Throwable) null);
    }

    public ResolveFailedException(final String detail, final Throwable cause) {
        super(LocaleFactory.localizedString("Connection failed", "Error"), detail, cause);
    }

    @Override
    public String getHelp() {
        return LocaleFactory.localizedString("DNS is the network service that translates a server name to " +
                "its Internet address. This error is most often caused by having no connection to the " +
                "Internet or a misconfigured network. It can also be caused by an unresponsive DNS server " +
                "or a firewall preventing access to the network.", "Support");
    }
}
