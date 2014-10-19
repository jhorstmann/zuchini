package org.zuchini.runner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

abstract class ClasspathScanner {
    protected final ClassLoader classLoader;
    private final List<String> packageNames;
    private final String extension;


    protected ClasspathScanner(ClassLoader classLoader, List<String> packageNames, String extension) {
        this.classLoader = classLoader;
        this.packageNames = packageNames;
        this.extension = extension.startsWith(".") ? extension : "." + extension;
    }

    protected final void scan() throws IOException {
        for (String packageName : packageNames) {
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                scanUrl(resources.nextElement(), path);
            }
        }
    }

    private void scanUrl(URL url, String pathPrefix) throws IOException {
        if ("file".equals(url.getProtocol())) {
            File file = new File(url.getFile());
            if (file.isDirectory()) {
                scanDirectory(file, pathPrefix);
            } else if (file.isFile() && file.getName().endsWith(".jar")) {
                scanJar(file, pathPrefix);
            }
        } else if ("jar".equals(url.getProtocol())) {
            scanUrl(new URL(url.toString().replaceAll("^jar:|!.*$", "")), pathPrefix);
        }
    }

    private void scanDirectory(File directory, String pathPrefix) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = pathPrefix == null || pathPrefix.isEmpty() ? file.getName() : pathPrefix + "/" + file.getName();
                if (file.isFile() && name.endsWith(extension)) {
                    handleResource(name);
                } else if (file.isDirectory()) {
                    scanDirectory(file, name);
                }
            }
        }
    }

    private void scanJar(File file, String pathPrefix) throws IOException {
        JarFile jf = new JarFile(file);
        Enumeration<JarEntry> jarEntries = jf.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String name = jarEntry.getName();
            if (name.startsWith(pathPrefix + "/") && !jarEntry.isDirectory() && name.endsWith(extension)) {
                handleResource(name);
            }
        }
    }

    protected abstract void handleResource(String resourceName) throws IOException;

}
