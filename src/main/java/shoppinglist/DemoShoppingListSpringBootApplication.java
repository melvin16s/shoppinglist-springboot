package shoppinglist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import shoppinglist.entity.DaftarBelanja;
import shoppinglist.entity.DaftarBelanjaDetil;
import shoppinglist.repository.DaftarBelanjaDetilRepo;
import shoppinglist.repository.DaftarBelanjaRepo;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
public class DemoShoppingListSpringBootApplication implements CommandLineRunner
{
    @Autowired
    private DaftarBelanjaRepo repo;

    @Autowired
    private DaftarBelanjaDetilRepo detilRepo;

    public static void main(String[] args) {
        SpringApplication.run(DemoShoppingListSpringBootApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Boolean exit = false;
        String input;

        Scanner s = new Scanner(System.in);

        while(!exit) {
            System.out.println("MENU \n +" +
                    "1. Membaca sebuah objek DaftarBelanja berdasarkan ID \n" +
                    "2. Mencari daftar DaftarBelanja berdasarkan kemiripan string judul yg diberikan \n" +
                    "3. Menyimpan sebuah objek DaftarBelanja ke tabel database \n" +
                    "4. Mengupdate sebuah objek DaftarBelanja ke tabel database \n" +
                    "5. Menghapus objek DaftarBelanja berdasarkan ID yg diberikan \n" +
                    "6. Membaca Semua Record DaftarBelanja \n" +
                    "[X] untuk Exit");
            System.out.print("Input: ");
            input = s.nextLine();
            switch(input) {
                case "1":
                    System.out.println("Membaca sebuah objek DaftarBelanja berdasarkan ID.");
                    readObjekDaftarBelanjaBerdasarkanID();
                    break;
                case "2":
                    System.out.println("Mencari daftar DaftarBelanja berdasarkan kemiripan string judul yg diberikan.");
                    cariObjekDaftarBelanjaBerdasarkanJudul();
                    break;
                case "3":
                    System.out.println("Menyimpan sebuah objek DaftarBelanja ke tabel database.");
                    simpanObjectDaftarBelanjaToDB();
                    break;
                case "4":
                    System.out.println("Mengupdate sebuah objek DaftarBelanja ke tabel database.");
                    updateObjectDaftarBelanjaToDB();
                    break;
                case "5":
                    System.out.println("Menghapus objek DaftarBelanja berdasarkan ID yg diberikan.");
                    hapusObjectDaftarBelanjaBerdasarkanID();
                    break;
                case "6":
                    System.out.println("Membaca Semua Record DaftarBelanja");
                    printAll();
                case "X", "x":
                    break;
            }
            System.out.println("Press any key to continue / [X] to Exit");
            input = s.nextLine();
            if(input.equals("X") || input.equals("x")) {
                exit = true;
            }
        }

    }

    // Print semua daftar
    public void printAll() {
        List<DaftarBelanja> all = repo.findAll();
        for (DaftarBelanja db : all) {
            System.out.println("[" + db.getId() + "] " + db.getJudul());

            List<DaftarBelanjaDetil> listBarang = db.getDaftarBarang();
            for (DaftarBelanjaDetil barang : listBarang) {
                System.out.println(barang.getNamaBarang() + " " + barang.getByk() + " " + barang.getSatuan());
            }
        }
    }

    // Membaca sebuah objek DaftarBelanja berdasarkan ID.
    public void readObjekDaftarBelanjaBerdasarkanID() {
        Scanner keyb = new Scanner(System.in);

        // Baca berdasarkan ID
        System.out.print("Masukkan ID dari objek DaftarBelanja yg ingin ditampilkan: ");
        long id = Long.parseLong(keyb.nextLine());
        System.out.println("Hasil pencarian: ");

        Optional<DaftarBelanja> optDB = repo.findById(id);
        if (optDB.isPresent()) {
            DaftarBelanja db = optDB.get();
            System.out.println("Judul: " + db.getJudul());
        }
        else {
            System.out.println("Object Not Found.");
        }
    }

    // Mencari daftar DaftarBelanja berdasarkan kemiripan string judul yg diberikan
    public void cariObjekDaftarBelanjaBerdasarkanJudul() {
        Scanner keyb = new Scanner(System.in);

        System.out.print("Masukkan judul: ");
        String judul = keyb.nextLine();

        List<DaftarBelanja> list_db = repo.findByJudulLike(judul);

        if (list_db.size() > 0) {

            for (DaftarBelanja db : list_db) {
                System.out.println(db.getId()+" . "+db.getJudul());
                List<DaftarBelanjaDetil> daftarBarang = db.getDaftarBarang();

                for (DaftarBelanjaDetil barang : daftarBarang) {
                    System.out.println(barang.getNamaBarang() + " " + barang.getByk() + " " + barang.getSatuan());
                }
            }
        } else {
            System.out.println("Judul Not Found");
        }
    }

    // Menyimpan sebuah objek DaftarBelanja ke tabel database.
    public void simpanObjectDaftarBelanjaToDB() {
        Scanner keyb = new Scanner(System.in);

        String judul;

        long id = Long.parseLong(keyb.nextLine());
        Optional<DaftarBelanja> optDB = repo.findById(id);

        System.out.print("Masukkan judul : ");
        judul = keyb.nextLine();

        DaftarBelanja in_db = new DaftarBelanja();
        in_db.setJudul(judul);
        in_db.setTanggal(LocalDateTime.now());
        in_db = repo.save(in_db);

        List<DaftarBelanjaDetil> detils = new LinkedList();

        int counter = 1;
        String namaBarang = "";

        while(true) {
            System.out.println("[X] to Exit ");
            System.out.print("Nama Barang:");

            namaBarang = keyb.nextLine();

            if (namaBarang.equals("X")){
                break;
            }

            System.out.print("Banyak :");
            float jml = Float.parseFloat(keyb.nextLine());
            System.out.print("Satuan :");
            String satuan = keyb.nextLine();
            System.out.print("Memo :");
            String memo = keyb.nextLine();

            DaftarBelanjaDetil detil = new DaftarBelanjaDetil();

            detil.setNoUrut(counter);
            detil.setNamaBarang(namaBarang);
            detil.setByk(jml);
            detil.setSatuan(satuan);
            detil.setMemo(memo);
            detils.add(detil);
            counter++;
        }

        for (DaftarBelanjaDetil barang : detils) {
            barang.setInduk(in_db);
            detilRepo.save(barang);
        }
        optDB = repo.findById(in_db.getId());
        in_db = optDB.get();
        System.out.println("Insert: ");
        System.out.println(in_db.getId() + " . " + in_db.getJudul());

        List<DaftarBelanjaDetil> daftarBarang = in_db.getDaftarBarang();

        for (DaftarBelanjaDetil barang : daftarBarang) {
            System.out.println(barang.getNamaBarang() + " " + barang.getByk() + " " + barang.getSatuan());
        }

    }

    // Mengupdate sebuah objek DaftarBelanja ke tabel database.
    public void updateObjectDaftarBelanjaToDB() {
        Scanner keyb = new Scanner(System.in);

        String judul;
        String namaBarang;

        long id = Long.parseLong(keyb.nextLine());
        Optional<DaftarBelanja> optDB = repo.findById(id);

        List<DaftarBelanjaDetil> daftarBarang;
        List<DaftarBelanja> all = repo.findAll();

        for (DaftarBelanja db : all) {
            System.out.println(db.getId()+" . "+db.getJudul());
            daftarBarang = db.getDaftarBarang();

            for (DaftarBelanjaDetil barang : daftarBarang) {
                System.out.println(barang.getNoUrut()+ ". "+ barang.getNamaBarang() + " " + barang.getByk() + " " + barang.getSatuan());
            }
        }

        System.out.print("Masukkan ID: ");
        id = Long.parseLong(keyb.nextLine());
        Optional<DaftarBelanja> update_db = repo.findById(id);

        if (update_db.isPresent()) {

            DaftarBelanja db = update_db.get();

            System.out.print("Masukkan judul: ");
            judul = keyb.nextLine();
            db.setJudul(judul);

            daftarBarang = db.getDaftarBarang();

            for (int i = 0; i < daftarBarang.size(); i++) {
                System.out.print("Nama Barang: "+daftarBarang.get(i).getNamaBarang() + " menjadi ");
                namaBarang = keyb.nextLine();
                System.out.print("Banyak: "+daftarBarang.get(i).getByk()+ " menjadi ");
                float jml = Float.parseFloat(keyb.nextLine());
                System.out.print("Satuan: "+daftarBarang.get(i).getSatuan()+" menjadi ");
                String satuan = keyb.nextLine();
                System.out.print("Memo: "+daftarBarang.get(i).getMemo()+" menjadi ");
                String memo = keyb.nextLine();

                daftarBarang.get(i).setNamaBarang(namaBarang);
                daftarBarang.get(i).setByk(jml);
                daftarBarang.get(i).setSatuan(satuan);
                daftarBarang.get(i).setMemo(memo);
                detilRepo.save(daftarBarang.get(i));
            }
            DaftarBelanja in_db = new DaftarBelanja();
            in_db.setJudul(judul);
            in_db.setTanggal(LocalDateTime.now());

            in_db = repo.save(db);
            optDB = repo.findById(in_db.getId());
            in_db = optDB.get();
            System.out.println("Update: ");
            System.out.println(in_db.getId()+" . "+in_db.getJudul());
            daftarBarang = in_db.getDaftarBarang();

            for (DaftarBelanjaDetil barang : daftarBarang) {
                System.out.println(barang.getNoUrut()+ " "+ barang.getNamaBarang() + " " + barang.getByk() + " " + barang.getSatuan());
            }
        } else {
            System.out.println("ID Not Found");
        }
    }

    // Menghapus objek DaftarBelanja berdasarkan ID yg diberikan.
    public void hapusObjectDaftarBelanjaBerdasarkanID() {
        Scanner keyb = new Scanner(System.in);

        String judul;
        String namaBarang;

        long id = Long.parseLong(keyb.nextLine());
        Optional<DaftarBelanja> optDB = repo.findById(id);

        List<DaftarBelanjaDetil> daftarBarang;
        List<DaftarBelanja> all = repo.findAll();

        System.out.print("Masukkan ID: ");
        id = Long.parseLong(keyb.nextLine());
        optDB = repo.findById(id);

        if (optDB.isPresent()) {
            DaftarBelanja db = optDB.get();
            System.out.println(db.getJudul());
            daftarBarang = db.getDaftarBarang();

            for (DaftarBelanjaDetil barang : daftarBarang) {
                System.out.println(barang.getNamaBarang() + " " + barang.getByk() + " " + barang.getSatuan());
                detilRepo.deleteById(barang.getId());
            }
            repo.deleteById(db.getId());
            all = repo.findAll();
            System.out.println("Current list: ");

            for (DaftarBelanja row : all) {
                System.out.println(row.getJudul());
                daftarBarang = row.getDaftarBarang();

                for (DaftarBelanjaDetil barang : daftarBarang) {
                    System.out.println(barang.getNamaBarang() + " " + barang.getByk() + " " + barang.getSatuan());
                }
            }
        } else {
            System.out.println("ID Not Found");
        }
    }

}

