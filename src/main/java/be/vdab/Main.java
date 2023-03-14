package be.vdab;

import be.vdab.repositories.BierRepository;
import be.vdab.repositories.BrouwerRepository;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {


        var repository = new BierRepository();
        var scanner = new Scanner(System.in);
        System.out.println("Geef een maand op (getal tussen 1 en 12:");
        var maand = scanner.nextInt();
        while (maand <1||maand>12){
            System.out.println("De opgegeven maand klopt niet, probeer opnieuw");
            maand=scanner.nextInt();
        }
        try {
            repository.bierenVanEenmaand(maand).forEach(System.out::println);



        } catch (
                SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }
}