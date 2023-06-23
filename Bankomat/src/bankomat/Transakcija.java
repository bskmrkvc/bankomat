/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bankomat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Bosko
 */
public class Transakcija implements Ispis{
    private Korisnik k;
    private static int idTransakcije;
    private static ArrayList<Transakcija> listaTransakcija = new ArrayList<>();
    private float novac;
    private static boolean obavljenaTransakcija;

    public Transakcija(Korisnik k, float novac) {
        this.k = k;
        this.idTransakcije++;
        this.novac = novac;
        
        this.listaTransakcija.add(this);
    }

    public Transakcija() {
        this.k = new Korisnik();
        this.novac = 0;
    }

    public static boolean isObavljenaTransakcija() {
        return obavljenaTransakcija;
    }
    
    
    
    @Override
    public void ispis(){
        System.out.println("| =>ID Transakcije: " + idTransakcije);
        System.out.println("| =>Promena stanja na racunu : " + novac);
    }   
    
    public void izvrsiTransakciju(int opcija){
        k.setSumaNovca(k.getSumaNovca() + novac); 
        if(opcija==1)
            this.ispis();
        else System.out.println("| =>Transakcija uspesno obavljena, prijatan dan!");
        obavljenaTransakcija = true;
        upisiTransakcijeUFajl(this.listaTransakcija, "transakcije.json");
    
    }
    
    public static void obrisiTransakcije(String putanja){
        File file = new File(putanja);

       try (PrintWriter pw = new PrintWriter(file)) {}
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        //System.out.println(listaTransakcija);
    }
    
    
    public void upisiTransakcijeUFajl(ArrayList<Transakcija> listaTransakcija, String putanja){
        JSONArray jsonTransakcije=new JSONArray();
        PrintWriter pw = null;
        int i = 1;
        for (Transakcija t : listaTransakcija) {
            JSONObject obj = new JSONObject();
            obj.put("id_transakcije", i);
            obj.put("id_korisnika", t.getK().getId());
            i++;
            obj.put("promena_stanja", t.getNovac());
            
            
            jsonTransakcije.add(obj);
            }
            try {
                pw=new PrintWriter(putanja);
                pw.write(jsonTransakcije.toJSONString());
            } catch (FileNotFoundException ex) {
                System.out.println("Greska prilikom upisa!\n" + ex.getMessage());
            }
            finally{
                if(pw != null)
                pw.close();
            }
    }

    public Korisnik getK() {
        return k;
    }

    public void setK(Korisnik k) {
        this.k = k;
    }

    public static int getIdTransakcije() {
        return idTransakcije;
    }

    public static void setIdTransakcije(int idTransakcije) {
        Transakcija.idTransakcije = idTransakcije;
    }

    public float getNovac() {
        return novac;
    }

    public void setNovac(float novac) {
        this.novac = novac;
    }

    public static ArrayList<Transakcija> getListaTransakcija() {
        return listaTransakcija;
    }

    /*
    public static void ispisListeTransakcija(ArrayList<Transakcija> procitaneTransakcije){
        for (int i = 0; i < procitaneTransakcije.size(); i++) {
            System.out.println("| =>ID Transakcije: " + (i+1));
            System.out.println("| =>Ime klijenta: " + procitaneTransakcije.get(i).getK().getIme());
            System.out.println("| =>Prezime klijenta: " + procitaneTransakcije.get(i).getK().getPrezime());
            String[] splitovano = procitaneTransakcije.get(i).getK().getBrojKartice().split("-");
            System.out.print("| =>Broj kartice: **** - **** - ");
            for (int j = 2; j < splitovano.length; j++) {
                System.out.print(splitovano[j]);
                if(j == 2) System.out.print(" - ");
            }
            System.out.println("\n| =>Promena stanja na racunu : " + procitaneTransakcije.get(i).getNovac());
            System.out.println("| | | | | | | | | | | | | | | | | | |");
        }
        
    }
*/
    
        public static void ispisiTransakcije(String putanja, ArrayList<Osoba> korisnici) throws Exception{
        FileReader fr = new FileReader(putanja);
        JSONArray jsonTransakcije = (JSONArray) new JSONParser().parse(fr);

        for(Object o : jsonTransakcije)
        {
            JSONObject jsonTransakcija = (JSONObject) o;
            int id_transakcije = Integer.parseInt(jsonTransakcija.get("id_transakcije").toString());
            float promena_stanja = Float.parseFloat(jsonTransakcija.get("promena_stanja").toString());
            int id_korisnika = Integer.parseInt(jsonTransakcija.get("id_korisnika").toString());
            for (int i = 0; i < korisnici.size(); i++) {
                if(korisnici.get(i).id == id_korisnika)
                {
                    System.out.println("| =>ID Transakcije: " + id_transakcije);
                    System.out.println("| =>Ime klijenta: " + korisnici.get(i).ime);
                    System.out.println("| =>Prezime klijenta: " + korisnici.get(i).prezime);
                    String[] splitovano = korisnici.get(i).brojKartice.split("-");
                    System.out.print("| =>Broj kartice: **** - **** - ");
                    for (int j = 2; j < splitovano.length; j++) {
                        System.out.print(splitovano[j]);
                        if(j == 2) System.out.print(" - ");
                    }
                    System.out.println("\n| =>Promena stanja na racunu : " + promena_stanja);
                    System.out.println("| | | | | | | | | | | | | | | | | | |");
                    break;
                }
                
            }
        }    
    }
}
