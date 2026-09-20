package com.yilu.yinling.profile.service;
import com.yilu.yinling.profile.dto.ProfileRequest;
import com.yilu.yinling.profile.vo.ProfileView;
public interface ProfileService { ProfileView get(Long userId); ProfileView update(Long userId, ProfileRequest request); ProfileView generate(Long userId, String description); }
