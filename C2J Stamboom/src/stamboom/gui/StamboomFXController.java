/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.gui;

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
    @FXML ComboBox selectGezin;
    @FXML ComboBox cbOuder1;
    @FXML ComboBox cbOuder2;
    @FXML TextField tfHuwelijkInvoer1;
    @FXML TextField tfScheidingInvoer1;
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
        
        //Test persoon voor het testen van de CB
        //String[] namen;
        //namen = new String[]{"test"};
        //GregorianCalendar GC = new GregorianCalendar();
        //controller.getAdministratie().addPersoon(Geslacht.MAN, namen , "Adkin", "De", GC, "Venray", null);
        //Test: Geslaagd.
        
        cbPersonen.setItems(this.controller.getAdministratie().getPersonen());
        //selectGezin.setItems(this.controller.getAdministratie().getGezinnen());
        
        
        controller.getAdministratie().getPersonen().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                cbPersonen = new ComboBox(controller.getAdministratie().getPersonen());
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

            //todo opgave 3
            //lvAlsOuderBetrokkenBij.setItems(persoon.getAlsOuderBetrokkenIn());
        }
    }

    public void setOuders(Event evt) {
        if (tfPersoonNr.getText().isEmpty()) {
            return;
        }
        Gezin ouderlijkGezin = (Gezin) cbOuderlijkGezin.getSelectionModel().getSelectedItem();
        if (ouderlijkGezin == null) {
            return;
        }

        int nr = Integer.parseInt(tfPersoonNr.getText());
        Persoon p = getAdministratie().getPersoon(nr);
        if(getAdministratie().setOuders(p, ouderlijkGezin)){
            showDialog("Success", ouderlijkGezin.toString()
                + " is nu het ouderlijk gezin van " + p.getNaam());
        }
        
    }

    public void selectGezin(Event evt) {
        // todo opgave 3

    }

    private void showGezin(Gezin gezin) {
        // todo opgave 3

    }

    public void setHuwdatum(Event evt) {
        // todo opgave 3

    }

    public void setScheidingsdatum(Event evt) {
        // todo opgave 3

    }

    public void cancelPersoonInvoer(Event evt) {
        // todo opgave 3

    }

    public void okPersoonInvoer(Event evt) {
        if(tfVoornamen1.textProperty().isEmpty().getValue() ||
                tfAchternaam1.textProperty().isEmpty().getValue() ||
                tfGeslacht1.textProperty().isEmpty().getValue() ||
                tfGebDatum1.textProperty().isEmpty().getValue() ||
                tfGebPlaats1.textProperty().isEmpty().getValue()
                ) {
            return;
        }
        String vnamenstring = tfVoornamen1.textProperty().getValue();
        String[] vnamen = vnamenstring.split(" ");
        String tussenvoegsel = "";
        if(!(tfTussenvoegsel1.getText().equals(""))) { 
            tussenvoegsel = tfTussenvoegsel1.textProperty().getValue();        
        }
        Geslacht geslacht = null;
        if (tfGeslacht1.textProperty().getValue().toLowerCase().charAt(0) == 'm') {
            geslacht = Geslacht.MAN;
        }
        else if (tfGeslacht1.textProperty().getValue().toLowerCase().charAt(0) == 'v') {
            geslacht = Geslacht.VROUW;
        }
        else {
            return;
        }     
        String anaam = tfAchternaam1.textProperty().getValue();
        SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy");
        Calendar gebdat = Calendar.getInstance();
        try {
            gebdat.setTime(df.parse(tfGebDatum1.textProperty().getValue()));
        } catch (ParseException ex) {
            System.out.println("Date conversion failed.");
        }
        String gebPlaats = tfGebPlaats1.textProperty().getValue();
        
        controller.getAdministratie().addPersoon(geslacht, vnamen, anaam, tussenvoegsel, gebdat, gebPlaats, null);
        clearTabPersoonInvoer();
    }

    public void okGezinInvoer(Event evt) {
        Persoon ouder1 = (Persoon) cbOuder1Invoer.getSelectionModel().getSelectedItem();
        if (ouder1 == null) {
            showDialog("Warning", "eerste ouder is niet ingevoerd");
            return;
        }
        Persoon ouder2 = (Persoon) cbOuder2Invoer.getSelectionModel().getSelectedItem();
        Calendar huwdatum;
        try {
            huwdatum = StringUtilities.datum(tfHuwelijkInvoer.getText());
        } catch (IllegalArgumentException exc) {
            showDialog("Warning", "huwelijksdatum :" + exc.getMessage());
            return;
        }
        Gezin g;
        if (huwdatum != null) {
            g = getAdministratie().addHuwelijk(ouder1, ouder2, huwdatum);
            if (g == null) {
                showDialog("Warning", "Invoer huwelijk is niet geaccepteerd");
            } else {
                Calendar scheidingsdatum;
                try {
                    scheidingsdatum = StringUtilities.datum(tfScheidingInvoer.getText());
                    if(scheidingsdatum != null){
                        getAdministratie().setScheiding(g, scheidingsdatum);
                    }
                } catch (IllegalArgumentException exc) {
                    showDialog("Warning", "scheidingsdatum :" + exc.getMessage());
                }
            }
        } else {
            g = getAdministratie().addOngehuwdGezin(ouder1, ouder2);
            if (g == null) {
                showDialog("Warning", "Invoer ongehuwd gezin is niet geaccepteerd");
            }
        }

        clearTabGezinInvoer();
    }

    public void cancelGezinInvoer(Event evt) {
        clearTabGezinInvoer();
    }

    
    public void showStamboom(Event evt) {
        // todo opgave 3
        
    }

    public void createEmptyStamboom(Event evt) {
        this.clearAdministratie();
        clearTabs();
        initComboboxes();
    }

    
    public void openStamboom(Event evt) {
        // todo opgave 3
       
    }

    
    public void saveStamboom(Event evt) {
        // todo opgave 3
       
    }

    
    public void closeApplication(Event evt) {
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
        //todo opgave 3
    
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
        // todo opgave 3
       
    }

    private void showDialog(String type, String message) {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    private Stage getStage() {
        return (Stage) menuBar.getScene().getWindow();
    }

}
