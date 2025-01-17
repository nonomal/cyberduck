﻿using ch.cyberduck.core;
using System;

namespace Ch.Cyberduck.Core.Refresh.Services
{
    public partial class IconProvider<T>
    {
        public T GetPath(Path path, int size)
        {
            var fileIconKey = path.getExtension();
            string key = "path:" + (path.isDirectory() ? "folder" : fileIconKey);
            Func<(string, Func<int, T>)> overlayFactory = default;
            if (path.getType().contains(AbstractPath.Type.decrypted))
            {
                overlayFactory = () => ("unlocked", (int size) => GetResource("unlockedbadge", size));
            }
            else if (path.isSymbolicLink())
            {
                overlayFactory = () => ("alias", (int size) => GetResource("aliasbadge", size));
            }
            else
            {
                var permission = path.attributes().getPermission();
                if (path.isFile() && permission.isExecutable() && string.IsNullOrWhiteSpace(fileIconKey))
                {
                    return GetResource("executable", size);
                }
                else if (path.isDirectory() && Permission.EMPTY != permission)
                {
                    if (!permission.isExecutable())
                    {
                        overlayFactory = () => ("privatefolder", (int size) => GetResource("privatefolderbadge", size));
                    }
                    else if (!permission.isReadable() && permission.isWritable())
                    {
                        overlayFactory = () => ("dropfolder", (int size) => GetResource("dropfolderbadge", size));
                    }
                    else if (!permission.isWritable() && permission.isReadable())
                    {
                        overlayFactory = () => ("readonlyfolder", (int size) => GetResource("readonlyfolderbadge", size));
                    }
                }
            }

            (string Class, Func<int, T> factory) = overlayFactory?.Invoke() ?? default;
            if (IconCache.TryGetIcon(key, size, out T image, Class))
            {
                return image;
            }

            var baseImage = GetFileIcon($".{fileIconKey}", path.isDirectory(), size >= 32, false);
            if (factory is not null)
            {
                baseImage = Overlay(baseImage, factory(size), size);
                IconCache.CacheIcon(key, size, baseImage, Class);
            }

            return baseImage;
        }
    }
}
