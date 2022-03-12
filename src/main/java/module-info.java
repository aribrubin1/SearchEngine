module com.example.final_course_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com to javafx.fxml;
    exports com;
}