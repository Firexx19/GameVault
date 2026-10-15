import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

class Jatek {
    private String cim;
    private String mufaj;
    private String platform;
    private int ev;
    private double ertekeles;
    private int jatekidoOra;
    private int ar;

    public Jatek(String cim, String mufaj, String platform, int ev,
                 double ertekeles, int jatekidoOra, int ar) {
        this.cim = cim;
        this.mufaj = mufaj;
        this.platform = platform;
        this.ev = ev;
        this.ertekeles = ertekeles;
        this.jatekidoOra = jatekidoOra;
        this.ar = ar;
    }

    public String getCim() { return cim; }
    public String getMufaj() { return mufaj; }
    public String getPlatform() { return platform; }
    public int getEv() { return ev; }
    public double getErtekeles() { return ertekeles; }
    public int getJatekidoOra() { return jatekidoOra; }
    public int getAr() { return ar; }

    public double getArPerOra() {
        return (double) ar / jatekidoOra;
    }

    @Override
    public String toString() {
        return String.format("[%-26s | %-10s | %-6s | %d | %.1f | %3d óra | %5d Ft]",
                cim, mufaj, platform, ev, ertekeles, jatekidoOra, ar);
    }
}

public class Main {

    static int beolvasEgesz(Scanner sc, String kerdes, int min, int max) {
        while (true) {
            System.out.print(kerdes + " (" + min + " - " + max + "): ");
            try {
                int szam = Integer.parseInt(sc.nextLine().trim());
                if (szam >= min && szam <= max) {
                    return szam;
                }
                System.out.println(" Hiba: " + min + " és " + max + " közé kell esnie");
            } catch (NumberFormatException e) {
                System.out.println(" Hiba: Nem egész számot adtál meg");
            }
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);
        List<Jatek> jatekok = new ArrayList<>();

        System.out.println("===== GAMEVAULT =====");

        try {
            Scanner fajl = new Scanner(new File("jatekok.txt"));
            while (fajl.hasNextLine()) {
                String sor = fajl.nextLine().trim();
                if (sor.isEmpty()) continue;
                String[] r = sor.split(";");
                jatekok.add(new Jatek(r[0], r[1], r[2], Integer.parseInt(r[3]),
                        Double.parseDouble(r[4]), Integer.parseInt(r[5]), Integer.parseInt(r[6])));
            }
            fajl.close();
            System.out.println(" Fájl betöltve: " + jatekok.size() + " játék");
        } catch (FileNotFoundException e) {
            System.out.println(" A jatekok.txt nem található, üres lista indul");
        }

        String valasz;
        do {
            System.out.println("\n1 - Összes játék");
            System.out.println("2 - Top 5 értékelés szerint");
            System.out.println("3 - Műfaj szerinti lista");
            System.out.println("4 - Keresés cím alapján");
            System.out.println("5 - Átlagos értékelés műfajonként");
            System.out.println("6 - Játékok platformonként");
            System.out.println("7 - Legjobb ár/érték (Top5)");
            System.out.println("8 - Statisztika");
            System.out.println("9 - Új játék felvétele");
            System.out.println("0 - Kilépés");
            System.out.print("Választás: ");
            valasz = sc.nextLine().trim();
            System.out.println();

            if (valasz.equals("1")) {
                jatekok.stream().forEach(j -> System.out.println(j));

            } else if (valasz.equals("2")) {
                List<Jatek> top = jatekok.stream()
                        .sorted((a, b) -> Double.compare(b.getErtekeles(), a.getErtekeles()))
                        .limit(5)
                        .collect(Collectors.toList());
                top.forEach(j -> System.out.println(j));

            } else if (valasz.equals("3")) {
                List<String> mufajok = jatekok.stream()
                        .map(j -> j.getMufaj()).distinct().sorted().collect(Collectors.toList());
                System.out.println("Műfajok: " + String.join(", ", mufajok));
                System.out.print("Melyik műfaj? ");
                String mufaj = sc.nextLine().trim();
                List<Jatek> talalat = jatekok.stream()
                        .filter(j -> j.getMufaj().equalsIgnoreCase(mufaj))
                        .collect(Collectors.toList());
                if (talalat.isEmpty()) {
                    System.out.println("Nincs ilyen műfaj");
                }
                talalat.forEach(j -> System.out.println(j));

            } else if (valasz.equals("4")) {
                System.out.print("Keresett szöveg: ");
                String keres = sc.nextLine().trim().toLowerCase();
                List<Jatek> talalat = jatekok.stream()
                        .filter(j -> j.getCim().toLowerCase().contains(keres))
                        .collect(Collectors.toList());
                if (talalat.isEmpty()) {
                    System.out.println("Nincs találat");
                }
                talalat.forEach(j -> System.out.println(j));

            } else if (valasz.equals("5")) {
                List<String> mufajok = jatekok.stream()
                        .map(j -> j.getMufaj()).distinct().sorted().collect(Collectors.toList());
                for (String m : mufajok) {
                    double atlag = jatekok.stream()
                            .filter(j -> j.getMufaj().equals(m))
                            .mapToDouble(j -> j.getErtekeles())
                            .average().orElse(0);
                    System.out.printf("%-12s %.2f%n", m, atlag);
                }

            } else if (valasz.equals("6")) {
                List<String> platformok = jatekok.stream()
                        .map(j -> j.getPlatform()).distinct().sorted().collect(Collectors.toList());
                for (String p : platformok) {
                    long db = jatekok.stream().filter(j -> j.getPlatform().equals(p)).count();
                    System.out.println(p + ": " + db + " db");
                }

            } else if (valasz.equals("7")) {
                jatekok.stream()
                        .filter(j -> j.getJatekidoOra() > 0)
                        .sorted((a, b) -> Double.compare(a.getArPerOra(), b.getArPerOra()))
                        .limit(5)
                        .forEach(j -> System.out.printf("%-26s %.0f Ft/óra%n", j.getCim(), j.getArPerOra()));

            } else if (valasz.equals("8")) {
                int osszOra = jatekok.stream().mapToInt(j -> j.getJatekidoOra()).sum();
                OptionalDouble atlagOra = jatekok.stream().mapToInt(j -> j.getJatekidoOra()).average();
                Optional<Jatek> legtobb = jatekok.stream()
                        .max(Comparator.comparingInt(j -> j.getJatekidoOra()));
                long ujak = jatekok.stream().filter(j -> j.getEv() >= 2020).count();
                System.out.println("Összes játékidő: " + osszOra + " óra");
                if (atlagOra.isPresent()) {
                    System.out.println("Átlagos játékidő: " + String.format("%.1f", atlagOra.getAsDouble()) + " óra");
                }
                if (legtobb.isPresent()) {
                    System.out.println("Legtöbbet játszott: " + legtobb.get().getCim());
                }
                System.out.println("2020 vagy újabb játékok: " + ujak + " db");

            } else if (valasz.equals("9")) {
                String cim;
                do {
                    System.out.print("Cím: ");
                    cim = sc.nextLine().trim();
                } while (cim.isEmpty());
                System.out.print("Műfaj: ");
                String mufaj = sc.nextLine().trim();
                System.out.print("Platform: ");
                String platform = sc.nextLine().trim();
                int ev = beolvasEgesz(sc, "Megjelenés éve", 1970, 2030);
                int ertek10 = beolvasEgesz(sc, "Értékelés 10-szerese ", 0, 100);
                int ora = beolvasEgesz(sc, "Játékidő órában", 1, 5000);
                int ar = beolvasEgesz(sc, "Ár forintban", 0, 100000);

                Jatek uj = new Jatek(cim, mufaj, platform, ev, ertek10 / 10.0, ora, ar);
                jatekok.add(uj);
                try {
                    FileWriter fw = new FileWriter("jatekok.txt", true);
                    fw.write(cim + ";" + mufaj + ";" + platform + ";" + ev + ";"
                            + ertek10 / 10.0 + ";" + ora + ";" + ar + "\n");
                    fw.close();
                    System.out.println(" Játék felvéve és mentve");
                } catch (IOException e) {
                    System.out.println(" Hiba a mentéskor");
                }

            } else if (!valasz.equals("0")) {
                System.out.println("Ismeretlen menüpont");
            }
        } while (!valasz.equals("0"));

        System.out.println("Viszlát!!!!");
    }
}
