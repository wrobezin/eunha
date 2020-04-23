package io.github.wrobezin.eunha.app.controller;

import io.github.wrobezin.eunha.app.service.MessageService;
import io.github.wrobezin.eunha.data.entity.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/23 18:40
 */
@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/read-count")
    public Integer countRead() {
        return messageService.countRead();
    }

    @GetMapping("/unread-count")
    public Integer countUnread() {
        return messageService.countUnread();
    }

    @GetMapping("/read")
    public List<Message> getRead() {
        return messageService.getRead();
    }

    @GetMapping("/unread")
    public List<Message> getUnread() {
        return messageService.getUnread();
    }

    @GetMapping
    public List<Message> getAll() {
        return messageService.getAll();
    }

    @PostMapping("/read/{id}")
    public boolean setRead(@PathVariable String id) {
        try {
            messageService.setRead(id);
        } catch (Exception e) {
            log.error("设置已读出错", e);
            return false;
        }
        return true;
    }

    @PostMapping("/unread/{id}")
    public boolean setUnread(@PathVariable String id) {
        try {
            messageService.setUnread(id);
        } catch (Exception e) {
            log.error("设置未读出错", e);
            return false;
        }
        return true;
    }

    @DeleteMapping("/read/{id}")
    public boolean deleteMessage(@PathVariable String id) {
        try {
            messageService.remove(id);
        } catch (Exception e) {
            log.error("设置已读出错", e);
            return false;
        }
        return true;
    }
}
