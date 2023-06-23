package bankomat;

import customExceptions.KarticaPreviseCrtica;
import customExceptions.NePostojiUListiException;
import customExceptions.NemaSamoBrojeve;
import customExceptions.NemaTackeException;
import customExceptions.NeodgovarajucBrojMajmunskogA;
import customExceptions.NevalidanJMBGException;
import customExceptions.NevalidanStartException;
import customExceptions.NevalidnoImePrezimeException;
import customExceptions.PreviseBrojeva;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public abstract class Osoba {
    protected String ime, prezime, jmbg, brojKartice, pin;
    protected int id;
    protected boolean adminPrivilegije;

    public Osoba(String ime, String prezime, String jmbg, String brojKartice, String pin, int id, boolean admin) {
        this.ime = ime;
        this.prezime = prezime;
        this.jmbg = jmbg;
        this.brojKartice = brojKartice;
        this.pin = pin;
        this.id = id;
        this.adminPrivilegije = admin;
    }
    
    public Osoba() {
        this.ime = "";
        this.prezime = "";
        this.jmbg = "";
        this.brojKartice = "";
        this.pin = "";
        this.id = 0;
        this.adminPrivilegije = false;
    }
    
    public static void upisiKorisnikeUFajl(ArrayList<Osoba> l, String putanja){ 
    JSONArray jsonOsobe=new JSONArray();
        PrintWriter pw = null;
        for (Osoba osoba : l) {
            JSONObject obj = new JSONObject();
            obj.put("ime", osoba.ime);
            obj.put("prezime", osoba.prezime);
            obj.put("jmbg", osoba.jmbg);
            obj.put("brojKartice", osoba.brojKartice);
            obj.put("pin", osoba.pin);
            obj.put("id", osoba.id);
            obj.put("adminOnline", String.valueOf(osoba.adminPrivilegije));
            if(osoba.adminPrivilegije)
                obj.put("email", ((Admin)osoba).getEmail());
            else
                obj.put("sumaNovca", ((Korisnik)osoba).getSumaNovca());
            jsonOsobe.add(obj);
            }
            try {
                pw=new PrintWriter(putanja);
                pw.write(jsonOsobe.toJSONString());
            } catch (FileNotFoundException ex) {
                System.out.println("Greska prilikom upisa!\n"+ex.getMessage());
            }
            finally{
                if(pw != null)
                pw.close();
            }
    }
    
     public static ArrayList<Osoba> procitajOsobe(String putanja) throws Exception{
        FileReader fr = new FileReader(putanja);
        ArrayList<Osoba> procitane = new ArrayList<>();
        JSONArray jsonOsobe = (JSONArray) new JSONParser().parse(fr);
        
        for(Object o : jsonOsobe)
        {
            JSONObject jsonOsoba =(JSONObject) o;
            String ime = jsonOsoba.get("ime").toString();
            String prezime = jsonOsoba.get("prezime").toString();
            String jmbg = jsonOsoba.get("jmbg").toString();
            String brojKartice = jsonOsoba.get("brojKartice").toString();
            String pin = jsonOsoba.get("pin").toString();
            int id = Integer.parseInt(jsonOsoba.get("id").toString());
            String adminPrivilegijaString = jsonOsoba.get("adminOnline").toString();
            boolean adminPrivilegija = false;
            if(adminPrivilegijaString.equals("true"))
                adminPrivilegija = true;
            else 
                adminPrivilegija = false;
            if(adminPrivilegija) {
                String email = jsonOsoba.get("email").toString(); 
                Admin admin = new Admin(ime, prezime, jmbg, brojKartice, pin, id, adminPrivilegija, email);
                procitane.add(admin);
            }
            else{
                float sumaNovca = Float.parseFloat(jsonOsoba.get("sumaNovca").toString());
                Korisnik korisnik = new Korisnik(ime, prezime, jmbg, brojKartice, pin, id, adminPrivilegija, sumaNovca);
                procitane.add(korisnik);
            }
        }
        return procitane;
    } 
     
    public static int validirajKarticuPin(String kartica, String pin, ArrayList<Osoba> korisnici){
        String[] podeljenoPoCrtici = kartica.split("-");
        Scanner unos = new Scanner(System.in);
        int brojac = 0;
        try{
            //Ispitivanje validnosti PIN-a
            for (int i = 0; i < pin.length(); i++) {
                if(!Character.isDigit(pin.charAt(i))) throw new NemaSamoBrojeve("Greska! PIN kartice nije odgovarajuc! (PIN sme da sadrzi samo brojeve!)");
            }
            if(pin.length() != 4) throw new PreviseBrojeva("Greska! PIN nije odgovarajuc! (PIN sme sadrzati samo 4 broja!)");
            
            
            //Ispitivanje validnosti kartice
            for (int i = 0; i < podeljenoPoCrtici.length; i++) {
                for (int j = 0; j < podeljenoPoCrtici[i].length(); j++) {
                    if(!Character.isDigit(podeljenoPoCrtici[i].charAt(j))) throw new NemaSamoBrojeve("Greska! Broj kartice nije odgovarajuc! (Broj kartice sme da sadrzi samo brojeve!)");
                    if(podeljenoPoCrtici[i].length() != 4) throw new PreviseBrojeva("Greska! Broj kartice nije odgovarajuc! (Svako polje treba sadrzati 4 cifre!)"); 
                }
            }
            if(podeljenoPoCrtici.length != 4) throw new KarticaPreviseCrtica("Greska! Broj kartice nije odgovarajuc! (Kartica treba sadrzati 4 polja od po 4 cifre odvojene znakom " + "\'" + "-" + "\'" + " izmedju!)"); 
           
            
            //Provera korisnika
            for (int i = 0; i < korisnici.size(); i++) {
                if(!korisnici.get(i).brojKartice.equals(kartica)) brojac++;
                else{
                    if(korisnici.get(i).pin.equals(pin)) {
                        System.out.println("***USPESNA PRIJAVA***");                    
                         if(korisnici.get(i).adminPrivilegije) return 1;
                         else return 2;
                    }
                    else throw new NePostojiUListiException("Greska! PIN nije odgovarajuc! (PIN za datu karticu nije validan!)");
                }
            }
            if(brojac == korisnici.size()) throw new NePostojiUListiException("Greska! Broj kartice nije odgovarajuc! (Kartica ne postoji u listi!)");
        }
        
        catch(NemaSamoBrojeve | PreviseBrojeva | KarticaPreviseCrtica | NePostojiUListiException ex){
            System.out.println(ex.getMessage());
        }
        
        return 3;
    }
    
    public static void validirajEmail(String email) throws NevalidanStartException, NeodgovarajucBrojMajmunskogA, NemaTackeException{  
            if(email.split("@").length > 2)
                throw new NeodgovarajucBrojMajmunskogA("Previse znakova '@' u adresi!");
            else if(email.split("@").length < 2)
                throw new NeodgovarajucBrojMajmunskogA("Nema znaka '@' u adresi!");
            if(email.charAt(0)=='@' || Character.isDigit(email.charAt(0)))
                throw new NevalidanStartException("Nevalidan pocetak adrese!");
            if(!email.substring(email.indexOf("@")).contains("."))
                throw new NemaTackeException("Nema tacke posle znaka '@'!"); 
    }
         
    public static boolean validirajJMBG(String JMBG, ArrayList<Osoba> korisnici) throws NevalidanJMBGException{
        int brojM = Integer.parseInt(JMBG, 2, 4, 10);
        int brojD = Integer.parseInt(JMBG, 0, 2, 10);
        try{
            for(int i = 0; i < korisnici.size(); i++){
            if(korisnici.get(i).jmbg.equals(JMBG))
                throw new NevalidanJMBGException("Greska! (Korisnik sa ovim JMBG-om vec postoji)");
        }
        }catch(NevalidanJMBGException ex)
        {
            System.out.println(ex.getMessage());
        }

        if(JMBG.length()!=13) return false;
        switch(brojM)
        {
            case 1:{
                if(brojD > 31 ||  brojD<1)  return false;                  
                else return true;
            }
            case 2:
                if(brojD > 28 || brojD<1) return false;
                else return true;
            case 3:
                if(brojD > 31 || brojD<1) return false;
                else return true;
            case 4:
                if(brojD > 30 || brojD<1) return false;
                else return true;
            case 5:
                if(brojD > 31 || brojD<1) return false;
                else return true;
            case 6:
                if(brojD > 30||  brojD<1) return false;
                else return true;
            case 7:
                if(brojD > 31 || brojD<1) return false;
                else return true;
            case 8:
                if(brojD > 31 || brojD<1) return false;
                else return true;
            case 9:
                if(brojD > 30 || brojD<1) return false;
                else return true;
            case 10:
                 if(brojD > 31 || brojD<1) return false;
                else return true;
            case 11:
                if(brojD > 30 ||  brojD<1) return false;
                else return true;
            case 12:
                if(brojD > 31 ||  brojD<1) return false;
                else return true;
            default:
                return false;
        }
    }
        
    public static String validirajImePrezime(String imeIliPrezime) throws NevalidnoImePrezimeException{
        String kopija = "";
        
        //try{
            for(int i = 0 ; i < imeIliPrezime.length(); i++){
                if(Character.isDigit(imeIliPrezime.charAt(i)) || !Character.isAlphabetic(imeIliPrezime.charAt(i)))
                    throw new NevalidnoImePrezimeException("Ime i prezime moraju imati samo slova u sebi!");
                
            }
            for(int i = 0 ; i < imeIliPrezime.length(); i++){
                if(i==0)
                    kopija+=Character.toUpperCase(imeIliPrezime.charAt(i));
                else kopija+=Character.toLowerCase(imeIliPrezime.charAt(i));
            }
            
        /*}
        catch(NevalidnoImePrezimeException ex){
            System.err.println(ex.getMessage());
        }
        */
        return kopija;
    }
    
    @Override
    public String toString() {
        return ">Ime: " + ime + "\n>Prezime: " + prezime + "\n>JMBG: " + jmbg + "\n>Broj Kartice: " + brojKartice + "\n>PIN: " + pin + "\n>ID: " + id;
    }
    
    
}
