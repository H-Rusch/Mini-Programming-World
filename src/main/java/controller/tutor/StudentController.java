package controller.tutor;

import controller.FXMLController;
import controller.save.SaveController;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.tutor.Answer;
import model.tutor.Request;
import model.tutor.Tutor;
import util.I18nUtil;
import util.PropertyController;

import javax.xml.stream.XMLStreamException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

public class StudentController implements TutorStudentController {

    private final FXMLController fxmlController;
    private final SaveController saveController;

    private Tutor tutor;
    private UUID lastRequestId;

    public StudentController(SaveController saveController, FXMLController fxmlController) {
        this.saveController = saveController;
        this.fxmlController = fxmlController;

        setUpEventHandlers();
    }

    private void setUpEventHandlers() {
        fxmlController.sendStudentAnswerMenuItem.setVisible(false);
        fxmlController.receiveStudentRequestMenuItem.setVisible(false);

        fxmlController.receiveTutorAnswerMenuItem.setDisable(true);

        fxmlController.sendTutorRequestMenuItem.setOnAction(a -> sendTutorRequest());
        fxmlController.receiveTutorAnswerMenuItem.setOnAction(a -> receiveTutorAnswer());
    }

    /** Connect to the tutor. Enable the button to send requests if successful. */
    private void connectToTutor() {
        if (tutor != null) {
            return;
        }
        // lookup the tutor from the registry
        try {
            Registry registry = LocateRegistry.getRegistry(PropertyController.getTutorHost(),
                    Integer.parseInt(PropertyController.getTutorPort()));
            this.tutor = (Tutor) registry.lookup("tutor");

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, I18nUtil.i18n("alert.tutor.connectionError"), ButtonType.OK).show();
        }
    }

    /** Send a request to the registered tutor, containing the current code and territory as an XML String. */
    private void sendTutorRequest() {
        // connect to the tutor, when sending a request for the first time
        connectToTutor();

        try {
            UUID newRequestId = UUID.randomUUID();
            Request request = new Request(newRequestId, fxmlController.codeTextArea.getText(), saveController.getTerritoryXMLString());
            tutor.sendRequest(lastRequestId, request);
            lastRequestId = newRequestId;

            fxmlController.receiveTutorAnswerMenuItem.setDisable(false);

            fxmlController.updateNotificationText(I18nUtil.i18n("notification.tutor.sentRequest"));
        } catch (XMLStreamException | RemoteException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, I18nUtil.i18n("alert.tutor.sendRequestError"),
                    ButtonType.OK).show();
        }
    }

    /** Receive the answer to a request from the tutor. Load the territory and code given in the answer. */
    private void receiveTutorAnswer() {
        try {
            Answer answer = tutor.receiveAnswer(lastRequestId);
            if (answer != null) {
                lastRequestId = null;

                fxmlController.codeTextArea.setText(answer.getCode());
                saveController.loadTerritoryFromXMLString(answer.getTerritoryXML());

                fxmlController.updateNotificationText(I18nUtil.i18n("notification.tutor.loadedAnswer"));
            } else {
                new Alert(Alert.AlertType.INFORMATION, I18nUtil.i18n("alert.tutor.noAnswerYet"),
                        ButtonType.OK).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, I18nUtil.i18n("alert.tutor.sendAnswerError"),
                    ButtonType.OK).show();
        }

    }
}
