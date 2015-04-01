/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import stamboom.domain.Administratie;

public class SerializationMediator implements IStorageMediator {

    /**
     * bevat de bestandslocatie. Properties is een subclasse van HashTable, een
     * alternatief voor een List. Het verschil is dat een List een volgorde
     * heeft, en een HashTable een key/value index die wordt opgevraagd niet op
     * basis van positie, maar op key.
     */
    private Properties props;

    /**
     * creation of a non configured serialization mediator
     */
    public SerializationMediator() {
        props = null;
    }

    @Override
    public Administratie load() throws IOException {
//        if (!isCorrectlyConfigured()) {
//            throw new RuntimeException("Serialization mediator isn't initialized correctly.");
//        
        System.out.println("Now Loading");
        

        //todo opgave 2 
        Administratie admin = null;
        int i = 1;
        try {
            //String path = props.getProperty("file");
            String path = System.getProperty("user.dir") + "\\Players\\Admin.ser";
            System.out.println(path);
            File myFile = new File(path);
            System.out.println(myFile.exists());
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            admin = (Administratie) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Player class not found");
            c.printStackTrace();
            return null;
        }
        System.out.println("Deserialized Employee...");
        System.out.println("aantal gezinnen: " + admin.aantalGeregistreerdeGezinnen());
        return admin;
    }

    @Override
    public void save(Administratie admin) throws IOException {
//        if (!isCorrectlyConfigured()) {
//            throw new RuntimeException("Serialization mediator isn't initialized correctly.");
//        }
        
        //todo opgave 2 
        try {
            //String path = props.getProperty("file");
            String path = System.getProperty("user.dir") + "\\Players";
            System.out.println(path);
            File myFile = new File(path);
            System.out.println(myFile.exists());
            if (!myFile.exists()) {
                myFile.mkdirs();
                myFile.createNewFile();
                System.out.println(myFile.exists());
            }
            FileOutputStream fileOut = new FileOutputStream(path + "\\Admin.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(admin);
            out.close();
            fileOut.close();
            System.out.println("Data was saved to " + path + "\\Admin.ser");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something went the f wrong");
        }
    }

    /**
     * Laadt de instellingen, in de vorm van een Properties bestand, en
     * controleert of deze in de juiste vorm is.
     *
     * @param props
     * @return
     */
    @Override
    public boolean configure(Properties props) {
        this.props = props;
        return isCorrectlyConfigured();
    }

    @Override
    public Properties config() {
        return props;
    }

    /**
     * Controleert of er een geldig Key/Value paar bestaat in de Properties. De
     * bedoeling is dat er een Key "file" is, en de Value van die Key een String
     * representatie van een FilePath is (eg. C:\\Users\Username\test.txt).
     *
     * @return true if config() contains at least a key "file" and the
     * corresponding value is formatted like a file path
     */
    @Override
    public boolean isCorrectlyConfigured() {
        
        if (props == null) {
            return false;
        }
        return props.containsKey("file")
                && props.getProperty("file").contains(File.separator);
    }
}
