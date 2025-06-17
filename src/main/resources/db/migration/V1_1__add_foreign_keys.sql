ALTER TABLE students
    ADD CONSTRAINT UQ_students_profile_id UNIQUE (profile_id),
   ADD CONSTRAINT UQ_students_student_record_id UNIQUE (student_record_id);

ALTER TABLE students
    ADD CONSTRAINT FK_students_profile_id
        FOREIGN KEY (profile_id)
            REFERENCES profiles (id);

ALTER TABLE enrollments
    ADD CONSTRAINT FK_enrollments_students_record_id
        FOREIGN KEY (student_record_id)
            REFERENCES students (student_record_id);