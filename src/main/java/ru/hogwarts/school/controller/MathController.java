package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/math")
public class MathController {

    @GetMapping("/sum")
    public ResponseEntity<Long> getSum() {
        long n = 1_000_000;
        long sum = n * (n + 1) / 2;
        return ResponseEntity.ok(sum);
    }
}
