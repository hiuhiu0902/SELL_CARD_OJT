package com.demo.sell_card_demo1.service;

import com.demo.sell_card_demo1.dto.AccountResponse;
import com.demo.sell_card_demo1.dto.EmailDetail;
import com.demo.sell_card_demo1.dto.LoginRequest;
import com.demo.sell_card_demo1.entity.Member;
import com.demo.sell_card_demo1.entity.User;
import com.demo.sell_card_demo1.enums.Role;
import com.demo.sell_card_demo1.exception.BadRequestException;
import com.demo.sell_card_demo1.repository.AuthenticationRepository;
import com.demo.sell_card_demo1.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {
    @Autowired
    AuthenticationRepository authenticationRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    TokenService tokenService;
    @Autowired
    EmailService emailService;

    public User register(User user){
        user.password = passwordEncoder.encode(user.getPassword());
        user.banned_at = null;
        if(user.role == Role.MEMBER) {
            Member member = new Member();
            member.setUser(user);
            member.setAddress("");
            member.setName(user.getFullname());
            member.setGender("");
            user.setMemberProfile(member);
        }
        authenticationRepository.save(user);
        return user;
    }
    public AccountResponse login(LoginRequest loginRequest){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.username,
                    loginRequest.password
            ));
        }catch(BadRequestException e){
            System.out.println("Sai ttin roi nhoc ac");
            throw new BadRequestException("Invalid username or password");
        }
        User user = authenticationRepository.findUserByUsername(loginRequest.username);
        String token = tokenService.generateToken(user);
        AccountResponse response = new AccountResponse();
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setName(user.getFullname());
        response.setToken(token);
        return response;
    }
    public Member getMemberProfile(String token){
        User user = tokenService.extractAccount(token);
        if(user.getRole() == Role.MEMBER){
            return memberRepository.findMemberByUser(user);
        }else{
            throw new BadRequestException("User is not a member");
        }
    }
    public User resetPassword(String password){
        User user = getCurrentUser();
        user.setPassword(passwordEncoder.encode(password));
        return authenticationRepository.save(user);
    }
    public User getCurrentUser(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticationRepository.findUserByUsername(user.username);
    }

    public void forgotPassword(String email){
        User user = authenticationRepository.findUserByEmail(email);
        if(user != null){
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setSubject("Forget Password");
            emailDetail.setLink("http://localhost:3000/reset-password?token="+tokenService.generateToken(user));
            emailDetail.setRecipient(user.getEmail());
            emailService.sendMail(emailDetail);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authenticationRepository.findUserByUsername(username);
    }

}
