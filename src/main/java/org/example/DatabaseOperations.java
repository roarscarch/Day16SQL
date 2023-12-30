package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseOperations {
    public static Connection getConnection(){
        try{
            String url = "jdbc:mysql://localhost:3306/practice";
            String user = "root";
            String password = "password";
            return DriverManager.getConnection(url, user, password);

        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public static List<Contact> read_from_db(){
        List<Contact> contacts = new ArrayList<>();
        String sqlQuery = "select * from AddressBook_new ab inner join persons pd on ab.person_id = pd.person_id inner join address ad on ab.address_id = ad.address_id;";
        try(Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery)){
            while(resultSet.next()){
                String firstname = resultSet.getString("first_name");
                String lastname = resultSet.getString("last_name");
                String address = resultSet.getString("lane");
                String city = resultSet.getString("city");
                String state = resultSet.getString("state");
                int zipCodeInt = resultSet.getInt("zip_code");
                String zip_code = String.valueOf(zipCodeInt);
                String phonenumber = resultSet.getString("phone_number");
                String email = resultSet.getString("email");
                Contact contact = new Contact(firstname, lastname, address, city, state, zip_code, phonenumber, email);
                contacts.add(contact);
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return contacts;
    }

    public static void write_to_db(String type, Contact contact) {
        // Assuming you have tables: persons, address, AddressBook_new
        String sqlInsertPerson = "insert into persons (first_name, last_name, phone_number, email) values (?, ?, ?, ?);";
        String sqlInsertAddress = "insert into address (lane, city, state, zip_code) values (?, ?, ?, ?);";
        String sqlInsertAddressBookNew = "insert into AddressBook_new (person_id, address_id, email, type,date_added) values (?, ?, ?, ?,date(now()));";

        try {
            // Insert into persons table
            Connection connection = getConnection();
            try (PreparedStatement insertPersonStatement = connection.prepareStatement(sqlInsertPerson)) {
                insertPersonStatement.setString(1, contact.getFirstName());
                insertPersonStatement.setString(2, contact.getLastName());
                insertPersonStatement.setString(3, contact.getPhoneNumber());
                insertPersonStatement.setString(4, contact.getEmail());
                insertPersonStatement.executeUpdate();
            }

            // Insert into address table
            try (PreparedStatement insertAddressStatement = connection.prepareStatement(sqlInsertAddress)) {
                insertAddressStatement.setString(1, contact.getAddress());
                insertAddressStatement.setString(2, contact.getCity());
                insertAddressStatement.setString(3, contact.getState());
                insertAddressStatement.setInt(4,  Integer.parseInt(contact.getZip()));
                insertAddressStatement.executeUpdate();
            }


            int personId, addressId;
            try (PreparedStatement getLastPersonId = connection.prepareStatement("select last_insert_id()");
                 PreparedStatement getLastAddressId = connection.prepareStatement("select last_insert_id()"))
            {

                try (ResultSet resultSet = getLastPersonId.executeQuery()) {
                    if (resultSet.next()) {
                        personId = resultSet.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve the last person ID");
                    }
                }

                try (ResultSet resultSet = getLastAddressId.executeQuery()) {
                    if (resultSet.next()) {
                        addressId = resultSet.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve the last address ID");
                    }
                }
            }

            // Insert into AddressBook_new table
            try (PreparedStatement insertAddressBookNewStatement = connection.prepareStatement(sqlInsertAddressBookNew)) {
                insertAddressBookNewStatement.setInt(1, personId);
                insertAddressBookNewStatement.setInt(2, addressId);
                insertAddressBookNewStatement.setString(3, contact.getEmail());
                insertAddressBookNewStatement.setString(4, type);
                insertAddressBookNewStatement.executeUpdate();
            }

            System.out.println("Data added to the database");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void edit_person(String column, int personId, String new_val){
        String sqlQuery = "update persons set "+ column + " = ? where person_id = ?";
        try( Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)){
            statement.setString(1, new_val);
            statement.setInt(2, personId);
            statement.executeUpdate();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static void edit_address(String column, int addressId, String new_val){
        String sqlQuery = "update address set "+ column + " = ? where address_id = ?";
        try( Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)){
            statement.setString(1, new_val);
            statement.setInt(2, addressId);
            statement.executeUpdate();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static int get_person_id(String first_name){
        String sqlQuery = "select person_id from persons where first_name = ?";
        int personID = -1;
        try( Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)){
            statement.setString(1, first_name);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                personID = resultSet.getInt("person_id");
            }
            return personID;

        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return personID;
        }
    }

    public static int get_address_id(String first_name){
        int personID = get_person_id(first_name);
        String sqlQuery = "select address_id from AddressBook_new where person_id = ?";
        int addressID = -1;
        try( Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)){
            statement.setInt(1, personID);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                addressID = resultSet.getInt("address_id");
            }
            return addressID;

        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return addressID;
        }
    }

    public static List<Contact> get_contact_added_between_dates(String start, String end){
        String sqlQuery = "select * from AddressBook_new ab inner join persons pd on ab.person_id = pd.person_id inner join address ad on ab.address_id = ad.address_id where date_added between cast(? as date) and cast(? as date);";
        List<Contact> contacts = new ArrayList<>();
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlQuery)){
            statement.setString(1, start);
            statement.setString(2, end);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                String firstname = resultSet.getString("first_name");
                String lastname = resultSet.getString("last_name");
                String address = resultSet.getString("lane");
                String city = resultSet.getString("city");
                String state = resultSet.getString("state");
                int zipCodeInt = resultSet.getInt("zip_code");
                String zip_code = String.valueOf(zipCodeInt);
                String phonenumber = resultSet.getString("phone_number");
                String email = resultSet.getString("email");
                Contact contact = new Contact(firstname, lastname, address, city, state, zip_code, phonenumber, email);
                contacts.add(contact);
            }
            return contacts;
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return contacts;
        }
    }

    public static void add_column_date_added(){
        String sqlQuery = "alter table AddressBook_new add column date_added DATE default null;";
        try(Connection connection = getConnection();
            Statement statement = connection.createStatement()){
            statement.executeUpdate(sqlQuery);
            System.out.println("Column of date_added added successfully!");
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static int countContacts_by_city(String city){
        String sqlQuery = "select count(distinct ab.person_id) from AddressBook_new ab inner join persons pd on ab.person_id = pd.person_id inner join address ad on ab.address_id = ad.address_id where city = ? ;";
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlQuery)){
            statement.setString(1, city);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                return resultSet.getInt(1);
            }

        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return 0;
        }
        return 0;
    }

    public static int countContacts_by_state(String state){
        String sqlQuery = "select count(distinct ab.person_id) from AddressBook_new ab inner join persons pd on ab.person_id = pd.person_id inner join address ad on ab.address_id = ad.address_id where state = ?;";
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlQuery)){
            statement.setString(1, state);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                return resultSet.getInt(1);
            }

        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return 0;
        }
        return 0;
    }
}

