package ch.cyberduck.core;

import ch.cyberduck.core.exception.BackgroundException;
import ch.cyberduck.core.exception.LoginCanceledException;
import ch.cyberduck.core.features.AttributesFinder;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.features.Directory;
import ch.cyberduck.core.features.Move;
import ch.cyberduck.core.features.Read;
import ch.cyberduck.core.features.Upload;
import ch.cyberduck.core.features.Write;
import ch.cyberduck.core.proxy.ProxyFinder;
import ch.cyberduck.core.threading.CancelCallback;

public class NullSession extends Session<Void> implements ListService {

    public NullSession(Host h) {
        super(h);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public Void connect(final ProxyFinder proxy, final HostKeyCallback key, final LoginCallback prompt, final CancelCallback cancel) {
        return null;
    }

    @Override
    public void login(final LoginCallback prompt, final CancelCallback cancel) throws BackgroundException {
        throw new LoginCanceledException();
    }

    @Override
    protected void logout() {
        //
    }

    @Override
    public AttributedList<Path> list(final Path folder, final ListProgressListener listener) throws BackgroundException {
        listener.chunk(folder, AttributedList.emptyList());
        return AttributedList.emptyList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T _getFeature(Class<T> type) {
        if(type == ListService.class) {
            return (T) this;
        }
        if(type == Upload.class) {
            return (T) new NullUploadFeature();
        }
        if(type == Write.class) {
            return (T) new NullWriteFeature();
        }
        if(type == Read.class) {
            return (T) new NullReadFeature();
        }
        if(type == Move.class) {
            return (T) new NullMoveFeature();
        }
        if(type == Delete.class) {
            return (T) new NullDeleteFeature();
        }
        if(type == Directory.class) {
            return (T) new NullDirectoryFeature();
        }
        if(type == AttributesFinder.class) {
            return (T) new NullAttributesFinder();
        }
        return super._getFeature(type);
    }

}

