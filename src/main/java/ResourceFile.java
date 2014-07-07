import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

/**
 * A class to help with finding resource files.
 * @author Luke Terheyden (terheyden@gmail.com)
 */
public class ResourceFile {

    private static final Logger log = LoggerFactory.getLogger(ResourceFile.class);

    private ResourceFile() {}

    /**
     * Reads a file from the class path into a String and returns it.
     * Returns null if it can't be found.
     */
    public static String findString(String filename) {

        try {

            InputStream in = findStream(filename);

            if (in == null) {
                return null;
            }

            Scanner scan = new Scanner(in);
            scan.useDelimiter("\\Z");
            String content = scan.next();
            scan.close();

            return content;

        } catch (Exception e) {
            log.error("Exception finding resource file: " + filename, e);
            return null;
        }
    }

    //////////////////////////////////////////////////////////////////////
    // NOTE that findURL() and findResource() make identical-ish calls.
    // If you change one, change the other...

    /**
     * Finds a resource file as a URL.
     * Returns null if can't be found!
     */
    public static URL findURL(String filename) {

        try {

            URL url = null;
            boolean triedNormal = false;
            boolean triedWithSlash = false;

            // We're going to try getResource("myfile.properties") and getResource("/myfile.properties"):

            while (!triedNormal || !triedWithSlash) {

                if (!triedNormal) {
                    triedNormal = true;
                } else {
                    filename = "/" + filename;
                    triedWithSlash = true;
                }

                url = ClassLoader.getSystemResource(filename);

                if (url != null) {
                    return url;
                }

                url = ClassLoader.getSystemClassLoader().getResource(filename);

                if (url != null) {
                    return url;
                }

                url = ResourceFile.class.getResource(filename);

                if (url != null) {
                    return url;
                }

                url = ResourceFile.class.getClassLoader().getResource(filename);

                if (url != null) {
                    return url;
                }

                url = ResourceFile.class.getClass().getResource(filename);

                if (url != null) {
                    return url;
                }

                url = ResourceFile.class.getClass().getClassLoader().getResource(filename);

                if (url != null) {
                    return url;
                }

                url = log.getClass().getResource(filename);

                if (url != null) {
                    return url;
                }

                url = log.getClass().getClassLoader().getResource(filename);

                if (url != null) {
                    return url;
                }

            } // End while trying different things.

            // Last chance - try just looking in the local folder.

            if (url == null) {

                File resFile = new File(System.getProperty("user.dir"), filename);

                if (resFile.isFile()) {
                    return resFile.toURI().toURL();
                }

                resFile = new File(filename);

                if (resFile.isFile()) {
                    return resFile.toURI().toURL();
                }
            }

            return url;

        } catch (Exception e) {
            log.error("Exception finding resource file: " + filename, e);
            return null;
        }
    }

    /**
     * Finds a resource file as an InputStream.
     * Returns null if it can't be found.
     */
    public static InputStream findStream(String filename) {

        try {

            InputStream in = null;
            boolean triedNormal = false;
            boolean triedWithSlash = false;

            // We're going to try getResource("myfile.properties") and getResource("/myfile.properties"):

            while (!triedNormal || !triedWithSlash) {

                if (!triedNormal) {
                    triedNormal = true;
                } else {
                    filename = "/" + filename;
                    triedWithSlash = true;
                }

                in = ClassLoader.getSystemResourceAsStream(filename);

                if (in != null) {
                    return in;
                }

                in = ClassLoader.getSystemClassLoader().getResourceAsStream(filename);

                if (in != null) {
                    return in;
                }

                in = ResourceFile.class.getResourceAsStream(filename);

                if (in != null) {
                    return in;
                }

                in = ResourceFile.class.getClassLoader().getResourceAsStream(filename);

                if (in != null) {
                    return in;
                }

                in = ResourceFile.class.getClass().getResourceAsStream(filename);

                if (in != null) {
                    return in;
                }

                in = ResourceFile.class.getClass().getClassLoader().getResourceAsStream(filename);

                if (in != null) {
                    return in;
                }

                in = log.getClass().getResourceAsStream(filename);

                if (in != null) {
                    return in;
                }

                in = log.getClass().getClassLoader().getResourceAsStream(filename);

                if (in != null) {
                    return in;
                }

            } // End while trying different things.

            // Last chance - try just looking in the local folder.

            if (in == null) {

                File resFile = new File(System.getProperty("user.dir"), filename);

                if (resFile.isFile()) {
                    return new FileInputStream(resFile);
                }

                resFile = new File(filename);

                if (resFile.isFile()) {
                    return new FileInputStream(resFile);
                }
            }

            return in;

        } catch (Exception e) {
            log.error("Exception finding resource file: " + filename, e);
            return null;
        }
    }

    /**
     * If the resource is a file outside of any JAR, but still in the class path or local dir,
     * return it as a File. Will return null if the file can't be found and verified to exist.
     */
    public static File findFile(String filename) {

        // First find as a URL:

        URL url = findURL(filename);

        if (url == null) {
            return null;
        }

        // Then try to convert it to a file:

        try {

            File resFile = null;

            try {
                resFile = new File(url.toURI());
            } catch (URISyntaxException e) {
                resFile = new File(url.getPath());
            }

            if (resFile == null || !resFile.isFile()) {
                // Looks like it's not a file ... probably embedded in a JAR.
                return null;
            }

            // Found it by some miracle.
            return resFile;

        } catch (Exception e) {
            log.error("Exception converting a resource URL into a file: " + url.toString(), e);
            return null;
        }
    }
}
