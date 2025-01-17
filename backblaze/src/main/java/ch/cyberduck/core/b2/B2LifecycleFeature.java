package ch.cyberduck.core.b2;

/*
 * Copyright (c) 2002-2017 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
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
 */

import ch.cyberduck.core.DefaultIOExceptionMappingService;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathContainerService;
import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.features.Lifecycle;
import ch.cyberduck.core.lifecycle.LifecycleConfiguration;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

import synapticloop.b2.LifecycleRule;
import synapticloop.b2.exception.B2ApiException;
import synapticloop.b2.response.B2BucketResponse;

public class B2LifecycleFeature implements Lifecycle {

    private final PathContainerService containerService
        = new B2PathContainerService();

    private final B2Session session;
    private final B2VersionIdProvider fileid;

    public B2LifecycleFeature(final B2Session session, final B2VersionIdProvider fileid) {
        this.session = session;
        this.fileid = fileid;
    }

    @Override
    public LifecycleConfiguration getConfiguration(final Path container) throws BackgroundException {
        try {
            final B2BucketResponse response = session.getClient().listBucket(containerService.getContainer(container).getName());
            final List<LifecycleRule> lifecycleRules = response.getLifecycleRules();
            for(LifecycleRule rule : lifecycleRules) {
                return new LifecycleConfiguration(
                    null == rule.getDaysFromUploadingToHiding() ? null : rule.getDaysFromUploadingToHiding().intValue(),
                    null == rule.getDaysFromHidingToDeleting() ? null : rule.getDaysFromHidingToDeleting().intValue());
            }
            return LifecycleConfiguration.empty();
        }
        catch(B2ApiException e) {
            throw new B2ExceptionMappingService(fileid).map("Failure to read attributes of {0}", e, container);
        }
        catch(IOException e) {
            throw new DefaultIOExceptionMappingService().map("Failure to read attributes of {0}", e, container);
        }
    }

    @Override
    public void setConfiguration(final Path container, final LifecycleConfiguration configuration) throws BackgroundException {
        try {
            if(LifecycleConfiguration.empty().equals(configuration)) {
                session.getClient().updateBucket(
                    fileid.getVersionId(containerService.getContainer(container)),
                    new B2BucketTypeFeature(session, fileid).toBucketType(container.attributes().getAcl())
                );
            }
            else {
                session.getClient().updateBucket(
                        fileid.getVersionId(containerService.getContainer(container)),
                        new B2BucketTypeFeature(session, fileid).toBucketType(container.attributes().getAcl()),
                        new LifecycleRule(
                                null == configuration.getExpiration() ? null : configuration.getExpiration().longValue(),
                                null == configuration.getTransition() ? null : configuration.getTransition().longValue(),
                                StringUtils.EMPTY)
                );
            }
        }
        catch(B2ApiException e) {
            throw new B2ExceptionMappingService(fileid).map("Failure to write attributes of {0}", e, container);
        }
        catch(IOException e) {
            throw new DefaultIOExceptionMappingService().map("Failure to write attributes of {0}", e, container);
        }
    }
}
