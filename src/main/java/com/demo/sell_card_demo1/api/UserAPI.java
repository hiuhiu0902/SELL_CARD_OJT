package com.demo.sell_card_demo1.api;

import com.demo.sell_card_demo1.dto.AccountResponse;
import com.demo.sell_card_demo1.dto.UpdateAccountRequest;
import com.demo.sell_card_demo1.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/user")
@RestController
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class UserAPI {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<AccountResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<AccountResponse> updateMyProfile(@RequestBody UpdateAccountRequest request) {
        return ResponseEntity.ok(userService.updateMyProfile(request));


    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AccountResponse> uploadUserAvatar(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadUserAvatar(file));
    }
}