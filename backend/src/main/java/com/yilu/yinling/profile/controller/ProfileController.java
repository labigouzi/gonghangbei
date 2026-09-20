package com.yilu.yinling.profile.controller;

import com.yilu.yinling.common.response.Result;
import com.yilu.yinling.profile.dto.ProfileGenerateRequest;
import com.yilu.yinling.profile.dto.ProfileRequest;
import com.yilu.yinling.profile.service.ProfileService;
import com.yilu.yinling.profile.vo.ProfileView;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@org.springframework.context.annotation.Profile("!demo")
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final ProfileService service;
    public ProfileController(ProfileService service) { this.service = service; }
    private Long userId(Authentication auth) { return (Long) auth.getDetails(); }
    @GetMapping("/me") public Result<ProfileView> get(Authentication auth) { return Result.ok(service.get(userId(auth))); }
    @PutMapping("/me") public Result<ProfileView> update(Authentication auth, @RequestBody ProfileRequest request) { return Result.ok(service.update(userId(auth), request)); }
    @PostMapping("/generate") public Result<ProfileView> generate(Authentication auth, @Valid @RequestBody ProfileGenerateRequest request) { return Result.ok(service.generate(userId(auth), request.description())); }
}
