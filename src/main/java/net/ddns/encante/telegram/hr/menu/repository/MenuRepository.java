package net.ddns.encante.telegram.hr.menu.repository;

import net.ddns.encante.telegram.hr.menu.entity.Menu;
import net.ddns.encante.telegram.hr.menu.entity.MenuPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Menu findFirstByOrderByKeyIdAsc();
    Menu findByChatId(Long chatId);
    @Query("SELECT m FROM Menu m WHERE m.chatId = :chatId AND m.messageId = :messageId")
    Menu findByCredentials (@Param("chatId")Long chatId, @Param("messageId")Long messageId);
    @Query("SELECT p FROM MenuPattern p Where p.name = :name")
    MenuPattern getPatternByName(@Param("name")String name);
}
