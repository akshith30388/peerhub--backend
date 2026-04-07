package com.peerhub.controller;

import com.peerhub.service.SettingService;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/settings")
public class SettingController {

    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public Map<String, String> getAll(Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Number idNum = (Number) claims.get("id");
        return settingService.getAllSettings(idNum.longValue());
    }

    @PutMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public Map<String, String> update(@RequestBody Map<String, String> updates, Authentication auth) {
        Claims claims = (Claims) auth.getPrincipal();
        Number idNum = (Number) claims.get("id");
        return settingService.updateSettings(idNum.longValue(), updates);
    }
}
