package ch.cyberduck.core;

/*
 *  Copyright (c) 2005 David Kocher. All rights reserved.
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

import ch.cyberduck.core.serializer.Serializer;

import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;

public class Path extends AbstractPath implements Referenceable, Serializable {

    /**
     * The path delimiter for remote paths
     */
    public static final char DELIMITER = '/';
    /**
     * Reference to the parent
     */
    protected Path parent;
    /**
     * The absolute remote path
     */
    private String path;
    /**
     * The target of the symbolic link if this path denotes a symbolic link or null
     */
    private Path symlink;
    /**
     * The file type
     */
    private EnumSet<Type> type;

    /**
     * File attributes
     */
    private PathAttributes attributes;

    public Path(final Path copy) {
        this.parent = copy.parent;
        this.path = copy.path;
        this.symlink = null == copy.symlink ? null : new Path(copy.symlink);
        this.type = EnumSet.copyOf(copy.type);
        this.attributes = new PathAttributes(copy.attributes);
    }

    /**
     * @param parent Parent path
     * @param name   The filename relative to parent
     * @param type   File types
     */
    public Path(final Path parent, final String name, final EnumSet<Type> type) {
        this.type = type;
        this.attributes = new PathAttributes();
        this.attributes.setRegion(parent.attributes.getRegion());
        this._setPath(parent, name);
    }

    /**
     * @param absolute The absolute path of the remote file
     * @param type     File types
     */
    public Path(final String absolute, final EnumSet<Type> type) {
        this.type = type;
        this.attributes = new PathAttributes();
        this.setPath(absolute);
    }

    /**
     * @param absolute   The absolute path of the remote file
     * @param type       File types
     * @param attributes File attributes
     */
    public Path(final String absolute, final EnumSet<Type> type, final PathAttributes attributes) {
        this.type = type;
        this.attributes = attributes;
        this.setPath(absolute);
    }

    /**
     * @param parent     Parent path
     * @param name       The filename relative to parent
     * @param attributes Attributes
     */
    public Path(final Path parent, final String name, final EnumSet<Type> type, final PathAttributes attributes) {
        this.type = type;
        this.attributes = attributes;
        this._setPath(parent, name);
    }

    @Override
    public <T> T serialize(final Serializer<T> dict) {
        dict.setStringForKey(String.valueOf(type), "Type");
        dict.setStringForKey(this.getAbsolute(), "Remote");
        if(symlink != null) {
            dict.setObjectForKey(symlink, "Symbolic Link");
        }
        dict.setObjectForKey(attributes, "Attributes");
        return dict.getSerialized();
    }

    private void setPath(final String absolute) {
        if(String.valueOf(Path.DELIMITER).equals(absolute)) {
            this._setPath(null, absolute);
        }
        else {
            final Path parent = new Path(PathNormalizer.parent(absolute, Path.DELIMITER), EnumSet.of(Type.directory));
            parent.attributes().setRegion(attributes.getRegion());
            if(parent.isRoot()) {
                parent.setType(EnumSet.of(Type.volume, Type.directory));
            }
            this._setPath(parent, PathNormalizer.name(absolute));
        }
    }

    private void _setPath(final Path parent, final String name) {
        this.parent = parent;
        if(null == parent) {
            this.path = name;
        }
        else {
            if(parent.isRoot()) {
                this.path = parent.getAbsolute() + name;
            }
            else {
                if(name.startsWith(String.valueOf(DELIMITER))) {
                    this.path = parent.getAbsolute() + name;
                }
                else {
                    this.path = parent.getAbsolute() + Path.DELIMITER + name;
                }
            }
        }
    }

    @Override
    public EnumSet<Type> getType() {
        return type;
    }

    public void setType(final EnumSet<Type> type) {
        this.type = type;
    }

    public Path withType(final EnumSet<Type> type) {
        this.setType(type);
        return this;
    }

    public boolean isVolume() {
        return type.contains(Type.volume);
    }

    public boolean isDirectory() {
        return type.contains(Type.directory);
    }

    public boolean isPlaceholder() {
        return type.contains(Type.placeholder);
    }

    public boolean isFile() {
        return type.contains(Type.file);
    }

    public boolean isSymbolicLink() {
        return type.contains(Type.symboliclink);
    }

    @Override
    public char getDelimiter() {
        return String.valueOf(DELIMITER).charAt(0);
    }

    public Path getParent() {
        if(this.isRoot()) {
            return this;
        }
        return parent;
    }

    public PathAttributes attributes() {
        return attributes;
    }

    public void setAttributes(final PathAttributes attributes) {
        this.attributes = attributes;
    }

    public Path withAttributes(final PathAttributes attributes) {
        this.setAttributes(attributes);
        return this;
    }

    /**
     * @return The path relative to its parent directory
     */
    @Override
    public String getName() {
        if(this.isRoot()) {
            return String.valueOf(DELIMITER);
        }
        if(parent.isRoot()) {
            return StringUtils.substringAfter(path, parent.getAbsolute());
        }
        return StringUtils.substringAfter(path, parent.getAbsolute() + Path.DELIMITER);
    }

    /**
     * @return the absolute path name, e.g. /home/user/filename
     */
    @Override
    public String getAbsolute() {
        return path;
    }

    /**
     * @return The target of the symbolic link if this path denotes a symbolic link
     * @see #isSymbolicLink
     */
    public Path getSymlinkTarget() {
        return symlink;
    }

    public void setSymlinkTarget(final Path target) {
        this.symlink = target;
    }

    /**
     * @return The hashcode of #getAbsolute()
     * @see #getAbsolute()
     */
    @Override
    public int hashCode() {
        return new DefaultPathPredicate(this).hashCode();
    }

    /**
     * @param other Path to compare with
     * @return true if the other path has the same absolute path name
     */
    @Override
    public boolean equals(Object other) {
        if(null == other) {
            return false;
        }
        if(other instanceof Path) {
            return new DefaultPathPredicate(this).test((Path) other);
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Path{");
        sb.append("path='").append(path).append('\'');
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }

    /**
     * @param directory Parent directory
     * @return True if this is a child in the path hierarchy of the argument passed
     */
    public boolean isChild(final Path directory) {
        if(directory.isFile()) {
            // If a file we don't have any children at all
            return false;
        }
        return new SimplePathPredicate(this).isChild(new SimplePathPredicate(directory));
    }
}
