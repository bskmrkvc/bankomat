/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bankomat;

import customExceptions.BrisanjeUlogovanogKorisnikaException;
import customExceptions.KarticaPreviseCrtica;
import customExceptions.NePostojiUListiException;
import customExceptions.NemaSamoBrojeve;
import customExceptions.NemaTackeException;
import customExceptions.NeodgovarajucBrojMajmunskogA;
import customExceptions.NevalidanJMBGException;
import customExceptions.NevalidanStartException;
import customExceptions.NevalidnaVrednostException;
import customExceptions.NevalidnoImePrezimeException;
import customExceptions.PreviseBrojeva;
import customExceptions.VecPostojiUListiException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


/**
 *
 * @author Bosko
 */
public class Admin extends Osoba implements Ispis{
    private String email;

    public Admin(String ime, String prezime, String jmbg, String brojKartice, String pin, int id, boolean adminPrivilegije ,String email) {
        super(ime, prezime, jmbg, brojKartice, pin, id, adminPrivilegije);
        this.email = email;
    }

    public Admin() {
        super();
        this.email = "";
    }
    
    public static int generisiID(ArrayList<Osoba> korisnici){
        Random random = new Random();
        boolean flag = false;
        int randomID = random.nextInt(1000) + 1;
        while(true){ 
            flag = false;
            for(int i  = 0; i < korisnici.size(); i++){           
                if(korisnici.get(i).id == randomID){
                    flag = true;
                    break;
                }             
            }
            if(flag)
                randomID = random.nextInt(1000) + 1;
            else return randomID;
        }
        
    }
    
    public void dodajKlijenta(Bankomat b){
                
        try{
            //unesite broj kartice klijenta / id klijenta
            ArrayList<Osoba> pom = b.getKorisnici();
            System.out.println("| =>Unesite broj kartice klijenta kog zelite ubaciti u sistem: ");
            Scanner sc = new Scanner(System.in);
            String brKartice = sc.nextLine();           
            String[] podeljenoPoCrtici = brKartice.split("-");
            
            //Ispitivanje validnosti kartice
            for (int i = 0; i < podeljenoPoCrtici.length; i++) {
                for (int j = 0; j < podeljenoPoCrtici[i].length(); j++) {
                    if(!Character.isDigit(podeljenoPoCrtici[i].charAt(j))) throw new NemaSamoBrojeve("Greska! Broj kartice nije odgovarajuc! (Broj kartice sme da sadrzi samo brojeve!)");
                    if(podeljenoPoCrtici[i].length() != 4) throw new PreviseBrojeva("Greska! Broj kartice nije odgovarajuc! (Svako polje treba sadrzati 4 cifre!)"); 
                }
            }
            if(podeljenoPoCrtici.length != 4) throw new KarticaPreviseCrtica("Greska! Broj kartice nije odgovarajuc! (Kartica treba sadrzati 4 polja od po 4 cifre odvojene znakom " + "\'" + "-" + "\'" + " izmedju!)"); 
           

            //proveri u listi da li vec postoji jmbg, broj kartice...
            for(int i = 0; i < b.getKorisnici().size(); i++){
                if(b.getKorisnici().get(i).brojKartice.equals(brKartice))
                    throw new VecPostojiUListiException("Greska! (Korisnik sa ovom karticom vec postoji)");
            }
                        
            //dodaj ga
            String temp;
            System.out.println("| =>Unesite ime korisnika: ");
            temp = sc.nextLine();
            String ime = Osoba.validirajImePrezime(temp);
            
            System.out.println("| =>Unesite prezime korisnika: ");    
            temp = sc.nextLine();
            String prezime = Osoba.validirajImePrezime(temp);
            
            System.out.println("| =>Unesite PIN: ");
            String pin=sc.nextLine();
            for (int i = 0; i < pin.length(); i++) {
                    if(!Character.isDigit(pin.charAt(i))) throw new NemaSamoBrojeve("Greska! PIN kartice nije odgovarajuc! (PIN sme da sadrzi samo brojeve!)");
                }
            if(pin.length() != 4) throw new PreviseBrojeva("Greska! PIN nije odgovarajuc! (PIN sme sadrzati samo 4 broja!)");
            
            System.out.println("| =>Unesite JMBG: ");
            String jmbg = sc.nextLine();
            if(!Osoba.validirajJMBG(jmbg, b.getKorisnici()))
                throw new NevalidanJMBGException("Greska! (Nije unet validan JMBG)");
            
            int id = Admin.generisiID(b.getKorisnici());
            
            String izbor;
            int adminOpcija = 2;
            do{
              System.out.println("| =>Da li je korisnik admin?");
              System.out.println("1. Da\n2. Ne");
              izbor = sc.next();
                for (int i = 0; i < izbor.length(); i++) {
                    if(!Character.isDigit(izbor.charAt(i))) throw new NemaSamoBrojeve("Greska! Izbor nije odgovarajuc!");
                }
             adminOpcija = Integer.parseInt(izbor);
              if(adminOpcija < 1 || adminOpcija > 2)
                    System.out.println("Greska! Uneli ste los izbor!");
            }while(adminOpcija < 1 || adminOpcija > 2);
                       
            
            if(adminOpcija == 1){
                System.out.println("Unesite email admina : ");
                if(sc.hasNextLine())
                    sc.nextLine();
                String email = sc.nextLine();
                Osoba.validirajEmail(email);    
                Admin a = new Admin(ime, prezime, jmbg, brKartice, pin, id, true, email);
                pom.add(a);
                b.setKorisnici(pom);
                System.out.println("***USPESNO DODAT ADMIN***"); 
            }
            else{
                System.out.println("Unesite stanje korisnika : ");
                if(sc.hasNextLine())
                    sc.nextLine();
                temp = sc.nextLine();
                float stanje = Float.parseFloat(temp);
                if(stanje < 0)
                    throw new NevalidnaVrednostException("Stanje na racunu ne moze biti negativno!");
                Korisnik k = new Korisnik(ime, prezime, jmbg, brKartice, pin, id, false, stanje);
                pom.add(k);
                b.setKorisnici(pom);
                System.out.println("***USPESNO DODAT KORISNIK***"); 
            }
        }
        catch(NemaSamoBrojeve | PreviseBrojeva | VecPostojiUListiException | KarticaPreviseCrtica | NevalidnaVrednostException | NevalidanJMBGException | NevalidnoImePrezimeException | NevalidanStartException | NeodgovarajucBrojMajmunskogA | NemaTackeException ex ){        
            System.err.println(ex.getMessage());
        }
        
        Osoba.upisiKorisnikeUFajl(b.getKorisnici(), "korisnici.json");
    }
    
