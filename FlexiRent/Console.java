package FlexiRent;
import utilities.DateTime;
import java.util.*;

public class Console {
    private ArrayList<RentalProperty> properties = new ArrayList<>();

    private static DateTime inputTimeConvert(String datetime) {
        DateTime dateTime;
        int day = Integer.valueOf(datetime.substring(0,2));
        int month = Integer.valueOf(datetime.substring(3,5));
        int year = Integer.valueOf(datetime.substring(6,10));
        dateTime = new DateTime(day,month,year);
        return dateTime;
    }

    private int findProperty(String targetId) {
        int i;
        int numberOfProperties = properties.size();
        boolean ifFound = false;
        for (i = 0; i < numberOfProperties; i++) {
            if (properties.get(i).getPropertyId().toUpperCase().equals(targetId.toUpperCase())) {
                ifFound = true;
                break;
            }
        }
        if (!ifFound) {
            System.out.println("Property does not exist!");
            i = -1;
            return i;
        }
        else {
            return i;
        }
    }

    private void printAvailableProperties() {
        System.out.println("Currently available property ID: ");
        int menuLength = this.properties.size();
        for (int i = 0; i < menuLength; i++) {
            if (this.properties.get(i) instanceof PremiumSuite) {
                if (!((PremiumSuite) this.properties.get(i)).getMaintenanceStatus()) {
                    if (this.properties.get(i).getIsAvailable()) {
                        System.out.println(this.properties.get(i).getPropertyId() + " at " + String.format("%.2f", this.properties.get(i).getDailyRental()) + " per day with " + String.format("%.2f", this.properties.get(i).getLateFeeRate()) + " one excess day.");
                    }
                }
            }
            else {
                if (this.properties.get(i).getIsAvailable()) {
                    System.out.println(this.properties.get(i).getPropertyId() + " at " + String.format("%.2f", this.properties.get(i).getDailyRental()) + " per day with " + String.format("%.2f", this.properties.get(i).getLateFeeRate()) + " one excess day.");
                }
            }
        }
    }

    private void printAvailablePropertiesForMaintenance() {
        System.out.println("Currently available property ID for maintenance: ");
        int menuLength = this.properties.size();
        for (int i = 0; i < menuLength; i++) {
            if (this.properties.get(i) instanceof PremiumSuite) {
                if (!((PremiumSuite) this.properties.get(i)).getMaintenanceStatus()) {
                    if (this.properties.get(i).getIsAvailable()) {
                        System.out.println(this.properties.get(i).getPropertyId());
                    }
                }
            }
            else {
                if (this.properties.get(i).getIsAvailable()) {
                    System.out.println(this.properties.get(i).getPropertyId());
                }
            }
        }
    }

    private static void showMenu() {
        System.out.println("***** FLEXIRENT SYSTEM MENU *****");
        System.out.println();
        System.out.println("Add Property:               1");
        System.out.println("Rent Property:              2");
        System.out.println("Return Property:            3");
        System.out.println("Property Maintenance:       4");
        System.out.println("Complete Maintenance:       5");
        System.out.println("Display All Properties:     6");
        System.out.println("Exit Programme:             7");
        System.out.println("Enter your choice:");
    }

