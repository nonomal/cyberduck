package ch.cyberduck.core.local;

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

import ch.cyberduck.binding.application.NSWorkspace;
import ch.cyberduck.binding.foundation.NSDate;
import ch.cyberduck.binding.foundation.NSDictionary;
import ch.cyberduck.binding.foundation.NSFileManager;
import ch.cyberduck.binding.foundation.NSNumber;
import ch.cyberduck.binding.foundation.NSObject;
import ch.cyberduck.binding.foundation.NSURL;
import ch.cyberduck.core.LocalAttributes;
import ch.cyberduck.core.Permission;
import ch.cyberduck.core.exception.AccessDeniedException;
import ch.cyberduck.core.exception.LocalAccessDeniedException;
import ch.cyberduck.core.exception.LocalNotfoundException;
import ch.cyberduck.core.exception.NotfoundException;

import org.rococoa.ObjCObjectByReference;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSError;

/**
 * Extending attributes with <code>NSFileManager</code>.
 *
 * @see ch.cyberduck.binding.foundation.NSFileManager
 */
public class FinderLocalAttributes extends LocalAttributes {

    private final FinderLocal local;

    public FinderLocalAttributes(final FinderLocal local) {
        super(local.getAbsolute());
        this.local = local;
    }

    private NSDictionary getNativeAttributes() throws AccessDeniedException, NotfoundException {
        if((!local.exists())) {
            throw new LocalNotfoundException(local.getAbsolute());
        }
        final ObjCObjectByReference error = new ObjCObjectByReference();
        // If flag is true and path is a symbolic link, the attributes of the linked-to file are returned;
        // if the link points to a nonexistent file, this method returns null. If flag is false,
        // the attributes of the symbolic link are returned.
        final NSDictionary dict = NSFileManager.defaultManager().attributesOfItemAtPath_error(
            local.getAbsolute(), error);
        if(null == dict) {
            final NSError f = error.getValueAs(NSError.class);
            if(null == f) {
                throw new LocalAccessDeniedException(local.getAbsolute());
            }
            throw new LocalAccessDeniedException(String.format("%s", f.localizedDescription()));
        }
        return dict;
    }

    private NSObject getNativeAttribute(final String name) throws AccessDeniedException, NotfoundException {
        final NSDictionary dict = this.getNativeAttributes();
        // Returns an entry’s value given its key, or null if no value is associated with key.
        return dict.objectForKey(name);
    }

    /**
     * Read <code>NSFileCreationDate</code>.
     *
     * @return Milliseconds since 1970
     */
    @Override
    public long getCreationDate() {
        try {
            final NSObject object = this.getNativeAttribute(NSFileManager.NSFileCreationDate);
            if(object.isKindOfClass(Rococoa.createClass("NSDate", NSDate._Class.class))) {
                return (long) (Rococoa.cast(object, NSDate.class).timeIntervalSince1970() * 1000);
            }
            return -1;
        }
        catch(AccessDeniedException | NotfoundException e) {
            return -1;
        }
    }

    @Override
    public String getOwner() {
        try {
            final NSObject object = this.getNativeAttribute(NSFileManager.NSFileOwnerAccountName);
            return object.toString();
        }
        catch(AccessDeniedException | NotfoundException e) {
            return null;
        }
    }

    @Override
    public String getGroup() {
        try {
            final NSObject object = this.getNativeAttribute(NSFileManager.NSFileGroupOwnerAccountName);
            return object.toString();
        }
        catch(AccessDeniedException | NotfoundException e) {
            return null;
        }
    }

    @Override
    public Permission getPermission() {
        return new FinderLocalPermission(super.getPermission().getMode());
    }

    /**
     * @return The value for the key NSFileSystemFileNumber, or 0 if the receiver doesn’t have an entry for the key
     */
    public Long getInode() {
        try {
            final NSObject object = this.getNativeAttribute(NSFileManager.NSFileSystemFileNumber);
            if(object.isKindOfClass(Rococoa.createClass("NSNumber", NSNumber._Class.class))) {
                final NSNumber number = Rococoa.cast(object, NSNumber.class);
                return number.longValue();
            }
            return null;
        }
        catch(AccessDeniedException | NotfoundException e) {
            return null;
        }
    }

    @Override
    public boolean isBundle() {
        return NSWorkspace.sharedWorkspace().isFilePackageAtPath(local.getAbsolute());
    }

    /**
     * Executable, readable and writable flags based on <code>NSFileManager</code>.
     */
    private final class FinderLocalPermission extends LocalPermission {
        private FinderLocalPermission() {
            //
        }

        private FinderLocalPermission(final String mode) {
            super(mode);
        }

        private FinderLocalPermission(final int mode) {
            super(mode);
        }

        @Override
        public boolean isExecutable() {
            final NSURL resolved;
            try {
                resolved = local.lock(false);
                if(null == resolved) {
                    return super.isExecutable();
                }
            }
            catch(AccessDeniedException e) {
                return super.isExecutable();
            }
            try {
                return NSFileManager.defaultManager().isExecutableFileAtPath(resolved.path());
            }
            finally {
                local.release(resolved);
            }
        }

        @Override
        public boolean isReadable() {
            final NSURL resolved;
            try {
                resolved = local.lock(false);
                if(null == resolved) {
                    return super.isReadable();
                }
            }
            catch(AccessDeniedException e) {
                return super.isReadable();
            }
            try {
                return NSFileManager.defaultManager().isReadableFileAtPath(resolved.path());
            }
            finally {
                local.release(resolved);
            }
        }

        @Override
        public boolean isWritable() {
            final NSURL resolved;
            try {
                resolved = local.lock(false);
                if(null == resolved) {
                    return super.isWritable();
                }
            }
            catch(AccessDeniedException e) {
                return super.isWritable();
            }
            try {
                return NSFileManager.defaultManager().isWritableFileAtPath(resolved.path());
            }
            finally {
                local.release(resolved);
            }
        }
    }
}
