package bankomat;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Bankomat implements Ispis{
    private String imeBanke, adresa;
    private float novac;
    private boolean ukljucenBankomat;
    private ArrayList<Osoba> korisnici;

    public Bankomat() {
        this.imeBanke = "UniCredit";
        this.adresa = "Vojvode Stepe 74";
        this.novac = 0;
        this.korisnici = new ArrayList<>();
        this.ukljucenBankomat = true;
    }

    public Bankomat(String imeBanke, String adresa) {
        this.imeBanke = imeBanke;
        this.adresa = adresa;
        this.novac = 0;
        this.korisnici = new ArrayList<>();
        this.ukljucenBankomat = true;
    }
    
    public String getImeBanke() {
        return imeBanke;
    }

    public void setImeBanke(String imeBanke) {
        this.imeBanke = imeBanke;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public float getNovac() {
        return novac;
    }

    public void setNovac(float novac) {
        this.novac = novac;
    }

    public boolean isUkljucenBankomat() {
        return ukljucenBankomat;
    }

    public void setUkljucenBankomat(boolean ukljucenBankomat) {
        this.ukljucenBankomat = ukljucenBankomat;
    }

    public ArrayList<Osoba> getKorisnici() {
        return korisnici;
    }

    public void setKorisnici(ArrayList<Osoba> korisnici) {
        this.korisnici = korisnici;
    }
    
    
    public void pokreni(){
        try{
            while(ukljucenBankomat)
            {
            Console console = System.console();
            boolean zauzetBankomat = true;
            Scanner unos = new Scanner(System.in);
            String kartica = "";
            String pin = "";
            String pinC = "";
            String opcija = "";
            Korisnik prijavljenKorisnik = new Korisnik();
            Admin prijavljenAdmin = new Admin();
            this.korisnici = Osoba.procitajOsobe("korisnici.json"); 
            this.novac = procitajStanje("stanje.json");
            Ispis.uvodniMeni(this);
            if(console == null)
            {
                kartica = unos.nextLine();
                System.out.println("| =>PIN: ");
                pin = unos.nextLine();    
            }
            else
            {
                kartica = console.readLine();
                System.out.println("| =>PIN: ");
                pin = new String(console.readPassword());
            }
            int adminOnline = Osoba.validirajKarticuPin(kartica, pin, korisnici);
            for (int i = 0; i < korisnici.size(); i++) {
                if(korisnici.get(i).brojKartice.equals(kartica) && korisnici.get(i).pin.equals(pin) && adminOnline == 1)
                {
                    prijavljenAdmin = (Admin) korisnici.get(i);
                    System.out.println(prijavljenAdmin);
                }
                else if(korisnici.get(i).brojKartice.equals(kartica) && korisnici.get(i).pin.equals(pin) && adminOnline == 2)
                {
                    prijavljenKorisnik = (Korisnik) korisnici.get(i);
                    System.out.println(prijavljenKorisnik);
                }
            } 
            if(adminOnline == 1){
                while(zauzetBankomat){
                    Ispis.adminUvodniMeni();
                    opcija = unos.next();
                    switch(opcija)
                    {
                        case "1":
                            prijavljenAdmin.dodajKlijenta(this);
                            break;
                        case "2":
                            prijavljenAdmin.obrisiKlijenta(this);
                            break;
                        case "3":
                            for (int i = 0; i < korisnici.size(); i++) {
                                if(korisnici.get(i) instanceof Korisnik)
                                    System.out.println(((Korisnik)korisnici.get(i)).toString());
                                else
                                    System.out.println(((Admin)korisnici.get(i)).toString()); 
                                System.out.println("| | | | | | | | | | | | | | | | | | |");
                            }
                            break;
                        case "4":
                            this.ispis();
                            break;
                        case "5":
                            prijavljenAdmin.dodajStanjeBankomata(this);
                            this.azurirajBankomat();
                            break;
                        case "6":
                            if(Transakcija.isObavljenaTransakcija())
                            {
                                Transakcija.ispisiTransakcije("transakcije.json", korisnici);
                                break;
                            }
                            else
                            {
                                System.out.println("| =>U ovoj sesiji nije obavljena nijedna transakcija!");
                                break;
                            }
                        case "7":
                            zauzetBankomat = false;
                            Ispis.izlazak();
                            break;
                        case "8":
                            if(Transakcija.isObavljenaTransakcija()) Transakcija.obrisiTransakcije("transakcije.json");
                            zauzetBankomat = false;
                            ukljucenBankomat = false;
                            this.azurirajBankomat();
                            Ispis.gasenje();
                            break;
                        default:
                            System.out.println("Niste uneli odgovarajucu opciju!");
                            break;
                    }
                }
            }
            else if(adminOnline == 2){
                while(zauzetBankomat){
                    Ispis.korisnikUvodniMeni();
                    opcija = unos.next();
                    switch(opcija)
                    {
                        case "1":
                            prijavljenKorisnik.ispis();
                            break;
                        case "2":
                            prijavljenKorisnik.korisnikIzvrsiTransakciju(this, 1);
                            break;
                        case "3":
                            prijavljenKorisnik.korisnikIzvrsiTransakciju(this, 2);
                            break;
                        case "4":
                            prijavljenKorisnik.korisnikPromenaPina(korisnici);
                            break;
                        case "5":
                            zauzetBankomat = false;
                            this.azurirajBankomat();
                            Ispis.izlazak();
                            break;
                        default:
                            System.out.println("Niste uneli odgovarajucu opciju!");
                            break;

                    }
                } 
              } 
           }
           
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    public void azurirajBankomat(){
        PrintWriter pw = null;
        String putanja = "stanje.json";
        JSONObject obj = new JSONObject();
        obj.put("stanjeBankomata", this.novac);         
        try {
            pw=new PrintWriter(putanja);
            pw.write(obj.toString());
        } catch (FileNotFoundException ex) {
            System.out.println("Greska prilikom upisa!\n"+ex.getMessage());
        }
        finally{
            if(pw != null)
            pw.close();
        }
    }    
    public float procitajStanje(String putanja) throws Exception{
        FileReader fr = new FileReader(putanja);
        JSONObject jsonStanje = (JSONObject) new JSONParser().parse(fr);
        float stanje = 0;
        stanje = Float.parseFloat(jsonStanje.get("stanjeBankomata").toString());

        return stanje;
    }            
    @Override
    public void ispis() {
        System.out.println("| => Stanje bankomata: " + this.novac);
    }        
    
  
}
