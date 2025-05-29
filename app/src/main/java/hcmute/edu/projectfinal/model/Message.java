package hcmute.edu.projectfinal.model;

public class Message {
    private String role;     // "user" hoặc "assistant"
    private String content;

    public Message() {
    }

    public Message(String content, String role) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}