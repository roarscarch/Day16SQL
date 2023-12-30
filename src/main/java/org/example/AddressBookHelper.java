package org.example;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class AddressBookHelper {
    private Map<String, AddressBook> addressBooks;
    private Scanner scanner;

    public AddressBookHelper() {
        this.addressBooks = new HashMap<>();
        this.scanner = new Scanner(System.in);
    }

    public void addAddressBook(String addressBookName) {
        AddressBook newAddressBook = new AddressBook(addressBookName);
        addressBooks.put(addressBookName, newAddressBook);
        System.out.println("Address Book '" + addressBookName + "' created successfully.");
    }

    public AddressBook accessAddressBook(String addressBookName) {
        return addressBooks.get(addressBookName);
    }

    public void viewAllAddressBooks() {
        System.out.println("Available Address Books:");
        for (String addressBookName : addressBooks.keySet()) {
            System.out.println(addressBookName);
        }
    }

    public void searchContactsByCity(String city) {
        for (AddressBook addressBook : addressBooks.values()) {
            System.out.println("Contacts in " + addressBook.getName() + " with city " + city + ":");
            addressBook.searchByCityOrState(city).forEach(System.out::println);
        }
    }

    public void searchContactsByState(String state) {
        for (AddressBook addressBook : addressBooks.values()) {
            System.out.println("Contacts in " + addressBook.getName() + " with state " + state + ":");
            addressBook.searchByCityOrState(state).forEach(System.out::println);
        }
    }

    public void countContactsByCity(String city) {
        int totalCount = 0;
        for (AddressBook addressBook : addressBooks.values()) {
            int count = addressBook.countContactsByCityOrState(city);
            System.out.println("Contacts in " + addressBook.getName() + " with city " + city + ": " + count);
            totalCount += count;
        }
        System.out.println("Total count across all Address Books: " + totalCount);
    }

    public void countContactsByState(String state) {
        int totalCount = 0;
        for (AddressBook addressBook : addressBooks.values()) {
            int count = addressBook.countContactsByCityOrState(state);
            System.out.println("Contacts in " + addressBook.getName() + " with state " + state + ": " + count);
            totalCount += count;
        }
        System.out.println("Total count across all Address Books: " + totalCount);
    }

    public void sortAddressBook(AddressBook addressBook) {
        System.out.println("Choose field to sort by: ");
        System.out.println("1. Name");
        System.out.println("2. City");
        System.out.println("3. State");
        System.out.println("4. Zip");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                addressBook.sortByName();
                break;
            case 2:
                addressBook.sortByCity();
                break;
            case 3:
                addressBook.sortByState();
                break;
            case 4:
                addressBook.sortByZip();
                break;
            default:
                System.out.println("Invalid choice");
        }
        addressBook.displayContacts();
    }

    public void writeToFile(AddressBook addressBook){
        String path = addressBook.getName();
        List<Contact> contacts = addressBook.getContacts();
        int num_col = 8;
        int num_row = contacts.size();
        String [][] data = new String[num_row][num_col];

        for(int i = 0;i<num_row;i++){
            Contact contact = contacts.get(i);
            data[i][0] = contact.getFirstName();
            data[i][1] = contact.getLastName();
            data[i][2] = contact.getAddress();
            data[i][3] = contact.getCity();
            data[i][4] = contact.getState();
            data[i][5] = contact.getZip();
            data[i][6] = contact.getPhoneNumber();
            data[i][7] = contact.getEmail();
        }

        try(CSVWriter writer = new CSVWriter(new FileWriter(path,false))){
            String[] header = {"First Name", "Last Name", "Address", "City", "State", "Zip","Phone number", "Email"};
            writer.writeNext(header);
            for(int i = 0;i<data.length;i++){
                writer.writeNext(data[i]);
            }
            System.out.println("Address Book imported to csv file");
        }
        catch(IOException exception){
            exception.printStackTrace();
        }

    }

    public void addContact(AddressBook addressBook) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter City: ");
        String city = scanner.nextLine();
        System.out.print("Enter State: ");
        String state = scanner.nextLine();
        System.out.print("Enter Zip: ");
        String zip = scanner.nextLine();
        System.out.print("Enter Phone Number: ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Type: ");
        String type = scanner.nextLine();

        Contact newContact = new Contact(firstName, lastName, address, city, state, zip, phoneNumber, email);

        if (!addressBook.isDuplicate(newContact)) {
            addressBook.addContact(newContact,type);
            System.out.println("Contact added successfully.");
        } else {
            System.out.println("Duplicate entry. Contact not added.");
        }
    }

    public void readfromFile(AddressBook addressBook){
        String path = addressBook.getName();
        try(CSVReader reader = new CSVReader(new FileReader(path))){
            String[] header = reader.readNext();
            String[] line;
            while((line = reader.readNext())!=null){
                Contact contact = new Contact(line[0], line[1], line[2], line[3], line[4], line[5], line[6], line[7]);
                if (!addressBook.isDuplicate(contact)) {
                    addressBook.addContact(contact,null);
                }
            }
            System.out.println("Contact read successfully from file");

        }
        catch(IOException exception){
            exception.printStackTrace();
        }
        catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public void writetoJsonFile(AddressBook addressBook){
        String path = addressBook.getName() + "json";
        List<Contact> contacts = addressBook.getContacts();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try(FileWriter writer = new FileWriter(path,false)){
            gson.toJson(contacts,writer);
            System.out.println("Addressbook added to Json File");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void readtoJsonFile(AddressBook addressBook){
        String path = addressBook.getName() + "json";
        List<Contact> contacts;
        Gson gson = new Gson();
        try(FileReader reader = new FileReader(path)){
            java.lang.reflect.Type contactListType = new TypeToken<List<Contact>>() {}.getType();
            contacts = gson.fromJson(reader,contactListType);
            for(int i = 0;i<contacts.size();i++){
                Contact temp = new Contact();
                temp = contacts.get(i);
                if(!addressBook.isDuplicate(temp)){
                    addressBook.addContact(temp,null);
                }
            }
            System.out.println("Contacts from Json File read successfully");
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }


}
