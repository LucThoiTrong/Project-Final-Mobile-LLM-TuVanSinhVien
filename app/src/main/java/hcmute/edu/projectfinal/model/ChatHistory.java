package hcmute.edu.projectfinal.model;

public class ChatHistory {
    private String sessionId;
    private String content;

    public ChatHistory() {}

    public ChatHistory(String sessionId, String content) {
        this.sessionId = sessionId;
        this.content = content;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
