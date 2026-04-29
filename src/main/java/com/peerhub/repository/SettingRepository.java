package com.peerhub.repository;

import com.peerhub.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SettingRepository extends JpaRepository<Setting, Long> {
    Optional<Setting> findByInstructorIdAndSettingKey(Long instructorId, String settingKey);
    Optional<Setting> findByInstructorIdIsNullAndSettingKey(String settingKey);
    List<Setting> findByInstructorIdOrderByIdAsc(Long instructorId);
}
