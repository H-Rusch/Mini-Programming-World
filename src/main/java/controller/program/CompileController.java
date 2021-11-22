package controller.program;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.program.Program;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;

public class CompileController {

    /**
     * Save and then compile a program.
     *
     * @param program the program object
     * @param code    the programs code without prefixes or suffix
     */
    public static void compileProgram(Program program, String code) {
        ProgramController.saveProgram(program, code);

        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        boolean success = javac.run(null, null, errStream, program.getFullFileName()) == 0;

        String str;
        Alert.AlertType alertType;
        if (!success) {
            str = errStream.toString();
            alertType = Alert.AlertType.ERROR;
        } else {
            str = "Kompilieren erfolgreich";
            alertType = Alert.AlertType.INFORMATION;
        }
        Alert alert = new Alert(alertType, str, ButtonType.OK);
        alert.showAndWait();
    }
}
