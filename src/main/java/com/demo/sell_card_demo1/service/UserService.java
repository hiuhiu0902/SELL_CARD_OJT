package com.demo.sell_card_demo1.service;

import com.demo.sell_card_demo1.dto.AccountResponse;
import com.demo.sell_card_demo1.dto.ProductResponse;
import com.demo.sell_card_demo1.dto.UpdateAccountRequest;
import com.demo.sell_card_demo1.entity.Member;
import com.demo.sell_card_demo1.entity.Product;
import com.demo.sell_card_demo1.entity.User;
import com.demo.sell_card_demo1.exception.BadRequestException;
import com.demo.sell_card_demo1.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserService {
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    S3Service s3Service;

    private AccountResponse buildAccountResponse(User user, Member member) {
        AccountResponse response = new AccountResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        if (user.getMemberProfile() != null) {
            response.setPhone(user.getMemberProfile().getPhone());
        }
        response.setRole(user.getRole());
        response.setName(member.getName());
        response.setAddress(member.getAddress());
        response.setAvatarUrl(s3Service.getUrl(member.getAvatarUrl()));
        return response;
    }

    public AccountResponse getMyProfile() {
        User user = authenticationService.getCurrentUser();
        Member member = memberRepository.findMemberByUser(user);
        AccountResponse accountResponse = buildAccountResponse(user, member);
        return accountResponse;
    }

    public AccountResponse updateMyProfile(UpdateAccountRequest request) {
        User user = authenticationService.getCurrentUser();
        Member member = memberRepository.findMemberByUser(user);
        member.setPhone(request.getPhone());
        member.setName(request.getFullName());
        member.setAddress(request.getAddress());
        memberRepository.save(member);
        AccountResponse accountResponse = buildAccountResponse(user, member);
        return accountResponse;
    }

    public AccountResponse uploadUserAvatar(MultipartFile file) {
        User user = authenticationService.getCurrentUser();
        if (file.isEmpty()) throw new BadRequestException("File is empty");

        try {
            String extension = "jpg";
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            }

            String s3Key = s3Service.uploadFile(
                    "avatars",
                    String.valueOf(user.getUserId()),
                    file.getInputStream(),
                    file.getSize(),
                    extension
            );

            Member member = memberRepository.findMemberByUser(user);
            member.setAvatarUrl(s3Key);
            memberRepository.save(member);

            return buildAccountResponse(user, member);

        } catch (IOException e) {
            throw new BadRequestException("Lỗi upload avatar: " + e.getMessage());
        }
    }
}
