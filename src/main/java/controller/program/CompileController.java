package controller.program;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.program.Program;
import model.territory.Actor;
import model.territory.Territory;
import util.I18nUtil;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;


public class CompileController {

    private CompileController() {
    }

    /**
     * Save and then compile a program. Shows an alert to inform the user whether compiling was successful or not.
     *
     * @param program the program object
     * @param code    the programs code without prefixes or suffix
     */
    public static void compileProgram(Program program, String code, Territory territory) {
        ProgramController.saveProgramToFile(program, code);

        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        boolean success = javac.run(null, null, errStream, program.getFullFileName()) == 0;

        if (!success) {
            new Alert(Alert.AlertType.ERROR, errStream.toString(), ButtonType.OK).showAndWait();
        } else {
            changeActorToCustomActor(program, territory);
            new Alert(Alert.AlertType.INFORMATION, I18nUtil.i18n("alert.compile.success"), ButtonType.OK).showAndWait();
        }
    }

    /**
     * Compile an existing file. The actor in the territory will be changed, if compiling was successful.
     *
     * @param program   the program to be compiled
     * @param territory the territory in which the actor should be replaced
     */
    public static void compileProgramSilently(Program program, Territory territory) {
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

        if (javac.run(null, null, null, program.getFullFileName()) == 0) {
            changeActorToCustomActor(program, territory);
        }
    }

    /**
     * Change the actor in the territory to a custom actor specified by a program describing the actor.
     *
     * @param program   program describing the actor
     * @param territory territory in which the actor should be replaced
     */
    private static void changeActorToCustomActor(Program program, Territory territory) {
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{new File
                (ProgramController.DIRECTORY).toURI().toURL()})) {

            Class<?> aClass = classLoader.loadClass(program.getName());
            Constructor<?> constructor = aClass.getConstructor();

            Object obj = constructor.newInstance();

            if (obj instanceof Actor) {
                territory.changeActor((Actor) obj);
            }

        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
