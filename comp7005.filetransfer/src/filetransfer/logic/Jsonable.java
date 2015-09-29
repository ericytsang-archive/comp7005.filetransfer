package filetransfer.logic;

import org.json.JSONObject;

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
}
