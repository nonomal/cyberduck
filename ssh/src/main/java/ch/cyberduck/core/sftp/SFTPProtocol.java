package ch.cyberduck.core.sftp;

/*
 * Copyright (c) 2002-2013 David Kocher. All rights reserved.
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

import ch.cyberduck.core.AbstractProtocol;
import ch.cyberduck.core.CredentialsConfigurator;
import ch.cyberduck.core.HostnameConfigurator;
import ch.cyberduck.core.JumphostConfigurator;
import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.Protocol;
import ch.cyberduck.core.Scheme;
import ch.cyberduck.core.sftp.openssh.OpenSSHCredentialsConfigurator;
import ch.cyberduck.core.sftp.openssh.OpenSSHHostnameConfigurator;
import ch.cyberduck.core.sftp.openssh.OpenSSHJumpHostConfigurator;

import org.apache.commons.lang3.StringUtils;

import com.google.auto.service.AutoService;

@AutoService(Protocol.class)
public class SFTPProtocol extends AbstractProtocol {

    private final CredentialsConfigurator credentials = new OpenSSHCredentialsConfigurator();
    private final HostnameConfigurator hostnmame = new OpenSSHHostnameConfigurator();
    private final JumphostConfigurator jumphost = new OpenSSHJumpHostConfigurator();

    @Override
    public Type getType() {
        return Type.sftp;
    }

    @Override
    public String getIdentifier() {
        return this.getScheme().name();
    }

    @Override
    public String getPrefix() {
        return String.format("%s.%s", SFTPProtocol.class.getPackage().getName(), StringUtils.upperCase(this.getType().name()));
    }

    @Override
    public String getDescription() {
        return LocaleFactory.localizedString("SFTP (SSH File Transfer Protocol)");
    }

    @Override
    public Scheme getScheme() {
        return Scheme.sftp;
    }

    @Override
    public String disk() {
        return String.format("%s.tiff", "ftp");
    }

    @Override
    public boolean isPrivateKeyConfigurable() {
        return true;
    }

    @Override
    public boolean isEncodingConfigurable() {
        return true;
    }

    @Override
    public DirectoryTimestamp getDirectoryTimestamp() {
        return DirectoryTimestamp.implicit;
    }

    @Override
    public VersioningMode getVersioningMode() {
        return VersioningMode.custom;
    }

    @Override
    public <T> T getFeature(final Class<T> type) {
        if(type == HostnameConfigurator.class) {
            return (T) hostnmame;
        }
        if(type == CredentialsConfigurator.class) {
            return (T) credentials;
        }
        if(type == JumphostConfigurator.class) {
            return (T) jumphost;
        }
        return super.getFeature(type);
    }
}
