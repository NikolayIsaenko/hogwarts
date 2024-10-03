ALTER TABLE Student
ADD CONSTRAINT chk_student_age CHECK (age >= 16);

ALTER TABLE Student
ADD CONSTRAINT uq_student_name UNIQUE (name),
ALTER COLUMN name SET NOT NULL;

ALTER TABLE Faculty
ADD CONSTRAINT uq_faculty_name_color UNIQUE (name, color);

ALTER TABLE Student
ALTER COLUMN age SET DEFAULT 20;