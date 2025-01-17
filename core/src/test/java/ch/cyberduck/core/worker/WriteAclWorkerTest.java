package ch.cyberduck.core.worker;

import ch.cyberduck.core.Acl;
import ch.cyberduck.core.DisabledProgressListener;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.NullSession;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.TestProtocol;
import ch.cyberduck.core.features.AclPermission;
import ch.cyberduck.core.transfer.TransferStatus;

import org.junit.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class WriteAclWorkerTest {

    @Test
    public void testRunNoFiles() throws Exception {
        final Acl acl = new Acl();
        final WriteAclWorker worker = new WriteAclWorker(Collections.emptyList(), acl, true, new DisabledProgressListener()) {
            @Override
            public void cleanup(final Boolean result) {
                //
            }
        };
        worker.run(new NullSession(new Host(new TestProtocol())) {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T _getFeature(Class<T> type) {
                if(type.equals(AclPermission.class)) {
                    return (T) new AclPermission() {
                        @Override
                        public Acl getPermission(final Path file) {
                            fail();
                            return null;
                        }

                        @Override
                        public void setPermission(final Path file, final TransferStatus status) {
                            fail();
                        }

                        @Override
                        public List<Acl.User> getAvailableAclUsers() {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public List<Acl.Role> getAvailableAclRoles(final List<Path> files) {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
                return super._getFeature(type);
            }
        });
    }

    @Test
    public void testRunEmpty() throws Exception {
        final Acl acl = new Acl();
        final Path t = new Path("/t", EnumSet.of(Path.Type.file));
        final WriteAclWorker worker = new WriteAclWorker(Collections.singletonList(t), acl, true, new DisabledProgressListener()) {
            @Override
            public void cleanup(final Boolean result) {
                //
            }
        };
        worker.run(new NullSession(new Host(new TestProtocol())) {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T _getFeature(Class<T> type) {
                if(type.equals(AclPermission.class)) {
                    return (T) new AclPermission() {
                        @Override
                        public Acl getPermission(final Path file) {
                            fail();
                            return null;
                        }

                        @Override
                        public void setPermission(final Path file, final TransferStatus status) {
                            assertEquals(Acl.EMPTY, status.getAcl());
                        }

                        @Override
                        public List<Acl.User> getAvailableAclUsers() {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public List<Acl.Role> getAvailableAclRoles(final List<Path> files) {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
                return super._getFeature(type);
            }
        });
    }

    @Test
    public void testRunNew() throws Exception {
        final Acl acl = new Acl(new Acl.EmailUser(), new Acl.Role("r"));
        final Path t = new Path("/t", EnumSet.of(Path.Type.file));
        final AtomicBoolean set = new AtomicBoolean();
        final WriteAclWorker worker = new WriteAclWorker(Collections.singletonList(t), acl, true, new DisabledProgressListener()) {
            @Override
            public void cleanup(final Boolean result) {
                //
            }
        };
        worker.run(new NullSession(new Host(new TestProtocol())) {
                       @Override
                       @SuppressWarnings("unchecked")
                       public <T> T _getFeature(Class<T> type) {
                           if(type.equals(AclPermission.class)) {
                               return (T) new AclPermission() {
                                   @Override
                                   public Acl getPermission(final Path file) {
                                       fail();
                                       return null;
                                   }

                                   @Override
                                   public void setPermission(final Path file, final TransferStatus n) {
                                       assertEquals(acl, n.getAcl());
                                       set.set(true);
                                   }

                                   @Override
                                   public List<Acl.User> getAvailableAclUsers() {
                                       throw new UnsupportedOperationException();
                                   }

                                   @Override
                                   public List<Acl.Role> getAvailableAclRoles(final List<Path> files) {
                                       throw new UnsupportedOperationException();
                                   }
                               };
                           }
                           return super._getFeature(type);
                       }
                   }
        );
        assertTrue(set.get()

        );
    }
}
