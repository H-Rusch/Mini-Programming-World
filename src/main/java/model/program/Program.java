package model.program;

import controller.program.ProgramController;

public class Program {

    private final String name;

    public Program(String name) {
        this.name = name;
        // make sure the file is created
        getCode();
    }

    public String getName() {
        return name;
    }

    public String getFullFileName() {
        return ProgramController.getFullFileName(name);
    }

    /** Load code for this program from file. */
    public String getCode() {
        return ProgramController.getProgramCode(getName());
    }
}
