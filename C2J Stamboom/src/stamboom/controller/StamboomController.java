/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import stamboom.domain.Administratie;
import stamboom.storage.*;

public class StamboomController {

    private Administratie admin;
    private IStorageMediator storageMediator;
    private IStorageMediator databaseMediator;

    /**
     * creatie van stamboomcontroller met lege administratie en onbekend
     * opslagmedium
     */
    public StamboomController() {
        admin = new Administratie();
        storageMediator = new SerializationMediator();
        databaseMediator = new DatabaseMediator();
    }

    public Administratie getAdministratie() {
        return admin;
    }

    /**
     * administratie wordt leeggemaakt (geen personen en geen gezinnen)
     */
    public void clearAdministratie() {
        admin = new Administratie();
    }

    /**
     * administratie wordt in geserialiseerd bestand opgeslagen
     *
     * @param bestand
     * @throws IOException
     */
    public boolean serialize(File bestand) throws IOException {
        //todo opgave 2 DONE
        Properties props = new Properties();
        try {
            props.setProperty("file", bestand.getPath());
        } catch (Exception e) {
            System.out.println("In serialize property ophalen ging fout");
        }
        
        storageMediator.configure(props);
        storageMediator.save(admin);
        return true;
    }

    /**
     * administratie wordt vanuit geserialiseerd bestand gevuld
     *
     * @param bestand
     * @throws IOException
     */
    public boolean deserialize(File bestand) throws IOException {
        //todo opgave 2 DONE
        Properties props = new Properties();
        try {
            props.setProperty("file", bestand.getPath());
        } catch (Exception e) {
            System.out.println("Iets ging fout met het laden van de properties bij deserialize");
        }
        storageMediator.configure(props);
        Administratie tempAdmin = storageMediator.load();
        if(tempAdmin == null)
        {
            return false;
        }
        else{
            admin = tempAdmin;
            return true;
        }
    }
    
    // opgave 4
    private void initDatabaseMedium() throws IOException {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream("database.properties")) {
                props.load(in);
            }
            databaseMediator.configure(props);        
    }
    
    /**
     * administratie wordt vanuit standaarddatabase opgehaald
     *
     * @throws IOException
     */
    public void loadFromDatabase() throws IOException {
        //todo opgave 4
        initDatabaseMedium();
        databaseMediator.load();
    }

    /**
     * administratie wordt in standaarddatabase bewaard
     *
     * @throws IOException
     */
    public void saveToDatabase() throws IOException {
        //todo opgave 4
        //initDatabaseMedium();
        databaseMediator.save(this.admin);
    }

}
