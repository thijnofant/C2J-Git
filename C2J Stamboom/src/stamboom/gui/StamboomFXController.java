/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.*;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.*;
import stamboom.controller.StamboomController;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;
import stamboom.util.StringUtilities;

/**
 *
 * @author frankpeeters
 */
public class StamboomFXController extends StamboomController implements Initializable {

    //MENUs en TABs
    @FXML MenuBar menuBar;
    @FXML MenuItem miNew;
    @FXML MenuItem miOpen;
    @FXML MenuItem miSave;
    @FXML CheckMenuItem cmDatabase;
    @FXML MenuItem miClose;
    @FXML Tab tabPersoon;
    @FXML Tab tabGezin;
    @FXML Tab tabPersoonInvoer;
    @FXML Tab tabGezinInvoer;

    //PERSOON
    @FXML ComboBox cbPersonen;
    @FXML TextField tfPersoonNr;
    @FXML TextField tfVoornamen;
    @FXML TextField tfTussenvoegsel;
    @FXML TextField tfAchternaam;
    @FXML TextField tfGeslacht;
    @FXML TextField tfGebDatum;
    @FXML TextField tfGebPlaats;
    @FXML ComboBox cbOuderlijkGezin;
    @FXML ListView lvAlsOuderBetrokkenBij;
    @FXML Button btStamboom;

    //GEZIN
    @FXML ComboBox cbGezinnen;
    @FXML TextField tfOuder1;
    @FXML TextField tfOuder2;
    @FXML TextField tfHuwelijk;
    @FXML TextField tfScheiding;
    @FXML Button btBevestigGezin;

    //INVOER NIEUW GEZIN
    @FXML ComboBox cbOuder1Invoer;
    @FXML ComboBox cbOuder2Invoer;
    @FXML TextField tfHuwelijkInvoer;
    @FXML TextField tfScheidingInvoer;
    @FXML Button btOKGezinInvoer;
    @FXML Button btCancelGezinInvoer;

    //INVOER NIEUW PERSOON
    @FXML TextField tfVoornamen1;
    @FXML TextField tfTussenvoegsel1;
    @FXML TextField tfAchternaam1;
    @FXML TextField tfGeslacht1;
    @FXML TextField tfGebDatum1;
    @FXML TextField tfGebPlaats1;
    @FXML ComboBox cbOuderlijkGezin1;
    @FXML Button btnMaakPersoon;
    @FXML Button btnCancelPersoon;