    public static void main(String args[]) {
        Console admin = new Console();
        Scanner sc = new Scanner(System.in);
        boolean ifEnd = false;
        while (!ifEnd) {
            int choice;
            showMenu();
            choice = Integer.parseInt(sc.nextLine());

            if (choice == 1) {
                String propertyId;
                String streetNum;
                String streetName;
                String suburb;
                int propertyTypeNum = 0;
                int numberOfBedrooms = 0;
                while (propertyTypeNum != 1 && propertyTypeNum != 2) {
                    System.out.println("Choose the type of the property:");
                    System.out.println("1. Apartment");
                    System.out.println("2. Premium Suite");
                    propertyTypeNum = Integer.parseInt(sc.nextLine());
                    if (propertyTypeNum != 1 && propertyTypeNum != 2) {
                        System.out.println("Please input 1 or 2");
                    }
                }
                System.out.println("Input street number:");
                streetNum = sc.nextLine();
                System.out.println("Input street name:");
                streetName = sc.nextLine();
                System.out.println("Input suburb:");
                suburb = sc.nextLine();
                if (propertyTypeNum == 1) {
                    while (numberOfBedrooms <= 0 || numberOfBedrooms >= 4) {
                        System.out.println("Input the number of bedrooms (1-3):");
                        numberOfBedrooms = Integer.parseInt(sc.nextLine());
                        if (numberOfBedrooms <= 0 || numberOfBedrooms >= 4) {
                            System.err.println("Input a valid number of bedrooms");
                        }
                    }
                }
                else {
                    numberOfBedrooms = 3;
                }
                if (propertyTypeNum == 1) {
                    propertyId = "A_" + streetNum + streetName.toUpperCase().substring(0,2) + suburb.toUpperCase().substring(0,2);
                    admin.properties.add(new Apartment(propertyId, streetNum, streetName, suburb, numberOfBedrooms));
                    System.out.println("Apartment added.");
                }
                else {
                    propertyId = "S_" + streetNum + streetName.toUpperCase().substring(0,2) + suburb.toUpperCase().substring(0,2);
                    admin.properties.add(new PremiumSuite(propertyId, streetNum, streetName, suburb));
                    System.out.println("Premium Suite added.");
                }
            }

            else if (choice == 2) {
                admin.printAvailableProperties();
                String epId;
                System.out.println("Enter property ID:");
                epId = sc.nextLine();
                int targetIndex = admin.findProperty(epId);
                if (targetIndex == -1) {
                    System.out.println("Please enter a valid property ID!");
                }
                else if (targetIndex >= 0) {
                    System.out.println("Input customer ID:");
                    String cId = sc.nextLine();
                    System.out.println("Input rent date (dd/mm/yyyy):");
                    DateTime rDate = inputTimeConvert(sc.nextLine());
                    System.out.println("Input rental length:");
                    int rLength = Integer.parseInt(sc.nextLine());
                    boolean isAvailable = admin.properties.get(targetIndex).checkAvailability(cId, rDate, rLength);
                    if (isAvailable) {
                        String rId = admin.properties.get(targetIndex).getPropertyId() + cId + rDate.getEightDigitDate();
                        admin.properties.get(targetIndex).addRentalRecord(new RentalRecord(cId, rId, rDate, rLength));
                        System.out.println("Property " + admin.properties.get(targetIndex).getPropertyId() + " reserved from " + rDate.getFormattedDate() + " to " + (new DateTime(rDate, rLength)).getFormattedDate() + " by " + cId);
                    }
                }
            }

            else if (choice == 3) {
                String targetId;
                System.out.println("Enter property ID:");
                targetId = sc.nextLine();
                int targetIndex = admin.findProperty(targetId);
                if (targetIndex >= 0) {
                    admin.properties.get(targetIndex).rtnProperty();
                }
            }

            else if (choice == 4) {
                String mtId;
                admin.printAvailablePropertiesForMaintenance();
                System.out.println("Enter property ID:");
                mtId = sc.nextLine();
                int index = admin.findProperty(mtId);
                if (index >= 0) {
                    admin.properties.get(index).propertyMaintenance();
                }
            }

            else if (choice == 5) {
                String fmtId;
                System.out.println("Enter property ID:");
                fmtId = sc.nextLine();
                int index = admin.findProperty(fmtId);
                if (index >= 0) {
                    admin.properties.get(index).finishMaintenance();
                }
            }

            else if (choice == 6) {
                int idx;
                int numberOfProperties = admin.properties.size();
                for (idx = 0; idx < numberOfProperties; idx++) {
                    admin.properties.get(idx).showPropertyStatus();
                    System.out.println("-------------------------");
                    System.out.println("Recent 10 records for " + admin.properties.get(idx).getPropertyId());
                    admin.properties.get(idx).printRentalRecord();
                    System.out.println("-------------------------");
                }
            }

            else if (choice == 7) {
                System.out.println("System Terminated!");
                ifEnd = true;
            }

            // test case
            else if (choice == 8) {
                System.out.println("Input a date (dd/mm/yyyy):");
                String newDate = sc.nextLine();
                DateTime dateTime = inputTimeConvert(newDate);
                System.out.println(dateTime.getWeekDay());
            }

            // test case
            else if (choice == 9) {
                System.out.println("Date Check");
                RentalProperty rp = new Apartment("ID", "100", "Test Street", "Test Area", 2);
                RentalRecord rr1 = new RentalRecord("Zero", "recordId1", new DateTime(1,8,2018), 6);
                RentalRecord rr2 = new RentalRecord("One", "recordId2", new DateTime(11,8,2018), 6);
                RentalRecord rr3 = new RentalRecord("Two", "recordId3", new DateTime(21,8,2018), 6);
                RentalRecord rr4 = new RentalRecord("Three", "recordId4", new DateTime(22,7,2018), 6);
                rp.addRentalRecord(rr1);
                rp.addRentalRecord(rr2);
                rp.addRentalRecord(rr3);
                rp.addRentalRecord(rr4);
                System.out.println("Customer ID");
                String cid = sc.nextLine();
                System.out.println("Rent Date");
                String pdate = sc.nextLine();
                DateTime date = inputTimeConvert(pdate);
                System.out.println("Rental Length");
                int l = Integer.parseInt(sc.nextLine());
                if (rp.checkAvailability(cid, date, l)) {
                    System.out.println("A");
                }
                else {
                    System.out.println("F");
                }
            }

        }
        System.out.println("Thank you for using FlexiRent!");
        System.exit(0);
    }
}
