package ru.hogwarts.school.repasitory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;

import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    Optional<Avatar> findByStudent(Student student);

    Page<Avatar> findAll(Pageable pageable);
}
