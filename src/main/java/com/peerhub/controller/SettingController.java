package com.peerhub.controller;

import com.peerhub.service.SettingService;
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
    public Map<String, String> getAll() {
        return settingService.getAllSettings();
    }

    @PutMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public Map<String, String> update(@RequestBody Map<String, String> updates) {
        return settingService.updateSettings(updates);
    }
}
