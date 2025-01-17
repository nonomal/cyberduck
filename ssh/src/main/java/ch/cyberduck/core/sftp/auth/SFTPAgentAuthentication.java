package ch.cyberduck.core.sftp.auth;

/*
 * Copyright (c) 2002-2017 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.AuthenticationProvider;
import ch.cyberduck.core.Credentials;
import ch.cyberduck.core.DefaultIOExceptionMappingService;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.LocalFactory;
import ch.cyberduck.core.LoginCallback;
import ch.cyberduck.core.exception.AccessDeniedException;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.sftp.SFTPExceptionMappingService;
import ch.cyberduck.core.sftp.openssh.OpenSSHCredentialsConfigurator;
import ch.cyberduck.core.sftp.openssh.OpenSSHIdentitiesOnlyConfigurator;
import ch.cyberduck.core.threading.CancelCallback;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.hierynomus.sshj.userauth.keyprovider.OpenSSHKeyFileUtil;
import com.jcraft.jsch.agentproxy.CustomIdentity;
import com.jcraft.jsch.agentproxy.Identity;
import com.jcraft.jsch.agentproxy.sshj.AuthAgent;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.Buffer;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.userauth.keyprovider.KeyFormat;
import net.schmizz.sshj.userauth.keyprovider.KeyProviderUtil;

public class SFTPAgentAuthentication implements AuthenticationProvider<Boolean> {
    private static final Logger log = LogManager.getLogger(SFTPAgentAuthentication.class);

    private final SSHClient client;
    private final AgentAuthenticator agent;

    public SFTPAgentAuthentication(final SSHClient client, final AgentAuthenticator agent) {
        this.client = client;
        this.agent = agent;
    }

    @Override
    public Boolean authenticate(final Host bookmark, final LoginCallback prompt, final CancelCallback cancel) throws BackgroundException {
        log.debug("Login using agent {} for {}", agent, bookmark);
        if(agent.getIdentities().isEmpty()) {
            log.warn("Skip authentication with agent {} with no identity available", agent);
            return false;
        }
        final Collection<Identity> identities;
        if(new OpenSSHIdentitiesOnlyConfigurator().isIdentitiesOnly(bookmark.getHostname())) {
            final Credentials configuration = new OpenSSHCredentialsConfigurator().configure(bookmark);
            if(configuration.isPublicKeyAuthentication()) {
                try {
                    final Local setting = configuration.getIdentity();
                    log.warn("Only read specific key {} from SSH agent with IdentitiesOnly configuration", setting);
                    final Identity identity = isPrivateKey(setting) ?
                            identityFromPrivateKey(setting) :
                            identityFromPublicKey(setting);
                    if(identity != null) {
                        identities = Collections.singleton(identity);
                    }
                    else {
                        log.warn("Missing public key for {}", setting);
                        identities = Collections.emptyList();
                    }
                }
                catch(IOException e) {
                    throw new DefaultIOExceptionMappingService().map(e);
                }
            }
            else {
                log.warn("Missing IdentityFile configuration for {}", bookmark);
                identities = Collections.emptyList();
            }
        }
        else {
            identities = this.filter(bookmark.getCredentials(), agent.getIdentities());
        }
        for(Identity identity : identities) {
            try {
                client.auth(bookmark.getCredentials().getUsername(), new AuthAgent(agent.getProxy(), identity));
                // Successfully authenticated
                break;
            }
            catch(UserAuthException e) {
                cancel.verify();
                // Continue;
            }
            catch(Buffer.BufferException e) {
                throw new DefaultIOExceptionMappingService().map(e);
            }
            catch(TransportException e) {
                throw new SFTPExceptionMappingService().map(e);
            }
        }
        return client.isAuthenticated();
    }

    @Override
    public String getMethod() {
        return "publickey";
    }

    protected Collection<Identity> filter(final Credentials credentials, final Collection<Identity> identities) {
        if(credentials.isPublicKeyAuthentication()) {
            final Local selected = credentials.getIdentity();
            for(Identity identity : identities) {
                if(identity.getComment() != null) {
                    final String candidate = new String(identity.getComment(), StandardCharsets.UTF_8);
                    if(selected.getAbsolute().equals(candidate)) {
                        log.debug("Matching identity {} found", candidate);
                        return Collections.singletonList(identity);
                    }
                }
            }
        }
        return identities;
    }

    static boolean isPrivateKey(final Local identity) throws AccessDeniedException, IOException {
        final KeyFormat format = KeyProviderUtil.detectKeyFileFormat(
                new InputStreamReader(identity.getInputStream()), true);
        return format != KeyFormat.Unknown;
    }

    static Identity identityFromPrivateKey(final Local identity) throws IOException, AccessDeniedException {
        final File pubKey = OpenSSHKeyFileUtil.getPublicKeyFile(new File(identity.getAbsolute()));
        if(pubKey != null) {
            return identityFromPublicKey(LocalFactory.get(pubKey.getAbsolutePath()));
        }
        log.warn("Unable to find public key file for identity {}", identity);
        return null;
    }

    static Identity identityFromPublicKey(final Local identity) throws IOException, AccessDeniedException {
        final List<String> lines = IOUtils.readLines(identity.getInputStream(), Charset.defaultCharset());
        for(String line : lines) {
            final String keydata = line.trim();
            if(StringUtils.isNotBlank(keydata)) {
                String[] parts = keydata.split("\\s+");
                if(parts.length >= 2) {
                    return new CustomIdentity(Base64.decodeBase64(parts[1]));
                }
            }
        }
        log.warn("Failure reading public key {}", identity);
        return null;
    }
}