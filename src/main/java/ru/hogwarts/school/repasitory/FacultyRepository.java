package ru.hogwarts.school.repasitory;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Faculty;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    List<Faculty> findAlByColor(String color);

    List<Faculty> findByColorOrNameIgnoreCase(String name, String color);

}
