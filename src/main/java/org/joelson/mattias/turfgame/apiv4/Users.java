package org.joelson.mattias.turfgame.apiv4;

import org.joelson.mattias.turfgame.util.JSONArray;
import org.joelson.mattias.turfgame.util.JSONObject;
import org.joelson.mattias.turfgame.util.URLReader;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Users {
    
    private static final String USERS_REQUEST = "https://api.turfgame.com/v4/users"; //NON-NLS
    private static final String NAME_PARAMETER = "name"; //NON-NLS
    private static final String ID_PARAMETER = "id"; // NON-NLS
    
    private Users() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }
    
    public static List<User> getUsers(Object... inputObjects) throws IOException, ParseException {
        if (inputObjects.length == 0) {
            return Collections.emptyList();
        }
        List<JSONObject> objects = new ArrayList<>(inputObjects.length);
        for (Object obj : inputObjects) {
            if (obj instanceof String) {
                objects.add(JSONObject.of(Map.of(NAME_PARAMETER, obj)));
            } else if (obj instanceof Integer) {
                objects.add(JSONObject.of(Map.of(ID_PARAMETER, obj)));
            } else {
                throw new IllegalArgumentException("Unknown input object type " + obj.getClass());
            }
        }
        JSONArray array = JSONArray.of(objects.toArray(JSONObject[]::new));
        String json = String.valueOf(array);
        return fromJSON(URLReader.postRequest(USERS_REQUEST, json));
    }

    static List<User> fromJSON(String s) throws ParseException {
        return JSONArray.parseArray(s).stream()
                .map(JSONObject.class::cast)
                .map(User::fromJSON)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException, ParseException {
        System.out.println(getUsers((Object[]) args));
    }
}
