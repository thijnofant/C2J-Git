/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import com.mysql.jdbc.PreparedStatement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import stamboom.domain.Administratie;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;

public class DatabaseMediator implements IStorageMediator {

    private Properties props;
    private Connection conn;

    
    @Override
    public Administratie load() throws IOException {
        Administratie admin = new Administratie();
        String url = (String) props.get("url");
        String username = (String) props.get("username");
        String password = (String) props.get("password");
        Statement stat;
        String query;
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            System.out.println("1 " + ex.getMessage());
        }
        if (conn != null) {
            ResultSet rs;
            try {
                stat = conn.createStatement();
                query = "SELECT persoonNummer, achternaam, voornaam, tussenvoegsel, geboortedatum, geboorteplaats, geslacht FROM PERSONEN ORDER BY persoonsnummer";
                rs = stat.executeQuery(query);
                String achternaam;
                String[] voornamen;
                String tussenvoegsel;
                Calendar geboortedatum;
                String geboorteplaats;
                Geslacht geslacht;
                while(rs.next())
                {
                    achternaam = rs.getString("achternaam");
                    voornamen = rs.getString("voornaam").split(" ");
                    tussenvoegsel = rs.getString("tussenvoegsel");
                    geboortedatum = new GregorianCalendar();
                    geboortedatum.setTime(rs.getDate("geboortedatum"));
                    geboorteplaats = rs.getString("geboorteplaats");
                    if(rs.getString("geslacht").equals("M"))
                    {
                        geslacht = Geslacht.MAN;
                    }
                    else
                    {
                        geslacht = Geslacht.VROUW;
                    }
                    admin.addPersoon(geslacht, voornamen, achternaam, tussenvoegsel, geboortedatum, geboorteplaats, null);
                }
                
            } catch (SQLException ex) {
                System.out.println("2" + ex.getMessage());
            }
            
            try
            {
                stat = conn.createStatement();
                query = "SELECT gezinsnummer, ouder1, ouder2, huwelijksdatum, scheidingsdatum FROM gezinnen ORDER BY gezinsnummer";
                rs = stat.executeQuery(query);
                Persoon ouder1;
                Persoon ouder2;
                Calendar huwelijksdatum;
                Calendar scheidingsdatum;
                while(rs.next())
                {
                    ouder1 = admin.getPersoon(rs.getInt("ouder1"));
                    ouder2 = admin.getPersoon(rs.getInt("ouder2"));
                    huwelijksdatum = new GregorianCalendar();
                    if(rs.getDate("huwelijksdatum") != null)
                    {
                    huwelijksdatum.setTime(rs.getDate("huwelijksdatum"));
                    }
                    else
                    {
                        huwelijksdatum = null;
                    }
                    scheidingsdatum = new GregorianCalendar();
                    if(rs.getDate("scheidingsdatum") != null)
                    {
                        scheidingsdatum.setTime(rs.getDate("scheidingsdatum"));
                    }
                    else
                    {
                        scheidingsdatum = null;
                    }
                    
                    
                    if(huwelijksdatum != null && scheidingsdatum == null)
                    {
                        admin.addHuwelijk(ouder1, ouder2, huwelijksdatum);
                    }
                    else if(huwelijksdatum != null && scheidingsdatum != null)
                    {
                        Gezin g = admin.addHuwelijk(ouder1, ouder2, huwelijksdatum);
                        admin.setScheiding(g, scheidingsdatum);
                        
                    }
                    else if(huwelijksdatum == null)
                    {
                        admin.addOngehuwdGezin(ouder1, ouder2);
                    }
                }
            }
            catch(SQLException ex)
            {
                System.out.println("3" + ex.getMessage());
            }
            
            try
            {
                stat = conn.createStatement();
                query = "SELECT persoonsnummer, ouders FROM PERSONEN ORDER BY persoonsnummer";
                rs = stat.executeQuery(query);
                while(rs.next())
                {
                    if(rs.getInt("ouders") != 0)
                    {
                        Persoon persoon = admin.getPersoon(rs.getInt("persoonsnummer"));
                        Gezin gezin = admin.getGezin(rs.getInt("ouders"));
                        admin.setOuders(persoon, gezin);
                    }
                }
            }
            catch(SQLException ex)
            {
                System.out.println("4" + ex.getMessage());
            }
        } else {
            System.out.println("Geen verbinding");
        }
        return admin;
    }

    @Override
    public void save(Administratie admin) throws IOException {
        //todo opgave 4  
        String url = (String) props.get("url");
        String username = (String) props.get("username");
        String password = (String) props.get("password");
        Statement stat;
        String query;
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            System.out.println("1 " + ex.getMessage());
        }
        if (conn != null) {
            try {
                stat = conn.createStatement();
            } catch (SQLException exc) {
                System.out.println("2 " + exc.getMessage());
                return;
            }

            try {
                query = "UPDATE PERSONEN SET ouders = NULL";
                stat.executeUpdate(query);
            } catch (SQLException ex) {
                System.out.println("3 " + ex.getMessage());
            }

            //gezinnen verwijderen
            try {
                query = "DELETE FROM GEZINNEN";
                stat.executeUpdate(query);
            } catch (SQLException ex) {
                System.out.println("4 " + ex.getMessage());
            }

            //personen verwijderen
            try {
                query = "DELETE FROM PERSONEN";
                stat.executeUpdate(query);
            } catch (SQLException ex) {
                System.out.println("5 " + ex.getMessage());
            }

            for (Persoon persoon : admin.getPersonen()) {
                String geslacht;
                if (persoon.getGeslacht().equals(Geslacht.MAN)) {
                    geslacht = "M";
                } else {
                    geslacht = "V";
                }
                query = "INSERT INTO PERSONEN VALUES(?,?,?,?,?,?,?,?)";
                try {
                    PreparedStatement ps = (PreparedStatement) conn.prepareStatement(query);
                    ps.setInt(1, persoon.getNr());
                    ps.setString(2, persoon.getAchternaam());
                    ps.setString(3, persoon.getVoornamen());
                    ps.setString(4, persoon.getTussenvoegsel());
                    ps.setDate(5, new java.sql.Date(persoon.getGebDat().getTimeInMillis()));
                    ps.setString(6, persoon.getGebPlaats());
                    ps.setString(7, geslacht);
                    ps.setNull(8, java.sql.Types.NULL);
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    System.out.println("6" + ex.getMessage());
                }
            }

            for (Gezin gezin : admin.getGezinnen()) {
                Persoon ouder2 = gezin.getOuder2();
                Calendar huwdatum = gezin.getHuwelijksdatum();
                Calendar scheidingsdatum = gezin.getScheidingsdatum();

                query = "INSERT INTO GEZINNEN VALUES(?,?,?,?,?)";
                try {
                    PreparedStatement ps = (PreparedStatement) conn.prepareStatement(query);
                    ps.setInt(1, gezin.getNr());
                    ps.setInt(2, gezin.getOuder1().getNr());
                    if (ouder2 == null) {
                        ps.setNull(3, java.sql.Types.NULL);
                    } else {
                        ps.setInt(3, gezin.getOuder2().getNr());
                    }
                    if (huwdatum == null) {
                        ps.setNull(4, java.sql.Types.NULL);
                    } else {
                        ps.setDate(4, new java.sql.Date(huwdatum.getTimeInMillis()));
                    }
                    if (scheidingsdatum == null) {
                        ps.setNull(5, java.sql.Types.NULL);
                    } else {
                        ps.setDate(5, new java.sql.Date(scheidingsdatum.getTimeInMillis()));
                    }
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    System.out.println("7" + ex.getMessage());
                }

            }
            for (Persoon persoon : admin.getPersonen()) {
                query = "UPDATE PERSONEN SET ouders = ? WHERE persoonsNummer = ?";
                try {
                    if (persoon.getOuderlijkGezin() != null) {
                        PreparedStatement ps = (PreparedStatement) conn.prepareStatement(query);
                        ps.setInt(1, persoon.getOuderlijkGezin().getNr());
                        ps.setInt(2, persoon.getNr());
                        ps.executeUpdate();
                    }
                } catch (SQLException ex) {
                    System.out.println("8" + ex.getMessage());
                }
            }
        } else {
            System.out.println("Geen connectie");
        }
    }

    @Override
    public final boolean configure(Properties props) {
        this.props = props;

        try {
            initConnection();
            return isCorrectlyConfigured();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            this.props = null;
            return false;
        } finally {
            closeConnection();
        }
    }

    @Override
    public Properties config() {
        return props;
    }

    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        if (!props.containsKey("driver")) {
            return false;
        }
        if (!props.containsKey("url")) {
            return false;
        }
        if (!props.containsKey("username")) {
            return false;
        }
        if (!props.containsKey("password")) {
            return false;
        }
        return true;
    }

    private void initConnection() throws SQLException {
        //opgave 4
        String driver = "oracle.jdbc.OracleDriver";
        String url = "jdbc:oracle:thin:@fhictora01.fhict.local:1521:fhictora";
        String username = "dbi298630";
        String password = "MjUuCmAc2H";
        props.setProperty("driver", driver);
        props.setProperty("url", url);
        props.setProperty("username", username);
        props.setProperty("password", password);

        conn = DriverManager.getConnection(url, username, password);
    
    }

    private void closeConnection() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}