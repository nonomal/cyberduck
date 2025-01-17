package ch.cyberduck.core.ctera;

/*
 * Copyright (c) 2002-2022 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.dav.DAVMoveFeature;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.InvalidFilenameException;

import java.text.MessageFormat;
import java.util.Optional;

import static ch.cyberduck.core.ctera.CteraAttributesFinderFeature.*;

public class CteraMoveFeature extends DAVMoveFeature {

    public CteraMoveFeature(final CteraSession session) {
        super(session);
    }

    @Override
    public void preflight(final Path source, final Optional<Path> optional) throws BackgroundException {
        assumeRole(source, DELETEPERMISSION);
        if(optional.isPresent()) {
            final Path target = optional.get();
            if(!CteraTouchFeature.validate(target.getName())) {
                throw new InvalidFilenameException(MessageFormat.format(LocaleFactory.localizedString("Cannot rename {0}", "Error"), target.getName())).withFile(source);
            }
            // defaults to Acl.EMPTY (disabling role checking) if target does not exist
            assumeRole(target, WRITEPERMISSION);
            // no createfilespermission required for now
            if(source.isDirectory()) {
                assumeRole(target.getParent(), target.getName(), CREATEDIRECTORIESPERMISSION);
            }
        }
    }
}
