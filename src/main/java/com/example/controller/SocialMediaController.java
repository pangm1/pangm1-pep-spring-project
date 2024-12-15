package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {
    @Autowired
    AccountService accounts;
    @Autowired
    MessageService messages;
    
    @PostMapping("register")
    public ResponseEntity<Account> register(@RequestBody Account newUser) {
        // username not blank or not exists, > 4 characters
        // success -> persist, return json of Account with accountId, 200 OK default
        // duplicate username -> 409 Conflict, return null?
        // unsuccessful, 400 client error
        if (!newUser.getUsername().isBlank() && newUser.getUsername().length() >= 4) {
            if (accounts.usernameExists(newUser.getUsername()))
                return new ResponseEntity<Account>(HttpStatus.CONFLICT);

            Optional<Account> res = accounts.create(newUser);
            return new ResponseEntity<Account>(res.orElse(null), HttpStatus.OK);
        }
        return new ResponseEntity<Account>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("login")
    public ResponseEntity<Account> login(@RequestBody Account user) {
        // username and password match
        // success -> return json of Account with accountId, 200 OK default
        // unsuccessful, 401 unauthorized
        Optional<Account> res = accounts.getValid(user);
        if (res.isPresent())
            return new ResponseEntity<Account>(res.orElse(null), HttpStatus.OK);
        return new ResponseEntity<Account>(HttpStatus.UNAUTHORIZED);
    }
    
    @PostMapping("messages")
    public ResponseEntity<Message> newPost(@RequestBody Message newMessage) {
        // message not blank, postedBy is a user
        // success -> persist, return json of Message with messageId, 200 OK default
        // unsuccessful, 400 client error
        if (accounts.getById(newMessage.getPostedBy()).isPresent() && !newMessage.getMessageText().isBlank()) {
            Optional<Message> res = messages.createMessage(newMessage);
            if (res.isPresent())
                return new ResponseEntity<Message>(res.get(), HttpStatus.OK);
        }
        return new ResponseEntity<Message>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("messages")
    public ResponseEntity<List<Message>> getAll() {
        Optional<List<Message>> res = messages.getAll();
        return new ResponseEntity<List<Message>>(res.orElse(null), HttpStatus.OK);
    }

    @GetMapping("messages/{messageId}")
    public ResponseEntity<Message> getById(@PathVariable int messageId) {
        Optional<Message> res = messages.getById(messageId);
        return new ResponseEntity<Message>(res.orElse(null), HttpStatus.OK);
    }

    @DeleteMapping("messages/{messageId}")
    public ResponseEntity<Integer> deleteById(@PathVariable int messageId) {
        // exists -> 1
        // not exists -> empty
        int res = messages.deleteById(messageId);
        return new ResponseEntity<Integer>(res == 1 ? 1 : null, HttpStatus.OK);
    }

    @PatchMapping("messages/{messageId}")
    public ResponseEntity<Integer> updateById(@PathVariable int messageId, @RequestBody JsonNode body) {
        // get messageText from body
        // id exists, message is not blank
        // successful -> update database, 1
        // not exists -> 400 client error
        String messageText = body.path("messageText").asText();
        if (messages.getById(messageId).isPresent() && !messageText.isBlank()) {
            int res = messages.updateMessageById(messageId, messageText);
            if (res == 1)
                return new ResponseEntity<Integer>(res == 1 ? 1 : null, HttpStatus.OK);
        }
        return new ResponseEntity<Integer>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getFromAccount(@PathVariable int accountId) {
        Optional<List<Message>> res = messages.getMessagesPostedBy(accountId);
        return new ResponseEntity<List<Message>>(res.orElse(null), HttpStatus.OK);
    }
}
