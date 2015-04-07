package stamboom.domain;

import java.util.*;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stamboom.util.StringUtilities;
import java.io.Serializable;

public class Gezin implements Serializable {

    // *********datavelden*************************************
    private final int nr;
    private final Persoon ouder1;
    private final Persoon ouder2;
    private final List<Persoon> kinderen;
    /**
     * kan onbekend zijn (dan is het een ongehuwd gezin):
     */
    private Calendar huwelijksdatum;
    /**
     * kan null zijn; als huwelijksdatum null is, dan zal scheidingsdatum ook
     * null zijn; Als huwelijksdatum en scheidingsdatum bekend zijn, dan zal de
     * scheidingsdatum na het huewelijk zijn.
     */
    private Calendar scheidingsdatum;

    // *********constructoren***********************************
    /**
     * er wordt een (kinderloos) gezin met ouder1 en ouder2 als ouders
     * geregistreerd; de huwelijks-(en scheidings)datum zijn onbekend (null);
     * het gezin krijgt gezinsNr als nummer;
     *
     * @param ouder1 mag niet null zijn, moet al geboren zijn, en mag geen
     * famillie van ouder2 zijn.
     * @param ouder2 ongelijk aan ouder1, moet al geboren zijn, en mag geen
     * familie van ouder1 zijn.
     */
    Gezin(int gezinsNr, Persoon ouder1, Persoon ouder2) {
        if (ouder1 == null) {
            throw new RuntimeException("Eerste ouder mag niet null zijn");
        }
        if (ouder1 == ouder2) {
            throw new RuntimeException("ouders hetzelfde");
        }
        if (ouder2 != null) {
            if (ouder1.getOuderlijkGezin() != null
                    && ouder1.getOuderlijkGezin().isFamilieVan(ouder2)) {
                throw new RuntimeException("ouder 2 is familie van ouder 1");
            }
            if (ouder2.getOuderlijkGezin() != null
                    && ouder2.getOuderlijkGezin().isFamilieVan(ouder1)) {
                throw new RuntimeException("ouder 1 is familie van ouder 2");
            }
        }
        if (ouder1.getGebDat().compareTo(Calendar.getInstance()) > 0) {
            throw new RuntimeException("ouder1 moet nog geboren worden");
        }
        if (ouder2 != null && ouder2.getGebDat().compareTo(Calendar.getInstance()) > 0) {
            throw new RuntimeException("ouder2 moet nog geboren worden");
        }

        this.nr = gezinsNr;
        this.ouder1 = ouder1;
        this.ouder2 = ouder2;
        this.kinderen = new ArrayList<>();
        this.huwelijksdatum = null;
        this.scheidingsdatum = null;
    }

    // ********methoden*****************************************
    /**
     * @return alle kinderen uit dit gezin
     */
    public ObservableList<Persoon> getKinderen() {
        ObservableList<Persoon> oListkinderen = FXCollections.observableArrayList(this.kinderen);
        return oListkinderen;
    }

    /**
     *
     * @return het aantal kinderen in dit gezin
     */
    public int aantalKinderen() {
        return kinderen.size();
    }

    /**
     *
     * @return het nummer van dit gezin
     */
    public int getNr() {
        return nr;
    }

    /**
     * @return de eerste ouder van dit gezin
     */
    public Persoon getOuder1() {
        return ouder1;
    }

    /**
     * @return de tweede ouder van dit gezin (kan null zijn)
     */
    public Persoon getOuder2() {
        return ouder2;
    }

    /**
     *
     * @return het nr, de naam van de eerste ouder, gevolgd door de naam van de
     * eventuele tweede ouder. Als dit gezin getrouwd is, wordt ook de
     * huwelijksdatum vermeld.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.nr).append(" ");
        s.append(ouder1.getNaam());
        if (ouder2 != null) {
            s.append(" met ");
            s.append(ouder2.getNaam());
        }
        if (heeftGetrouwdeOudersOp(Calendar.getInstance())) {
            s.append(" ").append(StringUtilities.datumString(huwelijksdatum));
        }
        return s.toString();
    }

    /**
     * @return de datum van het huwelijk (kan null zijn)
     */
    public Calendar getHuwelijksdatum() {
        return huwelijksdatum;
    }

    /**
     * @return de datum van scheiding (kan null zijn)
     */
    public Calendar getScheidingsdatum() {
        if (this.scheidingsdatum != null)
        {
                return scheidingsdatum;
        }
        return null;
    }

    /**
     * Als ouders zijn gehuwd, en er nog geen scheidingsdatum is dan wordt deze
     * geregistreerd.
     *
     * @param datum moet na de huwelijksdatum zijn.
     * @return true als scheiding kan worden voltrokken, anders false
     */
    public boolean setScheiding(Calendar datum) {
        if (this.scheidingsdatum == null && huwelijksdatum != null
                && datum.after(huwelijksdatum) && datum != null) {
            this.scheidingsdatum = datum;
            return true;
        } else {
            return false;
        }
    }

