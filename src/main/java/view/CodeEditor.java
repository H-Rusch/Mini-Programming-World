package view;

import javafx.scene.control.TextArea;
import model.program.Program;

public class CodeEditor extends TextArea {

    private Program program;

    public CodeEditor() {
        super();
    }

    public void setProgram(Program program) {
        this.program = program;
        setText(program.getCode());
    }
}
