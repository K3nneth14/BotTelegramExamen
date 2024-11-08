package umg.edu.gt.BotTelegram.Repository;
//autor @Kenneth//

import org.springframework.data.jpa.repository.JpaRepository;
import umg.edu.gt.BotTelegram.Model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
