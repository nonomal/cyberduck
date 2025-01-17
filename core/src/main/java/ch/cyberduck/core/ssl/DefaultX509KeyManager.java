package ch.cyberduck.core.ssl;

/*
 *  Copyright (c) 2008 David Kocher. All rights reserved.
 *  http://cyberduck.ch/
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *  dkocher@cyberduck.ch
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation to choose certificates from key store.
 */
public class DefaultX509KeyManager extends AbstractX509KeyManager implements X509KeyManager {
    private static final Logger log = LogManager.getLogger(DefaultX509KeyManager.class);

    private javax.net.ssl.X509KeyManager _manager;

    @Override
    public X509KeyManager init() {
        return this;
    }

    private synchronized javax.net.ssl.X509KeyManager getKeystore() throws IOException {
        try {
            if(null == _manager) {
                // Get the key manager factory for the default algorithm.
                final KeyManagerFactory factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                final KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
                // Load default key store
                store.load(null);
                // Load default key manager factory using key store
                factory.init(store, null);
                for(KeyManager m : factory.getKeyManagers()) {
                    if(m instanceof javax.net.ssl.X509KeyManager) {
                        // Get the first X509KeyManager in the list
                        _manager = (javax.net.ssl.X509KeyManager) m;
                        break;
                    }
                }
                if(null == _manager) {
                    throw new NoSuchAlgorithmException(String.format("The default algorithm %s did not produce a X.509 key manager",
                            KeyManagerFactory.getDefaultAlgorithm()));
                }
            }
            return _manager;
        }
        catch(CertificateException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
            log.error("Initialization of key store failed. {}", e.getMessage());
            throw new IOException(e);
        }
    }

    @Override
    public X509Certificate getCertificate(final String alias, final String[] keyTypes, final Principal[] issuers) {
        for(X509Certificate cert : _manager.getCertificateChain(alias)) {
            if(this.matches(cert, keyTypes, issuers)) {
                return cert;
            }
        }
        return null;
    }

    @Override
    public List<String> list() {
        final List<String> list = new ArrayList<>();
        try {
            final javax.net.ssl.X509KeyManager manager = this.getKeystore();
            {
                final String[] aliases = manager.getClientAliases("RSA", null);
                if(null != aliases) {
                    Collections.addAll(list, aliases);
                }
            }
            {
                final String[] aliases = manager.getClientAliases("DSA", null);
                if(null != aliases) {
                    Collections.addAll(list, aliases);
                }
            }
        }
        catch(IOException e) {
            log.warn("Failure listing aliases. {}", e.getMessage());
            return Collections.emptyList();
        }
        return list;
    }

    @Override
    public String[] getClientAliases(final String keyType, final Principal[] issuers) {
        final javax.net.ssl.X509KeyManager manager;
        try {
            manager = this.getKeystore();
        }
        catch(IOException e) {
            return null;
        }
        return manager.getClientAliases(keyType, issuers);
    }

    /**
     * Choose an alias to authenticate the client side of a secure socket given the public key type and the list of
     * certificate issuer authorities recognized by the peer (if any).
     *
     * @param keyType the key algorithm type name(s), ordered with the most-preferred key type first
     * @param issuers the list of acceptable CA issuer subject names or null if it does not matter which issuers are used
     * @param socket  the socket to be used for this connection. This parameter can be null, which indicates that
     *                implementations are free to select an alias applicable to any socket
     */
    @Override
    public String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
        final javax.net.ssl.X509KeyManager manager;
        try {
            manager = this.getKeystore();
        }
        catch(IOException e) {
            return null;
        }
        return manager.chooseClientAlias(keyType, issuers, socket);
    }

    @Override
    public String[] getServerAliases(final String keyType, final Principal[] issuers) {
        final javax.net.ssl.X509KeyManager manager;
        try {
            manager = this.getKeystore();
        }
        catch(IOException e) {
            return null;
        }
        return manager.getServerAliases(keyType, issuers);
    }

    @Override
    public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        final javax.net.ssl.X509KeyManager manager;
        try {
            manager = this.getKeystore();
        }
        catch(IOException e) {
            return null;
        }
        return manager.chooseServerAlias(keyType, issuers, socket);
    }

    /**
     * Returns the certificate chain associated with the given alias.
     */
    @Override
    public X509Certificate[] getCertificateChain(final String alias) {
        final javax.net.ssl.X509KeyManager manager;
        try {
            manager = this.getKeystore();
        }
        catch(IOException e) {
            return null;
        }
        return manager.getCertificateChain(alias);
    }

    @Override
    public PrivateKey getPrivateKey(final String alias) {
        final javax.net.ssl.X509KeyManager manager;
        try {
            manager = this.getKeystore();
        }
        catch(IOException e) {
            return null;
        }
        return manager.getPrivateKey(alias);
    }
}
