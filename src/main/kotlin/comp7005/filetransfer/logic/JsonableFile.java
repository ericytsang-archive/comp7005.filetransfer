package comp7005.filetransfer.logic;

import org.json.JSONObject;

import java.io.File;

/**
 * contains information about a file, that can be serialized to and
 *   deserialized from a JSON object.
 *
 * the purpose of this class is to be able to transfer file data over the
 *   network.
 *
 * @file    JsonableFile.java
 *
 * @program comp7005.filetransfer.jar
 *
 * @class   JsonableFile
 *
 * @date    2015-10-01T14:52:36-0800
 *
 * @author  Eric Tsang
 */
public class JsonableFile implements Jsonable
{
    // constants: JSON keys

    /**
     * JSON key that is associated with the boolean indicating if this file is
     *   a directory or not.
     */
    private static final String KEY_IS_DIR = "0";

    /**
     * JSON key that is associated with the absolute path to this file.
     */
    private static final String KEY_ABS_PATH = "1";

    /**
     * JSON key that is associated with the name of this file.
     */
    private static final String KEY_NAME = "2";

    // instance data: properties

    /**
     * boolean indicating if this file is a directory or not.
     */
    private final boolean isDirectory;

    /**
     * absolute path to this file.
     */
    private final String absolutePath;

    /**
     * name of this file.
     */
    private final String name;

    // public interface: constructors

    /**
     * instantiates a JsonableFile instance with the passed properties.
     *
     * @method  JsonableFile
     *
     * @date    2015-10-01T14:39:32-0800
     *
     * @author  Eric Tsang
     *
     * @param   isDirectory true if the file is a directory; false otherwise.
     * @param   absolutePath absolute path to the file.
     * @param   name name of the file.
     *
     * @return  a new jsonableFile instance.
     */
    public JsonableFile(boolean isDirectory,String absolutePath,String name)
    {
        this.isDirectory = isDirectory;
        this.absolutePath = absolutePath;
        this.name = name;
    }

    /**
     * instantiates a JsonableFile instance with the same properties as the
     *   passed file.
     *
     * @method  JsonableFile
     *
     * @date    2015-10-01T14:42:28-0800
     *
     * @author  Eric Tsang
     *
     * @param   file a file to inherit the properties of.
     *
     * @return  a new JsonableFile instance.
     */
    public JsonableFile(File file)
    {
        this(file.isDirectory(),file.getAbsolutePath(),file.getName());
    }

    // public interface: server methods

    /**
     * returns true if this file is a directory; false otherwise.
     *
     * @method  isDirectory
     *
     * @date    2015-10-01T14:47:12-0800
     *
     * @author  Eric Tsang
     *
     * @return  true if this file is a directory; false otherwise.
     */
    public boolean isDirectory()
    {
        return isDirectory;
    }

    /**
     * returns the absolute path to this file.
     *
     * @method  getAbsolutePath
     *
     * @date    2015-10-01T14:48:37-0800
     *
     * @author  Eric Tsang
     *
     * @return  the absolute path to this file.
     */
    public String getAbsolutePath()
    {
        return absolutePath;
    }

    /**
     * returns the name of the file.
     *
     * @method  getName
     *
     * @date    2015-10-01T14:50:32-0800
     *
     * @author  Eric Tsang
     *
     * @return  the name of the file.
     */
    public String getName()
    {
        return name;
    }

    // public interface: Jsonable

    /**
     * returns this object, serialized into a JSON object.
     *
     * @method  toJsonObject
     *
     * @date    2015-10-01T14:50:56-0800
     *
     * @author  Eric Tsang
     *
     * @return  this object, serialized into a JSON object.
     */
    @Override
    public JSONObject toJsonObject()
    {
        JSONObject json = new JSONObject();
        json.put(KEY_IS_DIR,isDirectory);
        json.put(KEY_ABS_PATH,getAbsolutePath());
        json.put(KEY_NAME,getName());
        return json;
    }

    /**
     * returns an instance of JsonableFile based off of the passed JSON object.
     *
     * @method  toJsonObject
     *
     * @date    2015-10-01T14:50:56-0800
     *
     * @author  Eric Tsang
     *
     * @return  an instance of JsonableFile based off of the passed JSON object.
     */
    @SuppressWarnings("unused")
    public static JsonableFile fromJsonObject(JSONObject json)
    {
        return new JsonableFile(
                json.getBoolean(KEY_IS_DIR),
                json.getString(KEY_ABS_PATH),
                json.getString(KEY_NAME));
    }
}
