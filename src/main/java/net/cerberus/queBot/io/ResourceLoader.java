package net.cerberus.queBot.io;

import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

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

    public String getResourceFileContent(String path){

        ClassLoader classLoader = getClass().getClassLoader();
        try {
            InputStream inputStream = classLoader.getResourceAsStream(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String temp;
            while((temp = bufferedReader.readLine()) != null){
                stringBuilder.append(temp);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
