package filetransfer.logic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import filetransfer.logic.Jsonable;

public class JsonableUtils
{
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
