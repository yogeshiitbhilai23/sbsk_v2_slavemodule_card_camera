package org.example.sbsk_v2_slavemodule_card_camera.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JsonUtils {
    private static final String VALID_USERS_FILE = "src/main/java/org/example/sbsk_v2_slavemodule_card_camera/utils/valid_users.json";

    public static Map<String, String> loadValidUsers() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(VALID_USERS_FILE), new TypeReference<Map<String, String>>() {});
    }

    public static boolean isValidUser(String idNumber, String name) {
        try {
            Map<String, String> validUsers = loadValidUsers();
            String storedName = validUsers.get(idNumber);
            return storedName != null && storedName.equalsIgnoreCase(name.trim());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}