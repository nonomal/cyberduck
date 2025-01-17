package ch.cyberduck.core.smb;

/*
 * Copyright (c) 2002-2023 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.AbstractPath;
import ch.cyberduck.core.AttributedList;
import ch.cyberduck.core.Credentials;
import ch.cyberduck.core.ListProgressListener;
import ch.cyberduck.core.ListService;
import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.LoginOptions;
import ch.cyberduck.core.PasswordCallback;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.exception.AccessDeniedException;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.NotfoundException;
import ch.cyberduck.core.exception.UnsupportedException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.hierynomus.smbj.session.Session;
import com.rapid7.client.dcerpc.mssrvs.ServerService;
import com.rapid7.client.dcerpc.mssrvs.dto.NetShareInfo;
import com.rapid7.client.dcerpc.mssrvs.dto.NetShareInfo0;
import com.rapid7.client.dcerpc.transport.RPCTransport;
import com.rapid7.client.dcerpc.transport.SMBTransportFactories;
import com.rapid7.helper.smbj.io.SMB2Exception;

public class SMBRootListService implements ListService {
    private static final Logger log = LogManager.getLogger(SMBRootListService.class);

    private final SMBSession session;
    private final PasswordCallback prompt;
    private final Session context;

    public SMBRootListService(final SMBSession session, final PasswordCallback prompt, final Session context) {
        this.session = session;
        this.prompt = prompt;
        this.context = context;
    }

    @Override
    public AttributedList<Path> list(final Path directory, final ListProgressListener listener) throws BackgroundException {
        if(directory.isRoot()) {
            try {
                log.debug("Attempt to list available shares");
                // An SRVSVC_HANDLE pointer that identifies the server.
                final RPCTransport transport = SMBTransportFactories.SRVSVC.getTransport(context);
                log.debug("Obtained transport {}", transport);
                final ServerService lookup = new ServerService(transport);
                final List<NetShareInfo0> info = lookup.getShares0();
                log.debug("Retrieved share info {}", info);
                final AttributedList<Path> result = new AttributedList<>();
                for(final String s : info.stream().map(NetShareInfo::getNetName).collect(Collectors.toSet())) {
                    final Path share = new Path(s, EnumSet.of(AbstractPath.Type.directory, AbstractPath.Type.volume));
                    try {
                        result.add(share.withAttributes(new SMBAttributesFinderFeature(session).find(share)));
                    }
                    catch(NotfoundException | AccessDeniedException | UnsupportedException e) {
                        log.warn("Skip unsupported share {} with failure {}", s, e);
                    }
                    listener.chunk(directory, result);
                }
                return result;
            }
            catch(SMB2Exception e) {
                log.warn("Failure {} getting share info from server", e.getMessage());
                final Credentials name = prompt.prompt(session.getHost(),
                        LocaleFactory.localizedString("SMB Share"),
                        LocaleFactory.localizedString("Enter the pathname to list:", "Goto"),
                        new LoginOptions().icon(session.getHost().getProtocol().disk()).keychain(false)
                                .passwordPlaceholder(LocaleFactory.localizedString("SMB Share"))
                                .password(false));
                log.debug("Connect to share {} from user input", name.getPassword());
                final Path share = new Path(name.getPassword(), EnumSet.of(Path.Type.directory, Path.Type.volume));
                final AttributedList<Path> result = new AttributedList<>(Collections.singleton(share.withAttributes(new SMBAttributesFinderFeature(session).find(share))));
                listener.chunk(directory, result);
                return result;
            }
            catch(IOException e) {
                throw new SMBTransportExceptionMappingService().map("Cannot read container configuration", e);
            }
        }
        return new SMBListService(session).list(directory, listener);
    }
}
