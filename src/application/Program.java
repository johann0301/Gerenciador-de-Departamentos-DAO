package application;

import model.entities.Department;
import model.entities.Seller;

import java.util.Date;

public class Program {
    public static void main(String[] args) {

        Department dep =  new Department(1, "Software Engineering");
        System.out.println(dep);

        Seller seller =  new Seller(2, "Johann", "johann.bezerra27@gmail.com", new Date(), 10000.00, dep);

        System.out.println(seller);
    }
}
