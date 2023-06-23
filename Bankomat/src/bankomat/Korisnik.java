package bankomat;

import customExceptions.NedovoljnoSredstavaZaPodizanjeException;
import customExceptions.NemaSamoBrojeve;
import customExceptions.NeodgovarajucaOpcijaException;
import customExceptions.NevalidnaVrednostException;
import customExceptions.PreviseBrojeva;
import java.util.ArrayList;
import java.util.Scanner;

public class Korisnik extends Osoba implements Ispis{
    private float sumaNovca;
   

    public Korisnik(String ime, String prezime, String jmbg, String brojKartice, String pin, int id, boolean adminPrivilegije ,float sumaNovca) {
        super(ime, prezime, jmbg, brojKartice, pin, id, adminPrivilegije);
        this.sumaNovca = sumaNovca;
    }

    public Korisnik() {
        super();
        this.sumaNovca = 0;
    }
    
    public void korisnikPromenaPina(ArrayList<Osoba> korisnici){
        Scanner sc = new Scanner(System.in); 
        try{
            System.out.println("| =>Unesite trenutni pin: ");
            String pin = sc.nextLine();
            if(this.pin.equals(pin)){
                System.out.println("| =>Unesite novi pin: ");
                pin = sc.nextLine();
                for (int i = 0; i < pin.length(); i++) {
                    if(!Character.isDigit(pin.charAt(i))) throw new NemaSamoBrojeve("Greska! PIN kartice nije odgovarajuc! (PIN sme sadrzati samo brojeve!)");
                }
                if(pin.length() != 4) throw new PreviseBrojeva("Greska! PIN nije odgovarajuc! (PIN sme sadrzati samo 4 broja!)");
                
                //prijavljenKorisnik.setPin(pin);
                for(int i = 0 ; i < korisnici.size(); i++){
                    if(korisnici.get(i).id == this.id){
                        ((Korisnik)korisnici.get(i)).setPin(pin);
                        
                        Osoba.upisiKorisnikeUFajl(korisnici, "korisnici.json");
                        System.out.println("| =>Uspesno ste promenili PIN!");
                        break;
                    }
                        
                }
            }
            else{
                System.out.println("Uneli ste pogresan PIN!");
            }
            
        }
        catch(NemaSamoBrojeve | PreviseBrojeva ex){
            System.out.println(ex.getMessage());
        }
    }
    
