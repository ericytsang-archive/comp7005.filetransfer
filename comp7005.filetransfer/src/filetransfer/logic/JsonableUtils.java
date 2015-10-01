package filetransfer.logic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import filetransfer.logic.Jsonable;

/**
 * provides various functions that are useful regarding the {@code Jsonable}
 *   interface.
 *
 * @file    ListItem.java
 *
 * @program comp7005.filetransfer.jar
 *
 * @class   JsonableUtils
 *
 * @date    2015-10-01T08:19:58-0800
 *
 * @author  Eric Tsang
 */
public class JsonableUtils
{
    /**
     * converts a JSON object into the an instance of class {@code clazz}.
     *
     * in order to use this function, the class {@code clazz} must have a static
     *   method named fromJsonObject...see {@code Jsonable.fromJsonObject} for
     *   more details.
     *
     * @method  fromJsonObject
     *
     * @date    2015-10-01T08:07:24-0800
     *
     * @author  Eric Tsang
     *
     * @param   clazz the class to convert the json object into an instance of.
     * @param   jsonObject the JSON object to convert into an object.
     *
     * @return  an instance of class {@code clazz}.
     */
    public static <T> T fromJsonObject(Class<T> clazz,JSONObject jsonObject)
    {
        try
        {
            Method m = clazz.getMethod("fromJsonObject",JSONObject.class);
            //noinspection unchecked
            return (T) m.invoke(null,jsonObject);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * converts a JSON array of JSON objects to convert into a list of instances
     *   of class {@code clazz}.
     *
     * @method  fromJsonArray
     *
     * @date    2015-10-01T08:11:11-0800
     *
     * @author  Eric Tsang
     *
     * @param   clazz the class to convert the json objects into an instances
     *   of.
     * @param   json the JSON array of JSON objects to parse.
     *
     * @return  a list of instances of class {@code clazz}.
     */
    public static <T> List<T> fromJsonArray(Class<T> clazz,JSONArray json)
    {
        try
        {
            List<T> items = new ArrayList<>(json.length());
            for(int i = 0; i < json.length(); ++i)
            {
                JSONObject jsonObject = new JSONObject(json.getString(i));
                T t = fromJsonObject(clazz,jsonObject);
                items.add(t);
            }
            return items;
        }
        catch(JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * converts a JSON array of String objects into a list of Strings.
     *
     * @method  fromJsonArray
     *
     * @date    2015-10-01T08:14:14-0800
     *
     * @author  Eric Tsang
     *
     * @param   json the JSON array to convert into a list of String instances
     *
     * @return  a List of String instances parsed from the JSON array.
     */
    public static List<String> fromJsonArray(JSONArray json)
    {
        try
        {
            List<String> items = new ArrayList<>(json.length());
            for(int i = 0; i < json.length(); ++i)
            {
                items.add(json.getString(i));
            }
            return items;
        }
        catch(JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * converts a collection of objects into a JSON array of JSON objects. if
     *   the objects implement {@code Jsonable}, then their {@code toJsonObject}
     *   method will be used to serialize them; the {@code toString} method will
     *   be used otherwise.
     *
     * @method  toJsonArray
     *
     * @date    2015-10-01T08:16:29-0800
     *
     * @author  Eric Tsang
     *
     * @param   objects the collection of objects to convert into a JSON array
     *   of strings, or JSON objects.
     *
     * @return  the JSON array created from the collection of objects.
     */
    public static JSONArray toJsonArray(Collection<?> objects)
    {
        JSONArray json = new JSONArray();
        for(Object object : objects)
        {
            String elementValue = object instanceof Jsonable
                    ? ((Jsonable) object).toJsonObject().toString()
                    : object.toString();
            json.put(elementValue);
        }
        return json;
    }
}
