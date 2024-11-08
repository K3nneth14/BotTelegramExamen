package umg.edu.gt.BotTelegram.Repository;

//autor @Kenneth//

import org.springframework.data.jpa.repository.JpaRepository;
import umg.edu.gt.BotTelegram.Model.BotCommand;
;

public interface BotCommandRepository extends JpaRepository<BotCommand, Long> {
    BotCommand findByCommand(String command);
}
