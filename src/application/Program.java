package application;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.dao.impl.SellerDaoJDBC;
import model.entities.Department;
import model.entities.Seller;

import java.util.Date;

public class Program {
    public static void main(String[] args) {

        Department dep =  new Department(1, "Software Engineering");

        Seller seller =  new Seller(2, "Johann", "johann.bezerra27@gmail.com", new Date(), 10000.00, dep);

        SellerDao sellerDao = DaoFactory.createSellerDao();

        System.out.println(dep);
        System.out.println(seller);
        System.out.println(sellerDao);
    }
}
