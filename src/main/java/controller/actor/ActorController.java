package controller.actor;

import controller.territory.PresentsDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import model.program.Invisible;
import model.territory.Actor;
import model.territory.Territory;
import model.territory.exceptions.ActorException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ActorController {

    private final Territory territory;
    private Actor actor;

    private AudioClip errorSound;

    public ActorController(Territory territory, Actor actor) {
        this.territory = territory;
        this.actor = actor;

        this.errorSound = new AudioClip(String.valueOf(getClass().getResource("/sound/error_sound.mp3")));
    }

    public void forward() {
        actor.forward();
    }

    public void turnLeft() {
        actor.turnLeft();
    }

    public void turnRight() {
        actor.turnRight();
    }

    public void pickUp() {
        actor.pickUp();
    }

    public void putDown() {
        actor.putDown();
    }

    /** Create a dialog window which asks the user to input how many presents should be in the basket. */
    public void setPresentCount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PresentsDialogView.fxml"));
            DialogPane dialogPane = loader.load();

            PresentsDialogController controller = loader.getController();
            controller.setPresentsInputText(String.valueOf(territory.getActorPresentCount()));

            Dialog<String> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Geschenke im Korb");

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return controller.getPresentsInputText();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(result ->
                    territory.setActorPresentCount(Integer.parseInt(result))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Fill the context menu with items representing the methods from the base actor class and the extended actor class. */
    public void createActionContextMenu(Stage stage, double x, double y) {
        this.actor = territory.getActor();
        ContextMenu contextMenu = new ContextMenu();

        // base classes public methods
        Class<Actor> actorClass = Actor.class;
        createMenuItems(contextMenu, actorClass.getDeclaredMethods(), true);

        // extended classes methods
        if (actor.getClass() != actorClass) {
            createMenuItems(contextMenu, actor.getClass().getDeclaredMethods(), false);
        }

        contextMenu.show(stage, x, y);
    }

    /**
     * Create menuItems for the actor's methods. Does not list abstract, private, static or @Invisible methods.
     * Gives the option to also only list the public methods.
     *
     * @param methods    list of methods the class has
     * @param onlyPublic information whether only public methods should be
     */
    private void createMenuItems(ContextMenu contextMenu, Method[] methods, boolean onlyPublic) {
        for (Method method : methods) {
            int modifiers = method.getModifiers();
            if (Modifier.isAbstract(modifiers) || Modifier.isPrivate(modifiers)
                    || Modifier.isStatic(modifiers) || (onlyPublic && !Modifier.isPublic(modifiers))
                    || method.getAnnotation(Invisible.class) != null) {
                continue;
            }
            String returnType = method.getReturnType().getName();
            String methodName = method.getName();

            Class<?>[] parameterClasses = method.getParameterTypes();
            String parameterTypes = Arrays.stream(parameterClasses)
                    .map(Class::getName)
                    .reduce((s1, s2) -> s1 + ", " + s2)
                    .orElse("");

            MenuItem item = new MenuItem(returnType + " " + methodName + "(" + parameterTypes + ")");
            item.setOnAction(a -> {
                try {
                    territory.pauseNotify();
                    // currently, the methods have to be created as public, else I would need to set everything accessible
                    method.setAccessible(true);
                    method.invoke(actor);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    if (e.getCause() instanceof ActorException) {
                        errorSound.play(0.2);
                    } else {
                        new Alert(Alert.AlertType.ERROR, e.getCause().getMessage(), ButtonType.OK).showAndWait();
                        e.printStackTrace();
                    }
                } finally {
                    territory.resumeNotify();
                }
            });

            if (parameterClasses.length != 0) {
                item.setDisable(true);
            }
            contextMenu.getItems().add(item);
        }
    }
}
