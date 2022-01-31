package controller.save;

import controller.FXMLController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.territory.Direction;
import model.territory.Territory;
import model.territory.Tile;
import model.territory.TileState;
import util.I18nUtil;
import util.Position;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class SaveController {

    private final FXMLController fxmlController;
    private final Stage stage;
    private final Territory territory;

    private final String territoryDTD;

    private static final String IMAGE_DIRECTORY = "images";
    private static final String TERRITORY_DIRECTORY = "territories";


    public SaveController(Territory territory, Stage stage, FXMLController controller) {
        this.fxmlController = controller;
        this.stage = stage;
        this.territory = territory;

        setUpEventHandlers();
        this.territoryDTD = createDTD();
    }

    /** Add EventHandlers so the user can save and load the territory. */
    private void setUpEventHandlers() {
        fxmlController.serializeMenuItem.setOnAction(a -> serializeTerritory());
        fxmlController.deserializeMenuItem.setOnAction(a -> deserializeTerritory());

        fxmlController.saveXmlMenuItem.setOnAction(a -> saveTerritoryXML());
        fxmlController.loadXmlMenuItem.setOnAction(a -> loadTerritoryFromXMLFile());

        fxmlController.saveImageMenuItem.setOnAction(a -> saveScreenshot());
    }

    /** Save the territory to a file by serializing it. */
    public void serializeTerritory() {
        FileChooser fileChooser = new FileChooser();
        createDirectoryIfNotExists(TERRITORY_DIRECTORY);
        fileChooser.setInitialDirectory(new File(TERRITORY_DIRECTORY));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Territory Files (*.ter)",
                "*.ter"));
        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(selectedFile.getAbsolutePath())))) {
                synchronized (territory) {
                    out.writeObject(territory);
                    // save the territory's state, so the territory can be reset to this state
                    territory.saveState();
                }
                fxmlController.updateNotificationText(I18nUtil.i18n("notification.territory.saved"));
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, I18nUtil.i18n("alert.save.saveTerritoryError"),
                        ButtonType.OK).show();
            }
        }
    }

    /** Load a territory from a file by deserializing it. */
    public void deserializeTerritory() {
        FileChooser fileChooser = new FileChooser();
        createDirectoryIfNotExists(TERRITORY_DIRECTORY);
        fileChooser.setInitialDirectory(new File(TERRITORY_DIRECTORY));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Territory Files (*.ter)",
                "*.ter"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try (ObjectInputStream in = new ObjectInputStream(
                    new BufferedInputStream(new FileInputStream(selectedFile.getAbsolutePath())))) {
                synchronized (territory) {
                    Territory loaded = (Territory) in.readObject();

                    this.territory.loadTerritory(loaded);
                    // save the territory's state, so the territory can be reset to this state
                    this.territory.saveState();
                }
                fxmlController.updateNotificationText(I18nUtil.i18n("notification.territory.loaded"));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, I18nUtil.i18n("alert.save.loadTerritoryError"),
                        ButtonType.OK).show();
            }
        }
    }

    /** Save a territory in the XML format. */
    public void saveTerritoryXML() {
        FileChooser fileChooser = new FileChooser();
        createDirectoryIfNotExists(TERRITORY_DIRECTORY);
        fileChooser.setInitialDirectory(new File(TERRITORY_DIRECTORY));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Territory XML Files (*.txml)",
                "*.txml"));
        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            try {
                String territoryXML = getTerritoryXMLString();
                Files.write(Paths.get(selectedFile.getAbsolutePath()), territoryXML.getBytes(StandardCharsets.UTF_8));

                fxmlController.updateNotificationText(I18nUtil.i18n("notification.territory.saved"));
            } catch (IOException | XMLStreamException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, I18nUtil.i18n("alert.save.saveTerritoryError"),
                        ButtonType.OK).show();
            }
        }
    }

    /** Load a territory from a file. */
    private void loadTerritoryFromXMLFile() {
        FileChooser fileChooser = new FileChooser();
        createDirectoryIfNotExists(TERRITORY_DIRECTORY);
        fileChooser.setInitialDirectory(new File(TERRITORY_DIRECTORY));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Territory XML Files (*.txml)",
                "*.txml"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // load territories XML from the file and then load the territory
                String territoryXML = Files.readAllLines(Paths.get(selectedFile.getAbsolutePath())).stream()
                        .reduce(String::concat)
                        .orElse("");

                loadTerritoryFromXMLString(territoryXML);
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, I18nUtil.i18n("alert.save.loadTerritoryError"),
                        ButtonType.OK).show();
            }
        }
    }


    /** Load a territory from a String in the XML format. */
    public void loadTerritoryFromXMLString(String territoryXML) {
        Territory newTerritory = new Territory(1, 1);

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader parser = factory.createXMLEventReader(
                    new StringReader(territoryXML));

            while (parser.hasNext()) {
                XMLEvent event = parser.nextEvent();
                switch (event.getEventType()) {
                    case XMLStreamConstants.END_DOCUMENT:
                        parser.close();
                        break;
                    case XMLStreamConstants.START_ELEMENT:
                        handleStartingElement(newTerritory, event.asStartElement());
                        break;
                    default:
                        break;
                }
            }

            synchronized (territory) {
                territory.loadTerritory(newTerritory);
                // save the territory's state, so the territory can be reset to this state
                territory.saveState();
            }

            fxmlController.updateNotificationText(I18nUtil.i18n("notification.territory.loaded"));
        } catch (XMLStreamException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, I18nUtil.i18n("alert.save.loadTerritoryError"),
                    ButtonType.OK).show();
        }
    }


    /** Save a screenshot of the TerritoryPanel and save it to the disk */
    public void saveScreenshot() {
        WritableImage screenshot = fxmlController.territoryPanel.snapshot(null, null);

        FileChooser fileChooser = new FileChooser();

        createDirectoryIfNotExists(IMAGE_DIRECTORY);
        fileChooser.setInitialDirectory(new File(IMAGE_DIRECTORY));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images (*.png, *.gif, *.jpg)",
                "*.png", "*.gif", "*.jpg"));
        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(screenshot, null), "png", selectedFile);
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, I18nUtil.i18n("alert.save.saveImageError"),
                        ButtonType.OK).show();
            }
        }
    }

    private void createDirectoryIfNotExists(String directoryName) {
        Path path = Paths.get(directoryName);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Get a String for the XML representation for the current territory. */
    public String getTerritoryXMLString() throws XMLStreamException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        StringWriter stringOut = new StringWriter();
        XMLStreamWriter writer = factory.createXMLStreamWriter(stringOut);
        writer.writeStartDocument("utf-8", "1.1");
        writer.writeCharacters("\n");

        writer.writeDTD(territoryDTD);
        writer.writeCharacters("\n");

        synchronized (territory) {
            writer.writeStartElement("territory"); // <territory>
            writer.writeAttribute("height", String.valueOf(territory.getHeight()));
            writer.writeAttribute("width", String.valueOf(territory.getWidth()));
            writer.writeCharacters("\n");

            writer.writeStartElement("actor"); // <actor>
            writer.writeAttribute("actorPresents", String.valueOf(territory.getActorPresents()));
            writer.writeAttribute("actorDirection", String.valueOf(territory.getActorDirection()));
            writer.writeCharacters("\n");

            writer.writeStartElement("actorPosition"); // <actorPosition>
            writer.writeAttribute("x", String.valueOf(territory.getActorPosition().getX()));
            writer.writeAttribute("y", String.valueOf(territory.getActorPosition().getY()));
            writer.writeEndElement(); // </actorPosition>
            writer.writeCharacters("\n");

            writer.writeEndElement(); // </actor>
            writer.writeCharacters("\n");


            Tile[][] market = territory.getMarket();
            for (int y = 0; y < market.length; y++) {
                for (int x = 0; x < market[y].length; x++) {
                    if (market[y][x].getState() != TileState.EMPTY) {
                        writer.writeStartElement("tile"); // <tile>
                        writer.writeAttribute("x", String.valueOf(x));
                        writer.writeAttribute("y", String.valueOf(y));
                        writer.writeAttribute("type", market[y][x].getState().name());
                        writer.writeEndElement(); // </tile>
                        writer.writeCharacters(" \n");
                    }
                }
            }

            // save the territory's state, so the territory can be reset to this state
            territory.saveState();
        }

        writer.writeEndElement(); // </territory>
        writer.writeEndDocument();
        writer.close();

        return stringOut.toString();
    }

    /** Handle an opening element when parsing the XML file while loading. */
    private void handleStartingElement(Territory newTerritory, StartElement element) {
        if ("territory".equals(element.getName().getLocalPart())) {
            int height = Integer.parseInt(
                    element.getAttributeByName(new QName("height")).getValue());
            int width = Integer.parseInt(
                    element.getAttributeByName(new QName("width")).getValue());

            newTerritory.resizeTerritory(height, width);

        } else if ("actor".equals(element.getName().getLocalPart())) {
            int presentCount = Integer.parseInt(
                    element.getAttributeByName(new QName("actorPresents")).getValue());

            Direction actorDirection = Direction.getElementByString(
                    element.getAttributeByName(new QName("actorDirection")).getValue());

            newTerritory.setActorPresents(presentCount);
            newTerritory.setActorDirection(actorDirection);

        } else if ("actorPosition".equals(element.getName().getLocalPart())) {
            int x = Integer.parseInt(
                    element.getAttributeByName(new QName("x")).getValue());
            int y = Integer.parseInt(
                    element.getAttributeByName(new QName("y")).getValue());

            newTerritory.setActorPosition(new Position(x, y));

        } else if ("tile".equals(element.getName().getLocalPart())) {
            int x = Integer.parseInt(
                    element.getAttributeByName(new QName("x")).getValue());
            int y = Integer.parseInt(
                    element.getAttributeByName(new QName("y")).getValue());
            TileState state = TileState.getElementByString(
                    element.getAttributeByName(new QName("type")).getValue());

            newTerritory.getMarket()[y][x] = new Tile(state);
        }
    }

    /** DTD for XML files containing a territory. */
    private String createDTD() {
        return String.join("\n", "<!DOCTYPE territory [",
                "<!ELEMENT territory (actor, tile*)>",
                "<!ATTLIST territory",
                "   height CDATA #REQUIRED",
                "   width CDATA #REQUIRED",
                ">",
                "<!ELEMENT actor (actorPosition) >",
                "<!ATTLIST actor",
                "   actorPresents CDATA #REQUIRED",
                "   actorDirection (EAST | NORTH | WEST | SOUTH) #REQUIRED",
                ">",
                "<!ELEMENT actorPosition EMPTY >",
                "<!ATTLIST actorPosition",
                "   x CDATA #REQUIRED",
                "   y CDATA #REQUIRED",
                ">",
                "<!ELEMENT tile EMPTY >",
                "<!ATTLIST tile",
                "   x CDATA #REQUIRED",
                "   y CDATA #REQUIRED",
                "   type (SHELF | CART| PRESENT | PRESENT_AND_CART) #REQUIRED",
                ">",
                "]>");
    }
}
