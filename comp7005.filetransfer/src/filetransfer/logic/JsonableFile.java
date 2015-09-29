package filetransfer.logic;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by Eric on 9/28/2015.
 */
public class JsonableFile implements Jsonable
{
    // constants: json keys
    private static final String KEY_IS_DIR = "0";
    private static final String KEY_ABS_PATH = "1";
    private static final String KEY_NAME = "2";

    // instance data: properties
    private final boolean isDirectory;
    private final String absolutePath;
    private final String name;

    // public interface: constructors

    public JsonableFile(boolean isDirectory,String absolutePath,String name)
    {
        this.isDirectory = isDirectory;
        this.absolutePath = absolutePath;
        this.name = name;
    }

    public JsonableFile(File file)
    {
        this(file.isDirectory(),file.getAbsolutePath(),file.getName());
    }

    // public interface: server methods

    public boolean isDirectory()
    {
        return isDirectory;
    }

    public String getAbsolutePath()
    {
        return absolutePath;
    }

    public String getName()
    {
        return name;
    }

    // public interface: Jsonable

    @Override
    public JSONObject toJsonObject()
    {
        JSONObject json = new JSONObject();
        json.put(KEY_IS_DIR,isDirectory);
        json.put(KEY_ABS_PATH,getAbsolutePath());
        json.put(KEY_NAME,getName());
        return json;
    }

    @SuppressWarnings("unused")
    public static JsonableFile fromJsonObject(JSONObject json)
    {
        return new JsonableFile(
                json.getBoolean(KEY_IS_DIR),
                json.getString(KEY_ABS_PATH),
                json.getString(KEY_NAME));
    }
}
