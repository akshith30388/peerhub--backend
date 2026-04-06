package com.peerhub.service;

import com.peerhub.model.Setting;
import com.peerhub.repository.SettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SettingService {

    private final SettingRepository settingRepository;

    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public Map<String, String> getAllSettings() {
        return settingRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Setting::getSettingKey,
                        Setting::getSettingValue,
                        (a, b) -> b,
                        LinkedHashMap::new
                ));
    }

    @Transactional
    public Map<String, String> updateSettings(Map<String, String> updates) {
        updates.forEach((key, value) -> {
            Setting setting = settingRepository.findBySettingKey(key)
                    .orElse(new Setting(key, value));
            setting.setSettingValue(value);
            settingRepository.save(setting);
        });
        return getAllSettings();
    }
}
