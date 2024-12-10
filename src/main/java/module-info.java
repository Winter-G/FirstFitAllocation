module com.winter.firstfitallocation {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.winter.firstfitallocation to javafx.fxml;
    exports com.winter.firstfitallocation;
}