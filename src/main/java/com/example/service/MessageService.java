package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

@RestController
public class MessageService {
    @Autowired
    MessageRepository dao;

    public Optional<Message> getById(int id) {
        return dao.findById(id);
    }

    public Optional<List<Message>> getAll() {
        return Optional.ofNullable(dao.findAll());
    }

    public Optional<Message> createMessage(Message newMessage) {
        if (newMessage.getMessageText().length() <= 255)
            return Optional.of(dao.save(newMessage));
        return Optional.empty();
    }

    public int deleteById(int messageId) {
        boolean exists = dao.existsById(messageId);
        if (exists) dao.deleteById(messageId);
        return exists ? 1 : 0;
    }

    public int updateMessageById(int messageId, String messageText) {
        Optional<Message> opt = getById(messageId);
        if (opt.isPresent() && messageText.length() <= 255) {
            Message mod = opt.get();
            mod.setMessageText(messageText);
            dao.save(mod);
            return 1;
        }
        return 0;
    }

    public Optional<List<Message>> getMessagesPostedBy(int accountId) {
        return Optional.ofNullable(dao.findMessagesByPostedBy(accountId));
    }

}
