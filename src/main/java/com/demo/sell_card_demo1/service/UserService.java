package com.demo.sell_card_demo1.service;

import com.demo.sell_card_demo1.dto.AccountResponse;
import com.demo.sell_card_demo1.dto.UpdateAccountRequest;
import com.demo.sell_card_demo1.entity.Member;
import com.demo.sell_card_demo1.entity.User;
import com.demo.sell_card_demo1.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    MemberRepository memberRepository;

    public AccountResponse getMyProfile() {
        User user = authenticationService.getCurrentUser();
        Member member = memberRepository.findMemberByUser(user);
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setUsername(user.getUsername());
        accountResponse.setEmail(user.getEmail());
        accountResponse.setPhone(user.getMemberProfile().getPhone());
        accountResponse.setRole(user.getRole());
        accountResponse.setName(member.getName());
        accountResponse.setAddress(member.getAddress());
        return accountResponse;
    }

    public AccountResponse updateMyProfile(UpdateAccountRequest request) {
        User user = authenticationService.getCurrentUser();
        Member member = memberRepository.findMemberByUser(user);
        member.setPhone(request.getPhone());
        member.setName(request.getFullName());
        member.setAddress(request.getAddress());
        memberRepository.save(member);
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setUsername(user.getUsername());
        accountResponse.setEmail(user.getEmail());
        accountResponse.setPhone(member
                .getPhone());
        accountResponse.setRole(user.getRole());
        accountResponse.setName(member.getName());
        accountResponse.setAddress(member.getAddress());
        return accountResponse;
    }

}
