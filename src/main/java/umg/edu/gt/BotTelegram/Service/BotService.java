package umg.edu.gt.BotTelegram.Service;
//autor @Kenneth//

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import umg.edu.gt.BotTelegram.Model.BotCommand;
import umg.edu.gt.BotTelegram.Model.ChatMessage;
import umg.edu.gt.BotTelegram.Model.Client;
import umg.edu.gt.BotTelegram.Repository.BotCommandRepository;
import umg.edu.gt.BotTelegram.Repository.ChatMessageRepository;
import umg.edu.gt.BotTelegram.Repository.Repository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class BotService {

    private final String BOT_TOKEN = "8160661422:AAHNVDbLPSfMqsj2pFUVl3-Enk_-f3WPXfo"; // Reemplaza con tu token
    private final String TELEGRAM_API_URL = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
    private final String OPENAI_API_URL = "";
    private final String OPENAI_API_KEY = "";

    @Autowired
    private BotCommandRepository botCommandRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public void registerCommand(Long chatId, String command, String message) {
        BotCommand botCommand = botCommandRepository.findByCommand(command);
        if (botCommand == null) {
            botCommand = new BotCommand();
            botCommand.setCommand(command);
            botCommandRepository.save(botCommand);
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatId(chatId);
        chatMessage.setMessage(message);
        chatMessage.setBotCommand(botCommand);
        chatMessageRepository.save(chatMessage);
    }

    private String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    private Map<Long, Boolean> askingName = new HashMap<>();
    private Map<Long, String> userNames = new HashMap<>();

    public void sendTelegramMessage(Long chatId, String message) {
        RestTemplate restTemplate = new RestTemplate();
        String url = TELEGRAM_API_URL + "?chat_id=" + chatId + "&text=" + message;
        restTemplate.getForObject(url, String.class);
    }

    public void setUserName(Long chatId, String name) {
        userNames.put(chatId, name);
    }

    public String getUserName(Long chatId) {
        return userNames.getOrDefault(chatId, "Aún no me has dicho tu nombre.");
    }

    public void setAskingName(Long chatId, boolean asking) {
        askingName.put(chatId, asking);
    }

    public boolean isAskingName(Long chatId) {
        return askingName.getOrDefault(chatId, false);
    }

    public Client getClientById(Long chatId) throws SQLException {
        System.out.println("Consultando a la base de datos para chatId: " + chatId);
        return Repository.getById(chatId);
    }

    public String getChatGptResponse(String message) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", message)
        ));
        requestBody.put("max_tokens", 150);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> messageContent = (Map<String, Object>) choices.get(0).get("message");
                return (String) messageContent.get("content");
            }
        }
        return "Lo siento, no pude obtener una respuesta en este momento.";
    }

    public void handleUpdate(Map<String, Object> update) throws SQLException {

        try {
            if (update.containsKey("message")) {
                Map<String, Object> message = (Map<String, Object>) update.get("message");
                Map<String, Object> chat = (Map<String, Object>) message.get("chat");
                long chatId = ((Number) chat.get("id")).longValue();
                String text = (String) message.get("text");


                Client client = getClientById(chatId);

                if (client != null) {
                    sendTelegramMessage(chatId, "¡Hola " + client.getName() + ", en qué te puedo ayudar hoy?");
                } else {
                    if (text.equalsIgnoreCase("/start")) {
                        sendTelegramMessage(chatId, "¡Bienvenido! ¿Cómo te llamas?");
                        setAskingName(chatId, true);
                    } else if (isAskingName(chatId)) {
                        setUserName(chatId, text);
                        Repository.add(text, chatId);

                        Client newClient = Repository.getById(chatId);
                        sendTelegramMessage(chatId, "¡Hola " + newClient.getName() + ", en qué te puedo ayudar hoy?");
                        setAskingName(chatId, false);
                        System.out.println("nombre guardado: " + newClient.getName());
                    } else {
                        String response = getChatGptResponse(text);
                        sendTelegramMessage(chatId, response);
                        System.out.println("Respuesta enviada: " + response);
                    }
                }
            } else {
                System.out.println("Error en la actualizacion.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
