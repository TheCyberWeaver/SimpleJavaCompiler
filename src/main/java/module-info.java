module org.example.simplejava {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jfxtras.styles.jmetro;
    requires java.desktop;

    opens org.example.simplejava to javafx.fxml;
    exports org.example.simplejava;
    exports org.example.simplejava.helperObjects;
    exports org.example.simplejava.tools;
    opens org.example.simplejava.helperObjects to javafx.fxml;
    opens org.example.simplejava.tools to javafx.fxml;
    exports org.example.simplejava.converters;
    opens org.example.simplejava.converters to javafx.fxml;
    exports org.example.simplejava.ASTTree;
    opens org.example.simplejava.ASTTree to javafx.fxml;
    exports org.example.simplejava.ASTTree.expressions;
    opens org.example.simplejava.ASTTree.expressions to javafx.fxml;
    exports org.example.simplejava.ASTTree.statements;
    opens org.example.simplejava.ASTTree.statements to javafx.fxml;
}