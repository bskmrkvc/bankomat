package bankomat;
public interface Ispis {
    public void ispis();
    public static void uvodniMeni(Bankomat b){
        System.out.println(" - - - - - - - - - - - - - - - - - - -");
        System.out.println("| =>ATM - " + b.getImeBanke() + " - " + b.getAdresa() + " |");
        System.out.println(" - - - - - - - - - - - - - - - - - - -");
        System.out.println("| =>Dobrodosli u UniCredit bankomat!\nMolimo vas unesite vasu karticu: ");
    }
    
    public static void korisnikUvodniMeni(){
        System.out.println(" - - - - - - - - - - - - - - - - - - -");
        System.out.println("| =>Molimo izaberite transakciju: ");
        System.out.println("| =>1.Upit stanja");
        System.out.println("| =>2.Podigni novac");
        System.out.println("| =>3.Uplata");
        System.out.println("| =>4.Promena pina");
        System.out.println("| =>5.Izlaz");
        System.out.println(" - - - - - - - - - - - - - - - - - - -");
    }
    
    public static void korisnikPodigniNovac(){
        System.out.println(" - - - - - - - - - - - - - - - - - - -");
        System.out.println("| =>1. 200 RSD");
        System.out.println("| =>2. 600 RSD");
        System.out.println("| =>3. 1000 RSD");
        System.out.println("| =>4. Drugi iznos");
        System.out.println(" - - - - - - - - - - - - - - - - - - -");
    }
    
    public static void adminUvodniMeni(){
        System.out.println(" - - - - - - - - - - - - - - - - - - -");
        System.out.println("| =>Molimo izaberite opciju: ");
        System.out.println("| =>1. Dodaj klijenta");
        System.out.println("| =>2. Obrisi klijenta");
        System.out.println("| =>3. Ispisi korisnike");
        System.out.println("| =>4. Stanje bankomata");
        System.out.println("| =>5. Azuriraj novac bankomata");
        System.out.println("| =>6. Proveri transakcije");
        System.out.println("| =>7. Izlaz");
        System.out.println("| =>8. Ugasi bankomat");
        System.out.println(" - - - - - - - - - - - - - - - - - - -");
    }
    
    public static void izlazak(){
        System.out.println(" - - - - - - - - - - - - - - - - - - -");
        System.out.println("| => Prijatno! Molimo vas uzmite vasu karticu!");
        System.out.println(" - - - - - - - - - - - - - - - - - - -");
    }
    
    public static void gasenje(){
        System.out.println(" - - - - - - - - - - - - - - - - - - -");
        System.out.println("| => !SHUTTING DOWN! <= |");
        System.out.println(" - - - - - - - - - - - - - - - - - - -");  
    }
}
