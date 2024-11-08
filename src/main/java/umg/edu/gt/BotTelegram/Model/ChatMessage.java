package umg.edu.gt.BotTelegram.Model;

import jakarta.persistence.*;
//autor @Kenneth//


import java.io.Serializable;

@Entity
public class ChatMessage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private String message;

    @ManyToOne
    @JoinColumn(name = "bot_command_id")
    private BotCommand botCommand;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BotCommand getBotCommand() {
        return botCommand;
    }

    public void setBotCommand(BotCommand botCommand) {
        this.botCommand = botCommand;
    }
}

