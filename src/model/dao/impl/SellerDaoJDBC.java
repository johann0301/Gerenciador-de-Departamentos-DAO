package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
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
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(
                        "INSERT INTO seller " +
                            "(Name, Email, BirthDate, BaseSalary, DepartmentId) " +
                            "VALUES " +
                            "(?,?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1,dep.getName());
            stmt.setString(2,dep.getEmail());
            stmt.setDate(3,new java.sql.Date(dep.getBirthDate().getTime()));
            stmt.setDouble(4,dep.getBaseSalary());
            stmt.setInt(5,dep.getDepartment().getId());

            int rowsAffecteds = stmt.executeUpdate();
            if (rowsAffecteds > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    dep.setId(id);
                }
                DB.closeResultSet(rs);
            }
            else{
                throw new DbException("Unexpected error while inserting seller");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatment(stmt);
        }
    }


    @Override
    public void update(Seller dep) {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(
                    "UPDATE seller " +
                            "SET Name = ?, Email = ?,  BirthDate = ?, BaseSalary = ?, DepartmentId = ? " +
                            "WHERE Id = ?",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1,dep.getName());
            stmt.setString(2,dep.getEmail());
            stmt.setDate(3,new java.sql.Date(dep.getBirthDate().getTime()));
            stmt.setDouble(4,dep.getBaseSalary());
            stmt.setInt(5,dep.getDepartment().getId());
            stmt.setInt(6,dep.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatment(stmt);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement("DELETE FROM seller WHERE Id = ?");

            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new DbException("Unexpected error while deleting seller");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally{
            DB.closeStatment(stmt);
        }
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
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(
                    "SELECT seller.*,department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.ID "
                            + "ORDER BY Name");

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
