package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SellerDaoJDBC implements SellerDao {

    private Connection con;

    public SellerDaoJDBC(Connection con) {
        this.con = con;
    }

    public SellerDaoJDBC() {

    }

    @Override
    public void insert(Seller dep) {

    }

    @Override
    public void update(Seller dep) {

    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(
                    "SELECT seller.*,department.Name as DepName "
                    + "FROM seller INNER JOIN department "
                    + "ON seller.DepartmentId = department.ID "
                    + "WHERE seller.id = ? ");

            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Department dep = new Department();
                dep.setId(rs.getInt("DepartmentId"));
                dep.setName(rs.getString("DepName"));

                Seller seller = new Seller();
                seller.setId(rs.getInt("Id"));
                seller.setName(rs.getString("Name"));
                seller.setEmail(rs.getString("Email"));
                seller.setBaseSalary(rs.getDouble("BaseSalary"));
                seller.setBirthDate(rs.getDate("BirthDate"));
                seller.setDepartment(dep);
                return seller;
            }
            return null;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatment(stmt);
            DB.closeResultSet(rs);
        }

    }

    @Override
    public List<Seller> findAll() {
        return List.of();
    }
}