    public void dodajStanjeBankomata(Bankomat b){
        Scanner sc = new Scanner(System.in);
        String temp = "";
        try{
            System.out.println("| =>Unesite novac kojim ce raspolagati bankomat: ");
            temp = sc.next();
            for (int i = 0; i < temp.length(); i++) {
                if(!Character.isDigit(temp.charAt(i))) throw new NemaSamoBrojeve("Greska! Iznos nije odgovarajuc! (Iznos mora sadrzati samo brojeve)");
            }
            float stanje = Float.parseFloat(temp);
            if(stanje < 0)
                throw new NevalidnaVrednostException("Greska! Iznos nije odgovarajuc! (Iznos mora biti pozitivan)");
            b.setNovac(stanje);
            b.azurirajBankomat();
            System.out.println("| =>Uspesno azurirano stanje na bankomatu!");
        }catch(NevalidnaVrednostException | NemaSamoBrojeve ex)
        {
            System.out.println(ex.getMessage());
        }

    }
    
    public void obrisiKlijenta(Bankomat b){
        try{
            System.out.println("| =>Unesite broj kartice klijenta koga zelite da uklonite iz sistema: ");
            Scanner sc = new Scanner(System.in);
            int brojac = 0;
            String brKartice = sc.nextLine();           
            String[] podeljenoPoCrtici = brKartice.split("-");
            
            //Ispitivanje validnosti kartice
            for (int i = 0; i < podeljenoPoCrtici.length; i++) {
                for (int j = 0; j < podeljenoPoCrtici[i].length(); j++) {
                    if(!Character.isDigit(podeljenoPoCrtici[i].charAt(j))) throw new NemaSamoBrojeve("Greska! Broj kartice nije odgovarajuc! (Broj kartice sme da sadrzi samo brojeve!)");
                    if(podeljenoPoCrtici[i].length() != 4) throw new PreviseBrojeva("Greska! Broj kartice nije odgovarajuc! (Svako polje treba sadrzati 4 cifre!)"); 
                }
            }
            if(podeljenoPoCrtici.length != 4) throw new KarticaPreviseCrtica("Greska! Broj kartice nije odgovarajuc! (Kartica treba sadrzati 4 polja od po 4 cifre odvojene znakom " + "\'" + "-" + "\'" + " izmedju!)"); 
           

            //proveri u listi da li vec postoji jmbg, broj kartice...
            for(int i = 0; i < b.getKorisnici().size(); i++){
                if(b.getKorisnici().get(i).brojKartice.equals(brKartice)) 
                {
                    if(b.getKorisnici().get(i).brojKartice.equals(brojKartice))
                    {
                        throw new BrisanjeUlogovanogKorisnikaException("Greska! (Ne mozete obrisati ulogovanog korisnika)");
                    }
                    else
                    {
                        ArrayList<Osoba> pom = b.getKorisnici();
                        pom.remove(i);
                        b.setKorisnici(pom);
                        System.out.println("***USPESNO OBRISANA OSOBA***");
                        brojac--;
                        break;
                    }
                }
                else brojac++; 
            }
            if(brojac == b.getKorisnici().size()) throw new NePostojiUListiException("Greska! (Korisnik sa ovom karticom ne postoji)");
            
        }catch(NePostojiUListiException | KarticaPreviseCrtica | PreviseBrojeva | NemaSamoBrojeve | BrisanjeUlogovanogKorisnikaException ex)
        {
            System.out.println(ex.getMessage());
        }
        upisiKorisnikeUFajl(b.getKorisnici(), "C:\\Users\\fyi\\Downloads\\Bankomat\\Bankomat\\Bankomat\\korisnici.json");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    public String toString() {
        return super.toString() + "\n>Email: " + email;
    }

    @Override
    public void ispis() {
       System.out.println("| =>Email: " + this.email);
    }
    
    
    
}
