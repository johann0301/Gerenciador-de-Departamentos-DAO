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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                Department dep = instantiateDepartment(rs);
                Seller seller = instantiateDepartment(rs,dep);
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

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("DepartmentId"));
        dep.setName(rs.getString("DepName"));
        return dep;
    }

    private Seller instantiateDepartment(ResultSet rs, Department dep) throws SQLException {
        Seller seller = new Seller();
        seller.setId(rs.getInt("Id"));
        seller.setName(rs.getString("Name"));
        seller.setEmail(rs.getString("Email"));
        seller.setBaseSalary(rs.getDouble("BaseSalary"));
        seller.setBirthDate(rs.getDate("BirthDate"));
        seller.setDepartment(dep);
        return seller;
    }

    @Override
    public List<Seller> findAll() {
        return List.of();
    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(
                    "SELECT seller.*,department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.ID "
                            + "WHERE DepartmentId = ? "
                            + "ORDER BY Name");

            stmt.setInt(1, department.getId());
            rs = stmt.executeQuery();

            List<Seller> sellersList = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()) {

                Department depart = map.get(rs.getInt("DepartmentId"));
                
                if (depart == null) {
                    depart = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), depart);
                }

                Seller seller = instantiateDepartment(rs,depart);
                sellersList.add(seller);
            }
            return sellersList;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatment(stmt);
            DB.closeResultSet(rs);
        }
    }
}
