package com.prt.bookstore.UserAPI.service;


import com.prt.bookstore.UserAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;//для работы с токеном

    public User findUserById(long id){
           User user =  userRepository.findUserById(id);
           if (user!=null){
               return user;
           }
           else
               return null;
    }
    public Long findUserByName(String leadUserName) {
        if (userRepository.findByUsername(leadUserName).isPresent())
            return  userRepository.findByUsername(leadUserName).get().getId();
        return null;
    }
}
