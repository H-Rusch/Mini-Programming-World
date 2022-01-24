package controller.tutor;

import controller.FXMLController;
import controller.save.SaveController;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.tutor.Answer;
import model.tutor.Request;
import model.tutor.Tutor;
import model.tutor.TutorImpl;
import util.PropertyController;

import javax.xml.stream.XMLStreamException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;
import java.util.UUID;

public class TutorController implements TutorStudentController {

    private final FXMLController fxmlController;
    private final SaveController saveController;

    private static Integer active = 0;
    private static Tutor tutor;

    private UUID activeRequestId;

    public TutorController(SaveController saveController, FXMLController fxmlController) {
        this.saveController = saveController;
        this.fxmlController = fxmlController;

        setUpEventHandlers();

        TutorController.start();
    }

    private void setUpEventHandlers() {
        fxmlController.sendTutorRequestMenuItem.setVisible(false);
        fxmlController.receiveTutorAnswerMenuItem.setVisible(false);

        fxmlController.sendStudentAnswerMenuItem.setDisable(true);

        fxmlController.receiveStudentRequestMenuItem.setOnAction(a -> receiveStudentRequest());
        fxmlController.sendStudentAnswerMenuItem.setOnAction(a -> sendStudentAnswer());
    }

    /** Load a request from a student into the simulator. */
    private void receiveStudentRequest() {
        // alert the tutor when trying to load a new request when the last one wan not answered yet
        if (activeRequestId != null) {
            Alert alert =
                    new Alert(Alert.AlertType.WARNING,
                            "Auf die zuletzt geladene Anfrage wurde noch keine Antwort geschickt. Wirklich laden?",
                            ButtonType.OK,
                            ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return;
            }
        }

        // load the oldest request
        try {
            Request request = tutor.receiveRequest();
            if (request != null) {
                activeRequestId = request.getId();

                fxmlController.codeTextArea.setText(request.getCode());
                saveController.loadTerritoryFromXMLString(request.getTerritoryXML());

                fxmlController.sendStudentAnswerMenuItem.setDisable(false);
                fxmlController.updateNotificationText("Studierendenanfrage geladen");
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Es ist keine weitere Anfrage eingegangen.",
                        ButtonType.OK).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Bei der Übertragung der Anfrage ist ein Fehler aufgetreten.",
                    ButtonType.OK).show();
        }
    }

    /** Send the current code and territory to the student. */
    private void sendStudentAnswer() {
        if (activeRequestId == null) {
            return;
        }
        try {
            Answer answer = new Answer(activeRequestId, fxmlController.codeTextArea.getText(), saveController.getTerritoryXMLString());
            activeRequestId = null;
            fxmlController.sendStudentAnswerMenuItem.setDisable(true);
            tutor.sendAnswer(answer);

            fxmlController.updateNotificationText("Antwort an Studierenden gesendet");
        } catch (XMLStreamException | RemoteException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Bei der Übertragung der Antwort ist ein Fehler aufgetreten.",
                    ButtonType.OK).show();
        }
    }

    /** Create the registry and export the tutor. */
    private static void start() {
        // create the registry only for the first tutor window
        if (active == 0) {
            try {
                tutor = new TutorImpl();

                Registry registry = LocateRegistry.createRegistry(Integer.parseInt(PropertyController.getTutorPort()));
                registry.rebind("tutor", tutor);

                System.out  .println(registry);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        active++;
    }

    /** Unexports the exported objects when the last tutor window is closed. */
    public static void end() {
        active--;
        if (active == 0) {
            try {
                UnicastRemoteObject.unexportObject(tutor, true);
            } catch (NoSuchObjectException e) {
                e.printStackTrace();
            }
        }
    }
}
