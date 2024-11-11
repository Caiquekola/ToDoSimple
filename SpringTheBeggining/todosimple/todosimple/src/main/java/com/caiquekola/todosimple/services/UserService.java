package com.caiquekola.todosimple.services;

import com.caiquekola.todosimple.models.User;
import com.caiquekola.todosimple.models.enums.ProfileEnum;
import com.caiquekola.todosimple.repositories.TaskRepository;
import com.caiquekola.todosimple.repositories.UserRepository;
import com.caiquekola.todosimple.services.exceptions.DataBindindViolationException;
import com.caiquekola.todosimple.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;


    public User findById(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    //Sempre que fizer um create/update utilize um Transactional - Uma arquitetura para atomicidade
    //Para alterar dado ou uma criação de dado
    //Cria uma conexão por fora com o banco de dados e com uma memória externa
    @Transactional
    public User create(User user) {
        user.setId(null);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user = this.userRepository.save(user);
        user.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
        this.taskRepository.saveAll(user.getTasks());
        return user;
    }

    @Transactional
    public User update(User user) {
        //Reutilizando o código do findById
        User newUser = this.findById(user.getId());
        newUser.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
        return this.userRepository.save(newUser);
    }

    public void delete(Long id) {
        User user = findById(id);
        try{
            this.userRepository.delete(user);
        } catch (Exception e){
            //Possivel erro de um usuário que possua relacionamentos com outras entidades
            throw new DataBindindViolationException(e+ "Error in deleting user");
        }


    }

}
