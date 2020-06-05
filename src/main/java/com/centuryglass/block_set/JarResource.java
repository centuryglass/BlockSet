/**
 * @file JarResource.java
 * 
 * Loads resource file data from this application's .jar file.
 */
package com.centuryglass.block_set;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loads resource file data from this application's .jar file.
 */
public class JarResource
{
    private static final String CLASSNAME = JarResource.class.getName();
    
    // Buffer size when copying resource files:
    private static final int BUF_SIZE = 50000;
   
    
    /**
     * Gets a jar resource as an input stream.
     * 
     * @param resourcePath  The path to a resource stored in this application's
     *                      jar file.
     * 
     * @return              An open InputStream for the requested resource, or
     *                      null if the resource isn't found.
     */
    public static InputStream getResourceStream(String resourcePath)
    {
        // Resource paths should all start with a leading '/' character, add it
        // if it's not already there.
        if (! (resourcePath.charAt(0) == '/'))
        {
            resourcePath = "/" + resourcePath;
        }
        return JarResource.class.getResourceAsStream(resourcePath);
    }
    
    /**
     * Copies a resource embedded in the application's .jar file to an external
     * file.
     * 
     * @param resourcePath  The path to an embedded application resource.
     * 
     * @param outFile       The file where the resource should be copied.
     * 
     * @throws IOException  If unable to read from the resource or write to the
     *                      output file.
     */
    public static void copyResourceToFile(String resourcePath, File outFile)
            throws IOException
    {
        FileOutputStream fileStream = null;
        boolean outFileExists = outFile.exists();
        if (! outFileExists)
        {
            try
            {
                // Create parent directories if necessary:
                File parentDir = outFile.getParentFile();
                if (parentDir != null && ! parentDir.exists())
                {
                    parentDir.mkdirs();
                }
                outFileExists = outFile.createNewFile();
            }
            catch (IOException e)
            {
                outFileExists = false;
            }
            if (! outFileExists)
            {
                throw new IOException("Unable to create file at '" + outFile
                        + "' to copy resource '" + resourcePath + "'.");
            }
        }
        try (InputStream resourceStream = getResourceStream(resourcePath))
        {
            assert (resourceStream != null ) : "Resource stream was null!";
            fileStream = new FileOutputStream(outFile);
            byte[] buffer = new byte[BUF_SIZE];
            int bytesRead;
            while ((bytesRead = resourceStream.read(buffer)) != -1)
            {
                fileStream.write(buffer, 0, bytesRead);   
            }
        }
        catch (IOException e)
        {
            throw new IOException("Unable to copy resource '" + resourcePath
                    + "', to file: resource not found");
        }
    }
}
