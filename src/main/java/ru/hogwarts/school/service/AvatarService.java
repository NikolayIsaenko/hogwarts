package ru.hogwarts.school.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;

import java.io.IOException;
import java.util.Optional;

public interface AvatarService {
    Avatar uploadAvatar(Long studentId, MultipartFile avatar) throws IOException;

    Avatar findAvatar(Long avatarId);
    Page<Avatar> getAllAvatars(Pageable pageable);

    Optional<Avatar> getAvatarByStudent(Student student);

    Optional<Avatar> getAvatarById(Long id);

    void deleteAvatar(Avatar avatar);
}
