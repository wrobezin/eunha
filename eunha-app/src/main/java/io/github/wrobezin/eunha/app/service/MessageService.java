package io.github.wrobezin.eunha.app.service;

import io.github.wrobezin.eunha.data.entity.message.Message;
import io.github.wrobezin.eunha.data.repository.mongo.MessageMongoRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/23 17:38
 */
@Service
public class MessageService {
    private final MessageMongoRepository messageRepository;

    public MessageService(MessageMongoRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Integer countUnread() {
        return messageRepository.countAllByRead(false);
    }

    public Integer countRead() {
        return messageRepository.countAllByRead(true);
    }

    public List<Message> getAll() {
        return messageRepository.findAll();
    }

    public List<Message> getUnread() {
        return messageRepository.findAllByRead(false);
    }

    public List<Message> getRead() {
        return messageRepository.findAllByRead(true);
    }

    public Message addNewMessage(Message message) {
        message.setRead(false);
        if (null == message.getParams()) {
            message.setParams(Collections.emptyMap());
        }
        return messageRepository.save(message);
    }

    public void setRead(String id) {
        messageRepository.findById(id).ifPresent(this::setRead);
    }

    public void setRead(Message message) {
        message.setRead(true);
        messageRepository.save(message);
    }

    public void setUnread(String id) {
        messageRepository.findById(id).ifPresent(this::setUnread);
    }

    public void setUnread(Message message) {
        message.setRead(false);
        messageRepository.save(message);
    }

    public void remove(String id) {
        messageRepository.findById(id).ifPresent(this::remove);
    }

    public void remove(Message message) {
        messageRepository.delete(message);
    }
}