    public void korisnikIzvrsiTransakciju(Bankomat b, int opcija){      
        try{
                System.out.println("Unesite iznos koji zelite da podignete : ");
                Ispis.korisnikPodigniNovac();
                Scanner sc = new Scanner(System.in);
                String unos = sc.next();
                float iznos=0;
                switch(unos){
                    case "1":
                        iznos=200;
                        break;
                    case "2":
                        iznos=600;
                        break;
                    case "3":
                        iznos=1000;
                        break;
                    case "4":
                        System.out.println("Unesite zeljeni iznos: ");
                        if(sc.hasNextLine()) //CISCENJE BAFERA
                            sc.nextLine();
                        unos = sc.next();
                        for (int i = 0; i < unos.length(); i++) {
                        if(!Character.isDigit(unos.charAt(i))) throw new NemaSamoBrojeve("Greska! Iznos nije odgovarajuc!");
                        }
                        iznos = Integer.parseInt(unos);
                        break;
                    default:
                        throw new NeodgovarajucaOpcijaException("Greska! Niste uneli dobar izbor!");
                }
                
                if(iznos<0)
                    throw new NevalidnaVrednostException("Greska! Negativan iznos!");
                
                if(opcija==1){
                    if(iznos > this.sumaNovca)
                        throw new NedovoljnoSredstavaZaPodizanjeException("Greska! Nemate dovoljno sredstava na racunu!"); 
                    
                    if(iznos > b.getNovac())
                        throw new NedovoljnoSredstavaZaPodizanjeException("Greska! Nedovoljno sredstava na bankomatu za podizanje!\nRaspolozivo stanje bankomata: " + b.getNovac());
                    
                    if(iznos > 40000)
                        throw new NedovoljnoSredstavaZaPodizanjeException("Greska! Maksimalna kolicina za podizanje je 40.000"); 
                    
                    Transakcija t = new Transakcija(this, iznos*(-1));
                    int priznanica;
                     
                     do{
                        System.out.println("| =>Zelite li priznanicu?");
                        System.out.println("1. Da\n2. Ne");
                        //if(sc.hasNext()) sc.next();
                        unos = sc.next();
                        for (int i = 0; i < unos.length(); i++) {
                            if(!Character.isDigit(unos.charAt(i))) throw new NemaSamoBrojeve("Greska! Izbor nije odgovarajuc!");
                        }
                        priznanica = Integer.parseInt(unos);
                        if(priznanica < 1 || priznanica > 2)
                        System.out.println("Greska! Uneli ste los izbor!");
                    }while(priznanica < 1 || priznanica > 2);
                    
                    t.izvrsiTransakciju(priznanica);
                    /*
                    if(priznanica == 1){
                        t.izvrsiTransakciju(priznanica);
                    }
                    else{
                        //t.upisiTransakcijeUFajl(Transakcija.getListaTransakcija(), "transakcije.json");
                        t.izvrsiTransakciju(priznanica);
                        
                    }
                    */
                    
                    b.setNovac(b.getNovac() - iznos);
                }    
                else {
                    Transakcija t = new Transakcija(this, iznos);
                    int priznanica;
                     do{
                        System.out.println("| =>Zelite li priznanicu?");
                        System.out.println("1. Da\n2. Ne");
                        //if(sc.hasNext()) sc.next();
                        unos = sc.next();
                        for (int i = 0; i < unos.length(); i++) {
                            if(!Character.isDigit(unos.charAt(i))) throw new NemaSamoBrojeve("Greska! Izbor nije odgovarajuc!");
                        }
                        priznanica = Integer.parseInt(unos);
                        if(priznanica < 1 || priznanica > 2)
                        System.out.println("Greska! Uneli ste los izbor!");
                    }while(priznanica < 1 || priznanica > 2);
                    
                    if(priznanica == 1){
                        t.izvrsiTransakciju(priznanica);
                    }
                    else{
                        System.out.println("| =>Transakcija uspesno obavljena, prijatan dan!");
                    }
                    b.setNovac(b.getNovac() + iznos);
                }
                
                for(int i = 0 ; i < b.getKorisnici().size(); i++){
                    if(b.getKorisnici().get(i).id == this.id){
                        b.getKorisnici().set(i, this);
                        
                        Osoba.upisiKorisnikeUFajl(b.getKorisnici(), "korisnici.json");
                        break;
                    }
                        
                }
                b.azurirajBankomat();
            }
            catch(NumberFormatException | NevalidnaVrednostException | NedovoljnoSredstavaZaPodizanjeException | NeodgovarajucaOpcijaException | NemaSamoBrojeve ex){
                System.out.println(ex.getMessage());
            }
           //proverava da li ima dovoljno novca na bankomatu za podizanje
           //novac na bankomatu za podizanje se menja pri svakoj transakciji
        }

    @Override
    public String toString() {
        return super.toString() +"\n>Novac na racunu: " + sumaNovca;
    }

    
    @Override
    public void ispis() {
        System.out.println("| =>Novac na racunu: " + this.sumaNovca);
    }
    
    public float getSumaNovca() {
        return sumaNovca;
    }

    public void setSumaNovca(float sumaNovca) {
        this.sumaNovca = sumaNovca;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getJmbg() {
        return jmbg;
    }

    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
    }

    public String getBrojKartice() {
        return brojKartice;
    }

    public void setBrojKartice(String brojKartice) {
        this.brojKartice = brojKartice;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    

    
    
    
}
