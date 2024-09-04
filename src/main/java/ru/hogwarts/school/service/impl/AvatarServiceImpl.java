package ru.hogwarts.school.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repasitory.AvatarRepository;
import ru.hogwarts.school.repasitory.StudentRepository;
import ru.hogwarts.school.service.AvatarService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.swagger.v3.core.util.AnnotationsUtils.getExtensions;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarServiceImpl implements AvatarService {
    @Value("${path.to.avatars.folder}")
    private String avatarsDir;
    private final int BUFFER_SIZE = 1024;

    private final StudentRepository studentRepository;
    private AvatarRepository avatarRepository;

    public AvatarServiceImpl(StudentRepository studentRepository, AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
    }

    @Override
    public Avatar uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        Student student = studentRepository.findById(studentId).orElseThrow(() ->
                new IllegalArgumentException(studentId + " - is not found")
        );
        Path avatarPath = saveToLocal(student, avatarFile);
        Avatar avatar = saveToDb(student, avatarPath, avatarFile);
        return avatar;
    }

    @Override
    public Avatar findAvatar(Long avatarId) {
        return avatarRepository.findById(avatarId).orElseThrow(() ->
                new IllegalArgumentException(avatarId + " - is not found")
        );
    }

    private String getExtensions(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private Path saveToLocal(Student student, MultipartFile avatarFile) throws IOException {

        Path avatarPath = Path.of(avatarsDir, student.getId() + "." + getExtensions(avatarFile.getOriginalFilename()));
        Files.createDirectories(avatarPath.getParent());
        Files.deleteIfExists(avatarPath);
        try (
                InputStream is = avatarFile.getInputStream();
                OutputStream os = Files.newOutputStream(avatarPath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
                BufferedOutputStream bos = new BufferedOutputStream(os, BUFFER_SIZE);
        ) {
            bis.transferTo(bos);
        }
        return avatarPath;
    }

    private Avatar saveToDb(Student student, Path avatarPath, MultipartFile avatarFile) throws IOException {
        Avatar avatar = getAvatarByStudent(student);
        avatar.setFilePath(avatarPath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(avatarFile.getBytes());
        return avatarRepository.save(avatar);
    }

    private Avatar getAvatarByStudent(Student student) {
        return avatarRepository.findByStudent(student).orElseGet(() -> {
            Avatar avatar = new Avatar();
            avatar.setStudent(student);
            return avatar;
        });
    }
}