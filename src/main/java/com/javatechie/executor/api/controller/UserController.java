package com.javatechie.executor.api.controller;

import com.javatechie.executor.api.entity.User;
import com.javatechie.executor.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@RestController
public class UserController {
    @Autowired
    private UserService service;

    @PostMapping(value = "/users", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
    public ResponseEntity saveUsers(@RequestParam(value = "files") MultipartFile[] files) throws Exception {
        for (MultipartFile file : files) {
            service.saveUsers(file);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/users", produces = "application/json")
    public CompletableFuture<ResponseEntity<List<User>>> findAllUsers() {
       return  service.findAllUsers().thenApply(ResponseEntity::ok)
               .exceptionally(handleGetAllUserFailure);
    }
    private static Function<Throwable, ? extends ResponseEntity<List<User>>> handleGetAllUserFailure = throwable -> {
        System.out.println("error is:"+throwable);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    };

    @GetMapping("/asyncexception")
    public CompletableFuture<ResponseEntity<Optional<User>>> findUserById(){
        return service.findUserById(10).thenApply(ResponseEntity::ok).
                exceptionally( handleGetUserFailure);
    }

    private static Function<Throwable, ? extends ResponseEntity<Optional<User>>> handleGetUserFailure = throwable -> {
        System.out.println("error is:"+throwable);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    };

    @GetMapping(value = "/getUsersByThread", produces = "application/json")
    public  ResponseEntity getUsers(){
        CompletableFuture<List<User>> users1=service.findAllUsers();
        CompletableFuture<List<User>> users2=service.findAllUsers();
        CompletableFuture<List<User>> users3=service.findAllUsers();
        CompletableFuture.allOf(users1,users2,users3).join();
        return  ResponseEntity.status(HttpStatus.OK).build();
    }
}