    //opgave 4
    private boolean withDatabase;
    private StamboomController controller;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        withDatabase = false;
        controller = new StamboomController();
        initComboboxes();
    }

    private void initComboboxes() {

        cbPersonen.setItems(this.controller.getAdministratie().getPersonen());
        cbOuderlijkGezin.setItems(this.controller.getAdministratie().getGezinnen());
        cbOuder1Invoer.setItems(this.controller.getAdministratie().getPersonen());
        cbOuder2Invoer.setItems(this.controller.getAdministratie().getPersonen());
        controller.getAdministratie().getPersonen().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                cbPersonen.setItems(controller.getAdministratie().getPersonen());
                cbOuder1Invoer.setItems(controller.getAdministratie().getPersonen());
                cbOuder2Invoer.setItems(controller.getAdministratie().getPersonen());
            }
        });

        cbGezinnen.setItems(this.controller.getAdministratie().getGezinnen());
        cbOuderlijkGezin1.setItems(this.controller.getAdministratie().getGezinnen());
        controller.getAdministratie().getGezinnen().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                cbGezinnen.setItems(controller.getAdministratie().getGezinnen());
                cbOuderlijkGezin.setItems(controller.getAdministratie().getGezinnen());
                cbOuderlijkGezin1.setItems(controller.getAdministratie().getGezinnen());
            }
        });

    }

    public void selectPersoon(Event evt) {
        Persoon persoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
        showPersoon(persoon);
    }

    private void showPersoon(Persoon persoon) {
        if (persoon == null) {
            clearTabPersoon();
        } else {
            tfPersoonNr.setText(persoon.getNr() + "");
            tfVoornamen.setText(persoon.getVoornamen());
            tfTussenvoegsel.setText(persoon.getTussenvoegsel());
            tfAchternaam.setText(persoon.getAchternaam());
            tfGeslacht.setText(persoon.getGeslacht().toString());
            tfGebDatum.setText(StringUtilities.datumString(persoon.getGebDat()));
            tfGebPlaats.setText(persoon.getGebPlaats());
            if (persoon.getOuderlijkGezin() != null) {
                cbOuderlijkGezin.getSelectionModel().select(persoon.getOuderlijkGezin());
            } else {
                cbOuderlijkGezin.getSelectionModel().clearSelection();
            }
        }
    }

    public void setOuders(Event evt) {
        if (tfPersoonNr.getText().isEmpty()) {
            this.showWindow("Selecteer de persoon");
            return;
        }
        Gezin ouderlijkGezin = (Gezin) cbOuderlijkGezin.getSelectionModel().getSelectedItem();
        if (ouderlijkGezin == null) {
            this.showWindow("Selecteer het gezin waar deze persoon kind van is");
            return;
        }

        int nr = Integer.parseInt(tfPersoonNr.getText());
        Persoon p = controller.getAdministratie().getPersoon(nr);
        if (controller.getAdministratie().setOuders(p, ouderlijkGezin)) {
            //showDialog("Success", ouderlijkGezin.toString() + " is nu het ouderlijk gezin van " + p.getNaam());
            this.showWindow("Success \n" + ouderlijkGezin.toString() + " is nu het ouderlijk gezin van " + p.getNaam());
        }

    }

    public void selectGezin(Event evt) {
        Gezin gezin = (Gezin) cbGezinnen.getSelectionModel().getSelectedItem();
        showGezin(gezin);
    }

    private void showGezin(Gezin gezin) {
        if (gezin == null) {
            return;
        }
        if (!(gezin.getOuder1() == null)) {
            tfOuder1.setText(gezin.getOuder1().toString());
        } else {
            tfOuder1.clear();
        }
        if (!(gezin.getOuder2() == null)) {
            tfOuder2.setText(gezin.getOuder2().toString());
        } else {
            tfOuder2.clear();
        }
        if (!(gezin.getHuwelijksdatum() == null)) {
            tfHuwelijk.setText(StringUtilities.datumString(gezin.getHuwelijksdatum()));
        } else {
            tfHuwelijk.clear();
        }
        if (!(gezin.getScheidingsdatum() == null)) {
            tfScheiding.setText(StringUtilities.datumString(gezin.getScheidingsdatum()));
        } else {
            tfScheiding.clear();
        }
    }

    public void setHuwdatum(Event evt) {
        Gezin gezin = (Gezin) cbGezinnen.getSelectionModel().getSelectedItem();
        SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy");
        Calendar huwdat = Calendar.getInstance();
        try {
            huwdat.setTime(df.parse(tfHuwelijk.textProperty().getValue()));
            controller.getAdministratie().getGezin(gezin.getNr()).setHuwelijk(huwdat);
        } catch (ParseException ex) {
            System.out.println("Date conversion failed.");
            this.showWindow("Controleer de datum");
            return;
        }
        this.showWindow("Huwelijks datum opgeslagen");
    }

    public void setScheidingsdatum(Event evt) {
        Gezin gezin = (Gezin) cbGezinnen.getSelectionModel().getSelectedItem();
        SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy");
        Calendar scheidat = Calendar.getInstance();
        try {
            scheidat.setTime(df.parse(tfScheiding.textProperty().getValue()));
            controller.getAdministratie().getGezin(gezin.getNr()).setScheiding(scheidat);
        } catch (ParseException ex) {
            System.out.println("Date conversion failed.");
            this.showWindow("Controleer de datum");
            return;
        }
        this.showWindow("Scheidings datum opgeslagen");
    }

    public void cancelPersoonInvoer(Event evt) {
        clearTabPersoonInvoer();

    }

    public void okPersoonInvoer(Event evt) {
        if (tfVoornamen1.textProperty().isEmpty().getValue()
                || tfAchternaam1.textProperty().isEmpty().getValue()
                || tfGeslacht1.textProperty().isEmpty().getValue()
                || tfGebDatum1.textProperty().isEmpty().getValue()
                || tfGebPlaats1.textProperty().isEmpty().getValue()) {
            this.showWindow("Controleer of alle velden zijn ingevuld");
            return;
        }
        String vnamenstring = tfVoornamen1.textProperty().getValue();
        String[] vnamen = vnamenstring.split(" ");
        String tussenvoegsel = "";
        if (!(tfTussenvoegsel1.getText().equals(""))) {
            tussenvoegsel = tfTussenvoegsel1.textProperty().getValue();
        }
        Geslacht geslacht = null;
        if (tfGeslacht1.textProperty().getValue().toLowerCase().charAt(0) == 'm') {
            geslacht = Geslacht.MAN;
        } else if (tfGeslacht1.textProperty().getValue().toLowerCase().charAt(0) == 'v') {
            geslacht = Geslacht.VROUW;
        } else {
            this.showWindow("Controleer geslacht");
            return;
        }
        String anaam = tfAchternaam1.textProperty().getValue();
        SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy");
        Calendar gebdat = Calendar.getInstance();
        try {
            gebdat.setTime(df.parse(tfGebDatum1.textProperty().getValue()));
        } catch (ParseException ex) {
            System.out.println("Date conversion failed.");
            this.showWindow("Controleer de datum");
        }
        String gebPlaats = tfGebPlaats1.textProperty().getValue();

        Gezin gezin = (Gezin) cbOuderlijkGezin1.getSelectionModel().getSelectedItem();

        if (controller.getAdministratie().addPersoon(geslacht, vnamen, anaam, tussenvoegsel, gebdat, gebPlaats, gezin) == null) {
            this.showWindow("er is iets fout gegaan in het maken van de persoon");
        }
        this.showWindow("Persoon is gemaakt");
        clearTabPersoonInvoer();
    }

    public void okGezinInvoer(Event evt) {
        Persoon ouder1 = (Persoon) cbOuder1Invoer.getSelectionModel().getSelectedItem();
        if (ouder1 == null) {
            this.showWindow("Warning \nEerste ouder is niet ingevoerd");
            return;
        }
        Persoon ouder2 = (Persoon) cbOuder2Invoer.getSelectionModel().getSelectedItem();
        Calendar huwdatum;
        try {
            huwdatum = StringUtilities.datum(tfHuwelijkInvoer.getText());
        } catch (IllegalArgumentException exc) {
            this.showWindow("Warning \nhuwelijksdatum :" + exc.getMessage());
            return;
        }
        Gezin g;
        if (huwdatum != null) {
            g = controller.getAdministratie().addHuwelijk(ouder1, ouder2, huwdatum);
            if (g == null) {
                showDialog("Warning", "Invoer huwelijk is niet geaccepteerd");
            } else {
                Calendar scheidingsdatum;
                try {
                    scheidingsdatum = StringUtilities.datum(tfScheidingInvoer.getText());
                    if (scheidingsdatum != null) {
                        controller.getAdministratie().setScheiding(g, scheidingsdatum);
                    }
                } catch (IllegalArgumentException exc) {
                    this.showWindow("Warning \nscheidingsdatum: " + exc.getMessage());

                }
            }
        } else {
            g = controller.getAdministratie().addOngehuwdGezin(ouder1, ouder2);
            if (g == null) {
                this.showWindow("Warning \nInvoer ongehuwd gezin is niet geaccepteerd");
            }
        }

        clearTabGezinInvoer();
    }

    public void cancelGezinInvoer(Event evt) {
        clearTabGezinInvoer();
    }

    public void showStamboom(Event evt) {
        // todo opgave 3 DONE
        String printString = "Selecteer een persoon";
        try {
            Persoon pers = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
            printString = pers.stamboomAlsString();
        } finally {
            this.showWindow(printString);
        }
    }

    public void createEmptyStamboom(Event evt) {
        this.clearAdministratie();
        clearTabs();
        initComboboxes();
    }

    public void openStamboom(Event evt) throws IOException {
        // todo opgave 3
        if (withDatabase) {
            try {
             this.controller.loadFromDatabase();   
             clearTabs();
             initComboboxes();
            this.showWindow("Administration was Loaded from database");
            } catch (Exception e) {
                this.showWindow("Something went wrong during loading");
            }
            
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Administration File...");
            File file = fileChooser.showOpenDialog(this.getStage());
            try {
                if (this.controller.deserialize(file)) {
                    clearTabs();
                    initComboboxes();
                    this.showWindow("Administration was Loaded from file");
                }
            } catch (Exception e) {
                this.showWindow("Something went wrong during loading");
            }
            
        }
    }

    public void saveStamboom(Event evt) throws IOException {
        // todo opgave 3
        if (withDatabase) {
            try {
                this.controller.saveToDatabase();
                this.showWindow("Administration was Saved");
            } catch (Exception e) {
                this.showWindow("Something went wrong during saving");
            }
            
            
        } else {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save As...");
                File file = fileChooser.showSaveDialog(this.getStage());
                this.controller.serialize(file);
                this.showWindow("Administration was Saved");
            } catch (Exception e) {
                this.showWindow("Something went wrong during saving");
            }
        }
    }

    public void closeApplication(Event evt) throws IOException {
        saveStamboom(evt);
        getStage().close();
    }

    public void configureStorage(Event evt) {
        withDatabase = cmDatabase.isSelected();
    }

    public void selectTab(Event evt) {
        Object source = evt.getSource();
        if (source == tabPersoon) {
            clearTabPersoon();
        } else if (source == tabGezin) {
            clearTabGezin();
        } else if (source == tabPersoonInvoer) {
            clearTabPersoonInvoer();
        } else if (source == tabGezinInvoer) {
            clearTabGezinInvoer();
        }
    }

    private void clearTabs() {
        clearTabPersoon();
        clearTabPersoonInvoer();
        clearTabGezin();
        clearTabGezinInvoer();
    }

    private void clearTabPersoonInvoer() {
        tfVoornamen1.clear();
        tfTussenvoegsel1.clear();
        tfAchternaam1.clear();
        tfGeslacht1.clear();
        tfGebDatum1.textProperty().set("dd-mm-yyyy");
        tfGebPlaats1.clear();
        cbOuderlijkGezin1.getSelectionModel().clearSelection();
    }

    private void clearTabGezinInvoer() {
        cbOuder1Invoer.getSelectionModel().clearSelection();
        cbOuder2Invoer.getSelectionModel().clearSelection();
        tfHuwelijkInvoer.clear();
        tfScheidingInvoer.clear();
    }

    private void clearTabPersoon() {
        cbPersonen.getSelectionModel().clearSelection();
        tfPersoonNr.clear();
        tfVoornamen.clear();
        tfTussenvoegsel.clear();
        tfAchternaam.clear();
        tfGeslacht.clear();
        tfGebDatum.clear();
        tfGebPlaats.clear();
        cbOuderlijkGezin.getSelectionModel().clearSelection();
        lvAlsOuderBetrokkenBij.setItems(FXCollections.emptyObservableList());
    }

    private void clearTabGezin() {
        cbGezinnen.getSelectionModel().clearSelection();
        tfOuder1.clear();
        tfOuder2.clear();
        tfHuwelijk.clear();
        tfScheiding.clear();
    }

    private void showDialog(String type, String message) {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    private Stage getStage() {
        return (Stage) menuBar.getScene().getWindow();
    }

    private void showWindow(String message) {
        Stage dialogStage = new Stage();
        dialogStage.setMinWidth(420);
        dialogStage.setMaxWidth(420);
        dialogStage.setMinHeight(240);
        dialogStage.setMaxHeight(300);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(VBoxBuilder.create().
                children(new Text(message)).
                alignment(Pos.CENTER).padding(new Insets(5)).build()));
        dialogStage.show();
    }
}
