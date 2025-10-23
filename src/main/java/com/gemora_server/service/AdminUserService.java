package com.gemora_server.service;

import com.gemora_server.dto.UserAdminViewDto;
import java.util.List;

public interface AdminUserService {
    List<UserAdminViewDto> getAllUsers();
    UserAdminViewDto getUserById(Long userId);

}
