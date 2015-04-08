package stamboom.domain;

import java.util.*;
import java.io.Serializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Administratie implements Serializable {

    //************************datavelden*************************************
    private int nextGezinsNr;
    private int nextPersNr;
    private List<Persoon> personen;
    private List<Gezin> gezinnen;
    private transient ObservableList<Persoon> obsPersonen;
    private transient ObservableList<Gezin> obsGezinnen;

    //***********************constructoren***********************************
    /**
     * er wordt een lege administratie aangemaakt.
     * personen en gezinnen die in de toekomst zullen worden gecreeerd, worden
     * (apart) opvolgend genummerd vanaf 1
     */
    public Administratie() {
        this.nextGezinsNr = 1;
        this.nextPersNr = 1;
        personen = new ArrayList<>();
        gezinnen = new ArrayList<>();
        obsPersonen = FXCollections.observableList(personen);
        obsGezinnen = FXCollections.observableList(gezinnen);
    }
    
    //**********************methoden****************************************
    /**
     * er wordt een persoon met de gegeven parameters aangemaakt; de persoon
     * krijgt een uniek nummer toegewezen, en de persoon is voortaan ook bij het
     * (eventuele) ouderlijk gezin bekend. Voor de voornamen, achternaam en
     * gebplaats geldt dat de eerste letter naar een hoofdletter en de resterende
     * letters naar kleine letters zijn geconverteerd; het tussenvoegsel is in
     * zijn geheel geconverteerd naar kleine letters; overbodige spaties zijn
     * verwijderd
     *
     * @param geslacht
     * @param vnamen vnamen.length>0; alle strings zijn niet leeg
     * @param anaam niet leeg
     * @param tvoegsel mag leeg zijn
     * @param gebdat
     * @param gebplaats niet leeg
     * @param ouderlijkGezin mag de waarde null (=onbekend) hebben
     *
     * @return de nieuwe persoon.
     * Als de persoon al bekend was (op basis van combinatie van getNaam(),
     * geboorteplaats en geboortedatum), wordt er null geretourneerd.
     */
    public Persoon addPersoon(Geslacht geslacht, String[] vnamen, String anaam,
            String tvoegsel, Calendar gebdat,
            String gebplaats, Gezin ouderlijkGezin) {

        if (vnamen.length == 0) {
            throw new IllegalArgumentException("ten minste 1 voornaam");
        }
        for (String voornaam : vnamen) {
            if (voornaam.trim().isEmpty()) {
                throw new IllegalArgumentException("lege voornaam is niet toegestaan");
            }
        }

        if (anaam.trim().isEmpty()) {
            throw new IllegalArgumentException("lege achternaam is niet toegestaan");
        }

        if (gebplaats.trim().isEmpty()) {
            throw new IllegalArgumentException("lege geboorteplaats is niet toegestaan");
        }

       
        Persoon newPersoon = new Persoon(this.nextPersNr, vnamen, anaam, tvoegsel, gebdat, gebplaats, geslacht, ouderlijkGezin);
        
        for (Persoon persoon : this.personen)
        {
            if (persoon.getNaam() == newPersoon.getNaam() && persoon.getGebPlaats() == newPersoon.getGebPlaats() && persoon.getGebDat() == newPersoon.getGebDat() )
            {
                return null;
            }
        }
        if (ouderlijkGezin != null) {
            ouderlijkGezin.breidUitMet(newPersoon);
        }

        
        this.obsPersonen.add(newPersoon);
        this.nextPersNr += 1;
        
        return newPersoon;
    }

    /**
     * er wordt, zo mogelijk (zie return) een (kinderloos) ongehuwd gezin met
     * ouder1 en ouder2 als ouders gecreeerd; de huwelijks- en scheidingsdatum
     * zijn onbekend (null); het gezin krijgt een uniek nummer toegewezen; dit
     * gezin wordt ook bij de afzonderlijke ouders geregistreerd;
     *
     * @param ouder1
     * @param ouder2 mag null zijn
     *
     * @return het nieuwe gezin. null als ouder1 = ouder2 of als een van de volgende
     * voorwaarden wordt overtreden:
     * 1) een van de ouders is op dit moment getrouwd
     * 2) het koppel vormt al een ander gezin
     */
    public Gezin addOngehuwdGezin(Persoon ouder1, Persoon ouder2) {

        try {
            Persoon ouder = ouder2;
            if (ouder1 == ouder) {
                return null;
            }
            if (ouder.getGebDat().after(Calendar.getInstance()) ) {
            return null;
            }
            for (Gezin gez : gezinnen) {
                if (ouder1.beschrijving().equals(gez.getOuder1().beschrijving())
                        || ouder1.beschrijving().equals(gez.getOuder2().beschrijving())
                        || ouder.beschrijving().equals(gez.getOuder1().beschrijving())
                        || ouder.beschrijving().equals(gez.getOuder2().beschrijving())) {
                    return null;
                }
            }
        } catch (Exception e) {
        }
        if (ouder1.getGebDat().after(Calendar.getInstance())) {
            return null;
        }
        if (ouder1.getGebDat().compareTo(Calendar.getInstance()) > 0) {
            return null;
        }
        if (ouder2 != null && ouder2.getGebDat().compareTo(Calendar.getInstance()) > 0) {
            return null;
        }

        Calendar nu = Calendar.getInstance();
        if (ouder1.isGetrouwdOp(nu) || (ouder2 != null
                && ouder2.isGetrouwdOp(nu))
                || ongehuwdGezinBestaat(ouder1, ouder2)) {
            return null;
        }

        Gezin gezin = new Gezin(nextGezinsNr, ouder1, ouder2);
        nextGezinsNr++;
        obsGezinnen.add(gezin);

        ouder1.wordtOuderIn(gezin);
        if (ouder2 != null) {
            ouder2.wordtOuderIn(gezin);
        }
        
        return gezin;
    }

    /**
     * Als het ouderlijk gezin van persoon nog onbekend is dan wordt
     * persoon een kind van ouderlijkGezin, en tevens wordt persoon als kind
     * in dat gezin geregistreerd. Als de ouders bij aanroep al bekend zijn,
     * verandert er niets
     *
     * @param persoon
     * @param ouderlijkGezin
     * @return of ouderlijk gezin kon worden toegevoegd.
     */
    public boolean setOuders(Persoon persoon, Gezin ouderlijkGezin) {
        return persoon.setOuders(ouderlijkGezin);
    }

    /**
     * als de ouders van dit gezin gehuwd zijn en nog niet gescheiden en datum
     * na de huwelijksdatum ligt, wordt dit de scheidingsdatum. Anders gebeurt
     * er niets.
     *
     * @param gezin
     * @param datum
     * @return true als scheiding geaccepteerd, anders false
     */
    public boolean setScheiding(Gezin gezin, Calendar datum) {
        return gezin.setScheiding(datum);
    }

    /**
     * registreert het huwelijk, mits gezin nog geen huwelijk is en beide
     * ouders op deze datum mogen trouwen (pas op: het is niet toegestaan dat een
     * ouder met een toekomstige (andere) trouwdatum trouwt.)
     *
     * @param gezin
     * @param datum de huwelijksdatum
     * @return false als huwelijk niet mocht worden voltrokken, anders true
     */
    public boolean setHuwelijk(Gezin gezin, Calendar datum) {
        return gezin.setHuwelijk(datum);
    }

    /**
     *
     * @param ouder1
     * @param ouder2
     * @return true als dit koppel (ouder1,ouder2) al een ongehuwd gezin vormt
     */
    boolean ongehuwdGezinBestaat(Persoon ouder1, Persoon ouder2) {
        return ouder1.heeftOngehuwdGezinMet(ouder2) != null;
    }

    /**
     * als er al een ongehuwd gezin voor dit koppel bestaat, wordt het huwelijk
     * voltrokken, anders wordt er zo mogelijk (zie return) een (kinderloos)
     * gehuwd gezin met ouder1 en ouder2 als ouders gecreeerd; de
     * scheidingsdatum is onbekend (null); het gezin krijgt een uniek nummer
     * toegewezen; dit gezin wordt ook bij de afzonderlijke ouders
     * geregistreerd;
     *
     * @param ouder1
     * @param ouder2
     * @param huwdatum
     * @return null als ouder1 = ouder2 of als een van de ouders getrouwd is
     * anders het gehuwde gezin
     */
    public Gezin addHuwelijk(Persoon ouder1, Persoon ouder2, Calendar huwdatum) {
        //todo opgave 1 DONE
        Gezin reGezin = null;
        if (ouder1.equals(ouder2)){ return null; }
        for (int i = 0; i < gezinnen.size(); i++)
        {
            Gezin get = gezinnen.get(i);
            if(get.getOuder1().beschrijving().equals(ouder1.beschrijving()) && get.getOuder2().beschrijving().equals(ouder2.beschrijving()) || get.getOuder2().beschrijving().equals(ouder1.beschrijving()) && get.getOuder1().beschrijving().equals(ouder2.beschrijving())
                    && !(get.isHuwelijkOp(huwdatum)))
            {
                reGezin = get;
            }
            else if ((!get.isOngehuwd())
                    && (get.getOuder1().equals(ouder1) || get.getOuder1().equals(ouder2) || get.getOuder2().equals(ouder1) || get.getOuder2().equals(ouder2)))
            {
                return null;
            }
            else if (get.getScheidingsdatum() != null && get.getScheidingsdatum().after(huwdatum)) {
                return null;
            }
        }

        if (!(reGezin == null))
        {
            reGezin.setHuwelijk(huwdatum);
            return reGezin;
        }
        else
        {
            reGezin = new Gezin(this.nextGezinsNr, ouder1, ouder2);
            this.nextGezinsNr += 1;
            reGezin.setHuwelijk(huwdatum);
            this.gezinnen.add(reGezin);
            this.obsGezinnen.add(reGezin);
            if(ouder1 != null){
                ouder1.wordtOuderIn(reGezin);
            }
            if(ouder2 != null){
                ouder2.wordtOuderIn(reGezin);
            }
            return reGezin;
        }
    }

    /**
     *
     * @return het aantal geregistreerde personen
     */
    public int aantalGeregistreerdePersonen() {
        return nextPersNr - 1;
    }

    /**
     *
     * @return het aantal geregistreerde gezinnen
     */
    public int aantalGeregistreerdeGezinnen() {
        return nextGezinsNr - 1;
    }

    /**
     *
     * @param nr
     * @return de persoon met nummer nr, als die niet bekend is wordt er null
     * geretourneerd
     */
    public Persoon getPersoon(int nr) {
        for (Persoon persoon : this.personen)
        {
            if (persoon.getNr() == nr)
            {
                return persoon;
            }
        }
        return null;
    }

    /**
     * @param achternaam
     * @return alle personen met een achternaam gelijk aan de meegegeven
     * achternaam (ongeacht hoofd- en kleine letters)
     */
    public ArrayList<Persoon> getPersonenMetAchternaam(String achternaam) {
        //todo opgave 1 DONE
        ArrayList reList = new ArrayList();
        for (int i = 0; i < personen.size(); i++)
        {
            Persoon get = personen.get(i);
            if (get.getAchternaam().toLowerCase().equals(achternaam.toLowerCase()))
            {
                reList.add(get);
            }
        }
        return reList;
    }

    /**
     *
     * @return de geregistreerde personen
     */
    public ObservableList<Persoon> getPersonen() {
         return FXCollections.unmodifiableObservableList(obsPersonen);
    }

    /**
     *
     * @param vnamen
     * @param anaam
     * @param tvoegsel
     * @param gebdat
     * @param gebplaats
     * @return de persoon met dezelfde initialen, tussenvoegsel, achternaam,
     * geboortedatum en -plaats mits bekend (ongeacht hoofd- en kleine letters),
     * anders null
     */
    public Persoon getPersoon(String[] vnamen, String anaam, String tvoegsel,
            Calendar gebdat, String gebplaats) {
        //todo opgave 1 DONE
        for(Persoon p : this.personen) {
            if(!(gebplaats.toLowerCase().equals(p.getGebPlaats().toLowerCase()))) {
                return null;
            }
            
            if(!(gebdat.equals(p.getGebDat()))) {
                return null;
            }
            
            if(!(tvoegsel.toLowerCase().equals(p.getTussenvoegsel().toLowerCase()))) {
                return null;
            }
            
            if(!(anaam.toLowerCase().equals(p.getAchternaam().toLowerCase()))) {
                return null;
            }
            
            StringBuilder sb = new StringBuilder();
            for (String s : vnamen) {
                sb.append(s.substring(0,1).toUpperCase()).append('.');
            }
            
            String persIni = p.getInitialen().toLowerCase();
            
            if(sb.toString().trim().toLowerCase().equals(persIni)) {
                return p;
            }
        }
        
        return null;
    }

    /**
     *
     * @return de geregistreerde gezinnen
     */
    public ObservableList<Gezin> getGezinnen() {
       return FXCollections.unmodifiableObservableList(obsGezinnen);
    }

    /**
     *
     * @param gezinsNr
     * @return het gezin met nummer nr. Als dat niet bekend is wordt er null
     * geretourneerd
     */
    public Gezin getGezin(int gezinsNr) {
        // aanname: er worden geen gezinnen verwijderd
        if (gezinnen != null && 1 <= gezinsNr && 1 <= gezinnen.size()) {
            return gezinnen.get(gezinsNr - 1);
        }
        return null;
    }
}
