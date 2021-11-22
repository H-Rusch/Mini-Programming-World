package view;

import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import model.program.Program;

public class CodeEditor extends TextArea {

    private Program program;

    public CodeEditor() {
        super();
        setFont(Font.font(java.awt.Font.MONOSPACED, 14));
    }

    public void setProgram(Program program) {
        this.program = program;
        setText(program.getCode());
    }
}
