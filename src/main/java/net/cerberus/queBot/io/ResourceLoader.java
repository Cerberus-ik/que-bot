package net.cerberus.queBot.io;

import java.io.*;
import java.net.URL;

/**
 * Simple class for accessing resource files.
 */
public class ResourceLoader {

    public ResourceLoader(){}

    /**
     * Will return the content of a resource file.
     * @param path the resource file path.
     * @return the resource file URL.
     */
    public URL getResourceFile(String path){

        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResource(path);
    }
}
