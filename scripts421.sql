ALTER TABLE Student
ADD CONSTRAINT chk_student_age CHECK (age >= 11),
ADD CONSTRAINT uq_student_name UNIQUE (name);

ALTER TABLE Faculty
ADD CONSTRAINT uq_faculty_name_color UNIQUE (name, color);

CREATE OR REPLACE FUNCTION set_default_age() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.age IS NULL THEN
        NEW.age := 20;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_set_default_age
BEFORE INSERT ON Student
FOR EACH ROW
EXECUTE FUNCTION set_default_age();