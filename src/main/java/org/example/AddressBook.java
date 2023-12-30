package org.example;

import java.util.ArrayList;
import java.util.List;


public class AddressBook {
    private String name;
    private List<Contact> contacts;

    public AddressBook(String name) {
        this.name = name;
        this.contacts = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public void addContact(Contact contact, String Type) {
        contacts.add(contact);
        DatabaseOperations.write_to_db(Type,contact);
    }

    public void editContact(String firstName, Contact updatedContact) {
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getFirstName().equals(firstName)) {
                contacts.set(i, updatedContact);
                return;
            }
        }
        System.out.println("Contact with first name " + firstName + " not found.");
    }

    public void deleteContact(String firstName) {
        contacts.removeIf(contact -> contact.getFirstName().equals(firstName));
    }

    public boolean isDuplicate(Contact contact) {
        return contacts.stream().anyMatch(c -> c.getFirstName().equals(contact.getFirstName()));
    }

    public List<Contact> searchByCityOrState(String cityOrState) {
        List<Contact> result = new ArrayList<>();
        for (Contact contact : contacts) {
            if (contact.getCity().equalsIgnoreCase(cityOrState) || contact.getState().equalsIgnoreCase(cityOrState)) {
                result.add(contact);
            }
        }
        return result;
    }

    public int countContactsByCityOrState(String cityOrState) {
        int count = 0;
        for (Contact contact : contacts) {
            if (contact.getCity().equalsIgnoreCase(cityOrState) || contact.getState().equalsIgnoreCase(cityOrState)) {
                count++;
            }
        }
        return count;
    }

    public void sortByName() {
        contacts.sort((c1, c2) -> c1.getFirstName().compareToIgnoreCase(c2.getFirstName()));
    }

    public void sortByCity() {
        contacts.sort((c1, c2) -> c1.getCity().compareToIgnoreCase(c2.getCity()));
    }

    public void sortByState() {
        contacts.sort((c1, c2) -> c1.getState().compareToIgnoreCase(c2.getState()));
    }

    public void sortByZip() {
        contacts.sort((c1, c2) -> c1.getZip().compareToIgnoreCase(c2.getZip()));
    }

    public void displayContacts() {
        System.out.println("Contacts in Address Book: " + name);
        for (Contact contact : contacts) {
            System.out.println(contact);
        }
    }
    public List<Contact> getContacts() {
        return contacts;
    }
}