package by.bsu.audioservice.dao.impl;

import by.bsu.audioservice.dao.AbstractUserDAO;
import by.bsu.audioservice.entity.Role;
import by.bsu.audioservice.entity.User;
import by.bsu.audioservice.exception.DAOException;
import by.bsu.audioservice.pool.ConnectionPool;
import by.bsu.audioservice.pool.ProxyConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by 7 on 24.07.2016.
 */
public class UserDAO extends AbstractUserDAO {
    private static final String SQL_SELECT_USER_BY_EMAIL_AND_PASSWORD = "SELECT user_id, first_name, last_name, email, password," +
            " phone_number, role FROM users WHERE email = ? and password = ?;";
    private static final String SQL_SELECT_USER_BY_EMAIL = "SELECT user_id, first_name, last_name, email, password, " +
            "phone_number, role FROM users WHERE email = ?;";
    private static final String SQL_INSERT_USER = "INSERT INTO users (first_name, last_name, email, password, phone_number, role)" +
            " VALUES (?, ?, ?, ?, ?, ?);";
    private static final String SQL_SELECT_USER_BY_USER_ID = "SELECT user_id, first_name, last_name, email, password," +
            " phone_number, role FROM users WHERE user_id = ?;";
    private static final String SQL_EDIT_USER_SETTINGS = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone_number = ?" +
            " WHERE user_id = ?";
    private static final String SQL_CHANGE_USER_PASSWORD = "UPDATE users SET password = ? WHERE user_id = ?";
    private static final String SQL_SELECT_ALL_USERS = "SELECT u.user_id, u.first_name, u.last_name, u.email, " +
            "u.password, u.phone_number, u.role FROM users AS u;";

    private static final String USER_ID = "user_id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String ROLE = "role";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    private static UserDAO instance = UserDAO.getInstance();

    private static final Logger LOGGER = LogManager.getLogger(UserDAO.class);
    private UserDAO(){
    }

    public static UserDAO getInstance(){
        if (instance == null){
            instance = new UserDAO();
        }
        return instance;
    }

    @Override
    public User findUserByLoginAndPassword(String email, String password) throws DAOException {
        User user = null;
        boolean flag = false;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
            PreparedStatement st = cn.prepareStatement(SQL_SELECT_USER_BY_EMAIL_AND_PASSWORD);) {
            st.setString(1, email);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();
            flag = rs.next();
            if (flag){
                Long id = rs.getLong(USER_ID);
                String firstName = rs.getString(FIRST_NAME);
                String lastName = rs.getString(LAST_NAME);
                String phoneNumber = rs.getString(PHONE_NUMBER);
                Role role = Role.valueOf(rs.getString(ROLE).toUpperCase());
                user = new User(id, firstName,lastName, email, password, phoneNumber, role);
            }
        } catch (SQLException e) {
            LOGGER.error("SQLException", e);
            throw new DAOException(e);
        }

        return user;
    }

    @Override
    public User findUserByEmail(String email) throws DAOException {
        User user = null;
        boolean flag = false;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
            PreparedStatement st = cn.prepareStatement(SQL_SELECT_USER_BY_EMAIL);){
            st.setString(1, email);
            ResultSet rs = st.executeQuery();
            flag = rs.next();
            if (flag){
                Long id = rs.getLong(USER_ID);
                String firstName = rs.getString(FIRST_NAME);
                String lastName = rs.getString(LAST_NAME);
                String phoneNumber = rs.getString(PHONE_NUMBER);
                String password = rs.getString(PASSWORD);
                Role role = Role.valueOf(rs.getString(ROLE).toUpperCase());
                user = new User(id, firstName,lastName, email, password, phoneNumber, role);
            }
        } catch (SQLException e) {
            throw new DAOException("SQLException in DAO layer!", e);
        }
        return user;
    }

    @Override
    public boolean addUser(User user) throws DAOException {
        boolean flag = false;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
            PreparedStatement st = cn.prepareStatement(SQL_INSERT_USER);) {
            st.setString(1, user.getFirstName());
            st.setString(2, user.getSecondName());
            st.setString(3, user.getEmail());
            st.setString(4, user.getPassword());
            st.setString(5, user.getPhoneNumber());
            st.setString(6, user.getRole().toString().toLowerCase());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            throw new DAOException("SQLException in DAO layer!", e);
        }
        return flag;
    }

    @Override
    public User findUserByUserID(Long id) throws DAOException {
        User user = null;
        boolean flag = false;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
            PreparedStatement st = cn.prepareStatement(SQL_SELECT_USER_BY_USER_ID)){
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            flag = rs.next();
            if (flag){
                String email = rs.getString(EMAIL);
                String firstName = rs.getString(FIRST_NAME);
                String lastName = rs.getString(LAST_NAME);
                String phoneNumber = rs.getString(PHONE_NUMBER);
                String password = rs.getString(PASSWORD);
                Role role = Role.valueOf(rs.getString(ROLE).toUpperCase());
                user = new User(id, firstName,lastName, email, password, phoneNumber, role);
            }
        } catch (SQLException e) {
            throw new DAOException("SQLException in DAO layer!", e);
        }
        return user;
    }

    @Override
    public boolean editUserSettings(User user) throws DAOException {
        boolean flag = false;
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
        PreparedStatement st = cn.prepareStatement(SQL_EDIT_USER_SETTINGS)){
            st.setString(1, user.getFirstName());
            st.setString(2, user.getSecondName());
            st.setString(3, user.getEmail());
            st.setString(4, user.getPhoneNumber());
            st.setLong(5, user.getUserId());
            st.executeUpdate();
            flag = true;
        } catch (SQLException e) {
            throw new DAOException("SQLException in UserDAO (editUserSettings)!", e);
        }
        return flag;
    }

    @Override
    public boolean changeUserPassword(User user, String newPassword) throws DAOException {
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
        PreparedStatement st = cn.prepareStatement(SQL_CHANGE_USER_PASSWORD)){
            st.setString(1, newPassword);
            st.setLong(2, user.getUserId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("SQLException");
        }
        return true;
    }

    @Override
    public ArrayList<User> takeAllUsers() throws DAOException {
        ArrayList<User> users = new ArrayList<User>();
        try(ProxyConnection cn = ConnectionPool.getInstance().takeConnection();
        PreparedStatement st = cn.prepareStatement(SQL_SELECT_ALL_USERS)){
            ResultSet rs = st.executeQuery();
            User user = null;
            while(rs.next()){
                Role role = Role.valueOf(rs.getString(ROLE).toUpperCase());
                if (role != Role.ADMIN) {
                    Long id = rs.getLong(USER_ID);
                    String email = rs.getString(EMAIL);
                    String firstName = rs.getString(FIRST_NAME);
                    String lastName = rs.getString(LAST_NAME);
                    String phoneNumber = rs.getString(PHONE_NUMBER);
                    String password = rs.getString(PASSWORD);
                    user = new User(id, firstName, lastName, email, password, phoneNumber, role);
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("SQLException id UserDAO(takeAllUsers)", e);
        }
        return users;
    }
}


