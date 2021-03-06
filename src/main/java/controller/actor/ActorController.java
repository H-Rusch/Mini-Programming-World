package controller.actor;

import controller.FXMLController;
import controller.territory.PresentsDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import model.territory.Actor;
import model.territory.Territory;
import model.territory.exceptions.ActorException;
import util.I18nUtil;
import util.annotations.Invisible;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ActorController {

    private final FXMLController fxmlController;
    private final Territory territory;
    private Actor actor;

    public ActorController(Territory territory, Actor actor, FXMLController controller) {
        this.territory = territory;
        this.actor = actor;
        this.fxmlController = controller;

        setUpEventHandlers();
    }

    /** Add EventHandlers for the interaction between buttons and the customer. */
    private void setUpEventHandlers() {
        fxmlController.forwardButton.setOnAction(a -> forward());
        fxmlController.forwardMenuItem.setOnAction(a -> forward());

        fxmlController.turnLeftButton.setOnAction(a -> turnLeft());
        fxmlController.turnLeftMenuItem.setOnAction(a -> turnLeft());

        fxmlController.turnRightButton.setOnAction(a -> turnRight());
        fxmlController.turnRightMenuItem.setOnAction(a -> turnRight());

        fxmlController.pickUpButton.setOnAction(a -> pickUp());
        fxmlController.pickUpMenuItem.setOnAction(a -> pickUp());

        fxmlController.putDownButton.setOnAction(a -> putDown());
        fxmlController.putDownMenuItem.setOnAction(a -> putDown());

        fxmlController.presentsButton.setOnAction(a -> setPresentCount());
        fxmlController.presentsMenuItem.setOnAction(a -> setPresentCount());
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
            controller.setPresentsInputText(String.valueOf(territory.getActorPresents()));

            Dialog<String> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(I18nUtil.i18n("dialog.present.title"));

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return controller.getPresentsInputText();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(result ->
                    territory.setActorPresents(Integer.parseInt(result))
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
                    method.setAccessible(true);
                    method.invoke(actor);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    if (e.getCause() instanceof ActorException) {
                        new AudioClip(String.valueOf(getClass().getResource("/sound/error_sound.mp3"))).play(0.2);
                    } else {
                        e.printStackTrace();
                    }
                    new Alert(Alert.AlertType.ERROR, e.getCause().getMessage(), ButtonType.OK).showAndWait();
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
