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

    public Map<String, String> getAllSettings(Long instructorId) {
        return settingRepository.findByInstructorIdOrderByIdAsc(instructorId).stream()
                .collect(Collectors.toMap(
                        Setting::getSettingKey,
                        Setting::getSettingValue,
                        (a, b) -> b,
                        LinkedHashMap::new
                ));
    }

    @Transactional
    public Map<String, String> updateSettings(Long instructorId, Map<String, String> updates) {
        updates.forEach((key, value) -> {
            String safeKey = key == null ? "" : key.trim();
            if (safeKey.isBlank()) {
                return;
            }

            Setting setting = settingRepository.findByInstructorIdAndSettingKey(instructorId, safeKey)
                    .orElseGet(() -> settingRepository.findBySettingKey(safeKey).orElse(new Setting(safeKey, value)));

            // Claim legacy/global row for this instructor to avoid duplicate-key inserts.
            setting.setSettingKey(safeKey);
            setting.setSettingValue(value);
            setting.setInstructorId(instructorId);
            settingRepository.save(setting);
        });
        return getAllSettings(instructorId);
    }
}
