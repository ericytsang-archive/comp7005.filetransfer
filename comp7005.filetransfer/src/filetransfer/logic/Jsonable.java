package filetransfer.logic;

import org.json.JSONObject;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * objects that implement this interface are given the ability to be serialized
 *   to a JSON object, and should provide a user-defined method to parse the
 *   JSON back into the original object.
 *
 * Created by Eric Tsang on 08/06/2015.
 */
public interface Jsonable
{
    /**
     * returns the object serialized into a JSON object that may be able to be
     *   recreated via a user-defined factory method.
     *
     * @return the object serialized into a JSON object that may be able to be
     *   recreated via a user-defined factory method.
     */
    JSONObject toJsonObject();

    /**
     * recreates an object from the passed JSON object. implementations of
     *   this interface need to define their own fromJsonObject static method.
     *   if they don't then when this is used, it will throw an exception.
     *
     * @param json a JSON object to deserialize into the object being returned.
     *
     * @return the object recreated from the passed JSON object.
     */
    @SuppressWarnings("unused")
    static Object fromJsonObject(JSONObject json)
    {
        throw new NotImplementedException();
    }
}
