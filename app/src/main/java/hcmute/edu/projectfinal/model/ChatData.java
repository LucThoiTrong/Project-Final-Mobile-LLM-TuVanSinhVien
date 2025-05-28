package hcmute.edu.projectfinal.model;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ChatData {
    public static final List<Message> messages = new ArrayList<>();
    public static final JSONArray messagesJSONToSend = new JSONArray();
}
