package com.example.student3.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.student3.dao.AnnouncementDao;
import com.example.student3.dao.CourseDao;
import com.example.student3.dao.DepartmentDao;
import com.example.student3.dao.InstructorDao;
import com.example.student3.dao.RegistrationDao;
import com.example.student3.dao.SemesterDao;
import com.example.student3.dao.StudentDao;
import com.example.student3.dao.UserProfileDao;
import com.example.student3.model.Announcement;
import com.example.student3.model.Course;
import com.example.student3.model.Department;
import com.example.student3.model.Instructor;
import com.example.student3.model.Registration;
import com.example.student3.model.Semester;
import com.example.student3.model.Student;
import com.example.student3.model.UserProfile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        Student.class,
        Course.class,
        Registration.class,
        Department.class,
        Instructor.class,
        Semester.class,
        Announcement.class,
        UserProfile.class
}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract StudentDao studentDao();
    public abstract CourseDao courseDao();
    public abstract RegistrationDao registrationDao();
    public abstract DepartmentDao departmentDao();
    public abstract InstructorDao instructorDao();
    public abstract SemesterDao semesterDao();
    public abstract AnnouncementDao announcementDao();
    public abstract UserProfileDao userProfileDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "student3_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration() // Allow destructive migration for development
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                // Populate the database with sample data
                populateDatabase(INSTANCE);
            });
        }
    };

    private static void populateDatabase(AppDatabase db) {
        // Clear existing data
        db.departmentDao().deleteAll();
        db.instructorDao().deleteAll();
        db.semesterDao().deleteAll();
        db.courseDao().deleteAll();
        db.studentDao().deleteAll();
        db.registrationDao().deleteAll();
        db.announcementDao().deleteAll();
        db.userProfileDao().deleteAll();

        // Add Departments - matching registration form order
        Department[] departments = {
            new Department("Computer Science", "CS", "Department of Computer Science and Information Technology", null),
            new Department("Engineering", "ENG", "Department of Engineering and Technology", null),
            new Department("Business", "BUS", "Department of Business Administration", null),
            new Department("Medicine", "MED", "Department of Medicine and Health Sciences", null),
            new Department("Arts", "ART", "Department of Arts and Humanities", null)
        };

        for (Department dept : departments) {
            db.departmentDao().insert(dept);
        }

        // Add Instructors
        Instructor[] instructors = {
            // Computer Science Instructors (Department ID 1)
            new Instructor("Dr. Sarah", "Johnson", "sarah.johnson@dann4.edu", "+1-555-0101", 1, null),
            new Instructor("Prof. Michael", "Chen", "michael.chen@dann4.edu", "+1-555-0102", 1, null),
            new Instructor("Dr. Emily", "Rodriguez", "emily.rodriguez@dann4.edu", "+1-555-0103", 1, null),

            // Engineering Instructors (Department ID 2)
            new Instructor("Dr. James", "Brown", "james.brown@dann4.edu", "+1-555-0401", 2, null),
            new Instructor("Prof. Jennifer", "Davis", "jennifer.davis@dann4.edu", "+1-555-0402", 2, null),

            // Business Instructors (Department ID 3)
            new Instructor("Dr. Thomas", "Wilson", "thomas.wilson@dann4.edu", "+1-555-0501", 3, null),
            new Instructor("Prof. Robert", "Taylor", "robert.taylor@dann4.edu", "+1-555-0502", 3, null),

            // Medicine Instructors (Department ID 4)
            new Instructor("Dr. Lisa", "Anderson", "lisa.anderson@dann4.edu", "+1-555-0601", 4, null),
            new Instructor("Prof. David", "Williams", "david.williams@dann4.edu", "+1-555-0602", 4, null),

            // Arts Instructors (Department ID 5)
            new Instructor("Prof. Maria", "Garcia", "maria.garcia@dann4.edu", "+1-555-0701", 5, null),
            new Instructor("Prof. Amanda", "Miller", "amanda.miller@dann4.edu", "+1-555-0702", 5, null)
        };

        for (Instructor instructor : instructors) {
            db.instructorDao().insert(instructor);
        }

        // Add Semesters
        Semester[] semesters = {
            new Semester("Fall 2024", "2024-08-26", "2024-12-15", "2024-08-20", true),
            new Semester("Spring 2025", "2025-01-15", "2025-05-10", "2025-01-10", false),
            new Semester("Summer 2025", "2025-06-01", "2025-08-15", "2025-05-25", false)
        };

        for (Semester semester : semesters) {
            db.semesterDao().insert(semester);
        }

        // Add Courses with scheduling information
        Course[] courses = {
            // Computer Science Courses (Department ID 1)
            new Course("CS101", "Introduction to Programming", "Basic programming concepts using Java", 3, 1, 1, 1, 30,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 1, 1, 3),
            new Course("CS201", "Data Structures", "Fundamental data structures and algorithms", 4, 1, 2, 1, 25,
                      "2024-08-26", "2024-12-15", "TUE,THU", 2, 3, 4),
            new Course("CS301", "Database Systems", "Database design and management", 3, 1, 3, 1, 20,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 3, 3, 3),
            new Course("CS401", "Software Engineering", "Software development methodologies", 4, 1, 1, 1, 22,
                      "2024-08-26", "2024-12-15", "TUE,THU", 4, 5, 4),
            new Course("CS202", "Object-Oriented Programming", "Advanced OOP concepts and design patterns", 4, 1, 2, 1, 28,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 5, 5, 4),
            new Course("CS302", "Web Development", "Full-stack web development with modern frameworks", 3, 1, 3, 1, 25,
                      "2024-08-26", "2024-12-15", "TUE,THU", 6, 6, 3),
            new Course("CS402", "Artificial Intelligence", "Introduction to AI and machine learning", 4, 1, 1, 1, 20,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 7, 7, 4),
            new Course("CS403", "Mobile App Development", "iOS and Android application development", 3, 1, 2, 1, 22,
                      "2024-08-26", "2024-12-15", "TUE,THU", 1, 2, 3),
            new Course("CS501", "Cybersecurity", "Network security and ethical hacking", 4, 1, 3, 1, 18,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 2, 3, 4),
            new Course("CS502", "Computer Networks", "Network protocols and distributed systems", 3, 1, 1, 1, 24,
                      "2024-08-26", "2024-12-15", "TUE,THU", 4, 4, 3),

            // Engineering Courses (Department ID 2)
            new Course("ENG101", "Engineering Fundamentals", "Basic engineering principles", 3, 2, 4, 1, 30,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 1, 1, 3),
            new Course("ENG201", "Circuit Analysis", "Electrical circuit analysis", 4, 2, 5, 1, 20,
                      "2024-08-26", "2024-12-15", "TUE,THU", 2, 3, 4),
            new Course("ENG301", "Mechanical Design", "Principles of mechanical engineering design", 4, 2, 4, 1, 25,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 3, 4, 4),
            new Course("ENG202", "Thermodynamics", "Heat transfer and energy systems", 4, 2, 5, 1, 22,
                      "2024-08-26", "2024-12-15", "TUE,THU", 5, 6, 4),
            new Course("ENG302", "Structural Engineering", "Design and analysis of structures", 4, 2, 4, 1, 20,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 6, 7, 4),
            new Course("ENG401", "Control Systems", "Automatic control and system design", 3, 2, 5, 1, 18,
                      "2024-08-26", "2024-12-15", "TUE,THU", 7, 7, 3),
            new Course("ENG402", "Materials Science", "Properties and applications of engineering materials", 3, 2, 4, 1, 24,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 8, 8, 3),
            new Course("ENG501", "Project Management", "Engineering project planning and execution", 3, 2, 5, 1, 26,
                      "2024-08-26", "2024-12-15", "TUE,THU", 1, 2, 3),

            // Business Courses (Department ID 3)
            new Course("BUS101", "Business Administration", "Introduction to business concepts", 3, 3, 6, 1, 40,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 1, 1, 3),
            new Course("BUS201", "Marketing Principles", "Fundamentals of marketing and consumer behavior", 3, 3, 7, 1, 35,
                      "2024-08-26", "2024-12-15", "TUE,THU", 2, 2, 3),
            new Course("BUS301", "Financial Management", "Corporate finance and investment analysis", 4, 3, 6, 1, 30,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 3, 4, 4),
            new Course("BUS202", "Human Resource Management", "Personnel management and organizational behavior", 3, 3, 7, 1, 32,
                      "2024-08-26", "2024-12-15", "TUE,THU", 5, 5, 3),
            new Course("BUS302", "International Business", "Global business strategies and cross-cultural management", 3, 3, 6, 1, 28,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 6, 6, 3),
            new Course("BUS401", "Strategic Management", "Business strategy and competitive analysis", 4, 3, 7, 1, 25,
                      "2024-08-26", "2024-12-15", "TUE,THU", 7, 8, 4),
            new Course("BUS402", "Entrepreneurship", "Starting and managing new ventures", 3, 3, 6, 1, 30,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 8, 8, 3),
            new Course("BUS501", "Business Analytics", "Data-driven decision making in business", 4, 3, 7, 1, 22,
                      "2024-08-26", "2024-12-15", "TUE,THU", 1, 2, 4),

            // Medicine Courses (Department ID 4)
            new Course("MED101", "Human Anatomy", "Structure and function of the human body", 4, 4, 8, 1, 25,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 1, 2, 4),
            new Course("MED201", "Physiology", "Human body systems and their functions", 4, 4, 9, 1, 25,
                      "2024-08-26", "2024-12-15", "TUE,THU", 3, 4, 4),
            new Course("MED301", "Pathology", "Study of disease processes and diagnosis", 4, 4, 8, 1, 20,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 5, 6, 4),
            new Course("MED202", "Biochemistry", "Chemical processes in living organisms", 4, 4, 9, 1, 22,
                      "2024-08-26", "2024-12-15", "TUE,THU", 7, 8, 4),
            new Course("MED302", "Pharmacology", "Drug actions and therapeutic applications", 4, 4, 8, 1, 20,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 1, 2, 4),
            new Course("MED401", "Clinical Medicine", "Patient care and diagnostic procedures", 5, 4, 9, 1, 18,
                      "2024-08-26", "2024-12-15", "TUE,THU", 3, 5, 5),
            new Course("MED402", "Surgery Fundamentals", "Basic surgical principles and techniques", 5, 4, 8, 1, 15,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 6, 8, 5),
            new Course("MED501", "Medical Ethics", "Ethical issues in healthcare practice", 3, 4, 9, 1, 25,
                      "2024-08-26", "2024-12-15", "TUE,THU", 1, 1, 3),

            // Arts Courses (Department ID 5)
            new Course("ART101", "Art History", "Survey of art movements and cultural contexts", 3, 5, 10, 1, 30,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 1, 1, 3),
            new Course("ART201", "Studio Art", "Hands-on creative practice in various media", 3, 5, 11, 1, 20,
                      "2024-08-26", "2024-12-15", "TUE,THU", 2, 2, 3),
            new Course("ART301", "Literature Analysis", "Critical analysis of literary works", 3, 5, 10, 1, 25,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 3, 3, 3),
            new Course("ART202", "Digital Arts", "Computer graphics and digital media creation", 3, 5, 11, 1, 22,
                      "2024-08-26", "2024-12-15", "TUE,THU", 4, 4, 3),
            new Course("ART302", "Creative Writing", "Fiction, poetry, and narrative techniques", 3, 5, 10, 1, 24,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 5, 5, 3),
            new Course("ART401", "Philosophy", "Introduction to philosophical thought and ethics", 3, 5, 11, 1, 28,
                      "2024-08-26", "2024-12-15", "TUE,THU", 6, 6, 3),
            new Course("ART402", "Music Theory", "Fundamentals of music composition and analysis", 3, 5, 10, 1, 20,
                      "2024-08-26", "2024-12-15", "MON,WED,FRI", 7, 7, 3),
            new Course("ART501", "Cultural Studies", "Interdisciplinary study of culture and society", 3, 5, 11, 1, 26,
                      "2024-08-26", "2024-12-15", "TUE,THU", 8, 8, 3)
        };

        for (Course course : courses) {
            db.courseDao().insert(course);
        }

        // Add sample announcements
        Announcement[] announcements = {
            new Announcement("Welcome to Fall 2024",
                           "Welcome to the new academic year! We're excited to have you join us for Fall 2024.",
                           "2024-08-20", "2024-12-31", true),
            new Announcement("Library Hours Extended",
                           "The library will now be open 24/7 during exam periods to support your studies.",
                           "2024-08-25", "2024-12-15", false),
            new Announcement("Important: Registration Deadline",
                           "Reminder: Course registration deadline is September 15th. Don't miss out!",
                           "2024-09-01", "2024-09-15", true),
            new Announcement("Campus WiFi Maintenance",
                           "WiFi will be temporarily unavailable on Saturday from 2-4 AM for maintenance.",
                           "2024-09-10", "2024-09-12", false),
            new Announcement("Student Health Services",
                           "Free flu shots available at the health center. No appointment necessary.",
                           "2024-09-15", "2024-11-30", false)
        };

        for (Announcement announcement : announcements) {
            db.announcementDao().insert(announcement);
        }

        // Skip other sample data to avoid foreign key constraint issues
        // Users will register through the app interface
    }
}