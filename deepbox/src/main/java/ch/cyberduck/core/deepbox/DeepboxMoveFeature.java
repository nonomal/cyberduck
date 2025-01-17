package ch.cyberduck.core.deepbox;

/*
 * Copyright (c) 2002-2024 iterate GmbH. All rights reserved.
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

import ch.cyberduck.core.Acl;
import ch.cyberduck.core.ConnectionCallback;
import ch.cyberduck.core.LocaleFactory;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.deepbox.io.swagger.client.ApiException;
import ch.cyberduck.core.deepbox.io.swagger.client.api.CoreRestControllerApi;
import ch.cyberduck.core.deepbox.io.swagger.client.model.NodeMove;
import ch.cyberduck.core.deepbox.io.swagger.client.model.NodeUpdate;
import ch.cyberduck.core.exception.AccessDeniedException;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.features.Move;
import ch.cyberduck.core.transfer.TransferStatus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;

import static ch.cyberduck.core.deepbox.DeepboxAttributesFinderFeature.*;

public class DeepboxMoveFeature implements Move {
    private static final Logger log = LogManager.getLogger(DeepboxMoveFeature.class);

    private final DeepboxSession session;
    private final DeepboxIdProvider fileid;

    public DeepboxMoveFeature(final DeepboxSession session, final DeepboxIdProvider fileid) {
        this.session = session;
        this.fileid = fileid;
    }

    @Override
    public Path move(final Path file, final Path renamed, final TransferStatus status, final Delete.Callback delete, final ConnectionCallback callback) throws BackgroundException {
        try {
            if(status.isExists()) {
                log.warn("Delete file {} to be replaced with {}", renamed, file);
                new DeepboxTrashFeature(session, fileid).delete(Collections.singletonList(renamed), callback, delete);
            }
            final String sourceId = fileid.getFileId(file);
            final NodeMove nodeMove = new NodeMove();
            final String targetParentId = fileid.getFileId(renamed.getParent());
            nodeMove.setTargetParentNodeId(targetParentId);
            new CoreRestControllerApi(session.getClient()).moveNode(nodeMove, sourceId);
            final NodeUpdate nodeUpdate = new NodeUpdate();
            nodeUpdate.setName(renamed.getName());
            new CoreRestControllerApi(session.getClient()).updateNode(nodeUpdate, sourceId);
            fileid.cache(file, null);
            fileid.cache(renamed, sourceId);
            return renamed.withAttributes(file.attributes().withFileId(sourceId));
        }
        catch(ApiException e) {
            throw new DeepboxExceptionMappingService(fileid).map("Cannot rename {0}", e, file);
        }
    }

    @Override
    public EnumSet<Flags> features(final Path source, final Path target) {
        return EnumSet.of(Flags.recursive);
    }

    @Override
    public void preflight(final Path source, final Optional<Path> optional) throws BackgroundException {
        if(source.isRoot() || new DeepboxPathContainerService(session, fileid).isContainer(source) || new DeepboxPathContainerService(session, fileid).isInTrash(source)) {
            throw new AccessDeniedException(MessageFormat.format(LocaleFactory.localizedString("Cannot rename {0}", "Error"), source.getName())).withFile(source);
        }
        if(optional.isPresent()) {
            final Path target = optional.get();
            if(target.isRoot() || new DeepboxPathContainerService(session, fileid).isContainer(target) || new DeepboxPathContainerService(session, fileid).isInTrash(target)) {
                throw new AccessDeniedException(MessageFormat.format(LocaleFactory.localizedString("Cannot create {0}", "Error"), target.getName())).withFile(source);
            }
            final Acl acl = source.attributes().getAcl();
            if(Acl.EMPTY == acl) {
                // Missing initialization
                log.warn("Unknown ACLs on {}", source);
                return;
            }
            if(!source.getName().equals(target.getName())) {
                if(!acl.get(new Acl.CanonicalUser()).contains(CANRENAME)) {
                    log.warn("ACL {} for {} does not include {}", acl, source, CANRENAME);
                    throw new AccessDeniedException(MessageFormat.format(LocaleFactory.localizedString("Cannot rename {0}", "Error"), source.getName())).withFile(source);
                }
            }
            if(!fileid.getFileId(source.getParent()).equals(fileid.getFileId(target.getParent()))) {
                if(fileid.getBoxNodeId(source.getParent()).equals(fileid.getBoxNodeId(target.getParent()))) {
                    if(!acl.get(new Acl.CanonicalUser()).contains(CANMOVEWITHINBOX)) {
                        log.warn("ACL {} for {} does not include {}", acl, source, CANMOVEWITHINBOX);
                        throw new AccessDeniedException(MessageFormat.format(LocaleFactory.localizedString("Cannot rename {0}", "Error"), source.getName())).withFile(source);
                    }
                }
                else {
                    if(!acl.get(new Acl.CanonicalUser()).contains(CANMOVEOUTOFBOX)) {
                        log.warn("ACL {} for {} does not include {}", acl, source, CANMOVEOUTOFBOX);
                        throw new AccessDeniedException(MessageFormat.format(LocaleFactory.localizedString("Cannot rename {0}", "Error"), source.getName())).withFile(source);
                    }
                }
            }
        }
    }
}