    /**
     * registreert het huwelijk, mits dit gezin nog geen huwelijk is en beide
     * ouders op deze datum mogen trouwen (pas op: het is mogelijk dat er al wel
     * een huwelijk staat gepland, maar nog niet is voltrokken op deze datum)
     * Mensen mogen niet trouwen voor hun achttiende.
     *
     * @param datum de huwelijksdatum
     * @return false als huwelijk niet mocht worden voltrokken, anders true
     */
    public boolean setHuwelijk(Calendar datum) {

        if (this.huwelijksdatum == null) {

            int res = datum.get(Calendar.YEAR) - this.ouder1.getGebDat().get(Calendar.YEAR);
            if ((this.ouder1.getGebDat().get(Calendar.MONTH) > datum.get(Calendar.MONTH))
                    || (this.ouder1.getGebDat().get(Calendar.MONTH) == datum.get(Calendar.MONTH) && this.ouder1.getGebDat().get(Calendar.DAY_OF_MONTH) > datum
                    .get(Calendar.DAY_OF_MONTH))) {
                res--;
            }

            if (res < 18) {
                return false;
            }

            res = datum.get(Calendar.YEAR) - this.ouder2.getGebDat().get(Calendar.YEAR);
            if ((this.ouder2.getGebDat().get(Calendar.MONTH) > datum.get(Calendar.MONTH))
                    || (this.ouder2.getGebDat().get(Calendar.MONTH) == datum.get(Calendar.MONTH) && this.ouder2.getGebDat().get(Calendar.DAY_OF_MONTH) > datum
                    .get(Calendar.DAY_OF_MONTH))) {
                res--;
            }
            if (res < 18) {
                return false;
            }

            this.huwelijksdatum = datum;
            return true;
        }
        return false;
    }

    /**
     * @return het gezinsnummer, gevolgd door de namen van de ouder(s), de
     * eventueel bekende huwelijksdatum, (als er kinderen zijn) de constante
     * tekst '; kinderen:', en de voornamen van de kinderen uit deze relatie
     * (per kind voorafgegaan door ' -')
     */
    public String beschrijving() {
        String beschrijving = "";
        beschrijving += Integer.toString(nr) + " ";
        
        beschrijving += this.ouder1.getNaam();
        beschrijving += " met " + this.ouder2.getNaam();
        
        if(this.huwelijksdatum != null) {
            beschrijving += " " + StringUtilities.datumString(huwelijksdatum);
        }
        
        if(!this.kinderen.isEmpty()) {
            beschrijving += "; kinderen:";
            
            for(Persoon kind : this.kinderen) {
                beschrijving += " -" + kind.getVoornamen();
            }
        }
        
        return beschrijving;
    }

    /**
     * Voegt kind toe aan dit gezin. Doet niets als dit kind al deel uitmaakt
     * van deze familie.
     *
     * @param kind
     */
    void breidUitMet(Persoon kind) {
        if (!kinderen.contains(kind) && !this.isFamilieVan(kind)) {
            kinderen.add(kind);
        }
    }

    /**
     * Controleert of deze familie niet al de gegeven persoon bevat.
     *
     * @param input
     * @return true als deze familie de gegeven persoon bevat.
     */
    boolean isFamilieVan(Persoon input) {
        if (this.ouder1.getNr() == input.getNr()
                || (this.ouder2 != null && this.ouder2.getNr() == input.getNr())
                || kinderen.contains(input)) {
            return true;
        }

        boolean output = this.ouder1.getOuderlijkGezin() != null
                && this.ouder1.getOuderlijkGezin().isFamilieVan(input);
        if (!output && this.ouder2 != null) {
            output = this.ouder2.getOuderlijkGezin() != null
                    && this.ouder2.getOuderlijkGezin().isFamilieVan(input);
        }
        return output;
    }

    /**
     *
     * @param datum
     * @return true als dit gezin op datum getrouwd en nog niet gescheiden is,
     * anders false
     */
    public boolean heeftGetrouwdeOudersOp(Calendar datum) {
        return isHuwelijkOp(datum)
                && (scheidingsdatum == null || scheidingsdatum.after(datum));
    }

    /**
     *
     * @param datum
     * @return true als dit gezin op of voor deze datum getrouwd is, ongeacht of
     * de ouders hierna gingen/gaan scheiden.
     */
    public boolean isHuwelijkOp(Calendar datum) {
        if (!(this.getHuwelijksdatum() != null))
        {
            return false;
        }
        else if (this.getHuwelijksdatum().before(datum) || this.getHuwelijksdatum() == datum)
        {
            return true;
        }
        return false;
    }

    /**
     *
     * @return true als de ouders van dit gezin niet getrouwd zijn, anders false
     */
    public boolean isOngehuwd() {
        return huwelijksdatum == null || scheidingsdatum != null;
    }

    /**
     *
     * @param datum
     * @return true als dit een gescheiden huwelijk is op datum, anders false
     */
    public boolean heeftGescheidenOudersOp(Calendar datum) {
        if (this.scheidingsdatum == null) {
            return false;
        }
        
        if (this.scheidingsdatum.before(datum))
        {
            return true;
        }
        return false;
    }
}
