package FlexiRent;
import utilities.DateTime;
import java.util.ArrayList;
import java.util.Scanner;

abstract class RentalProperty {
    private String propertyId;
    private String streetNum;
    private String streetName;
    private String suburb;
    private boolean isAvailable;
    protected String typeOfProperty = "New Property";
    private RentalRecord[] rentalRecord = new RentalRecord[10];
    protected int numberOfBedrooms;
    protected boolean underMaintenance;
    protected double dailyRental;
    protected double lateFeeRate;
    private ArrayList<RentalRecord> notCheckIn = new ArrayList<>();

    RentalProperty(String propertyId, String streetNum, String streetName, String suburb) {
        this.propertyId = propertyId;
        this.streetNum = streetNum;
        this.streetName = streetName;
        this.suburb = suburb;
        this.isAvailable = true;
        this.numberOfBedrooms = 0;
        this.dailyRental = 0;
        this.lateFeeRate = 0;
        this.underMaintenance = false;
    }

    double getDailyRental() {
        return this.dailyRental;
    }

    double getLateFeeRate() {
        return this.lateFeeRate;
    }

    String getPropertyId() {
        return this.propertyId;
    }

    String getStreetNum() {
        return this.streetNum;
    }

    String getStreetName() {
        return this.streetName;
    }

    String getSuburb() {
        return this.suburb;
    }

    RentalRecord[] getRentalRecord() {
        return this.rentalRecord;
    }

    boolean getIsAvailable() {
        return this.isAvailable;
    }

    String getTypeOfProperty() {
        return this.typeOfProperty;
    }

    private int getNumberOfRecords() {
        int numOfRecords = 0;
        int i;
        for (i = 0; i < 10; i++) {
            if (this.rentalRecord[i] != null) {
                numOfRecords += 1;
            }
            else {
                break;
            }
        }
        return numOfRecords;
    }

    private boolean checkRentDate(DateTime rentDate) {
        // System.out.println("Need to rent for today and future.");
        return (DateTime.diffDays(rentDate, new DateTime()) >= 0);
    }

    boolean checkAvailability(String customerId, DateTime rentDate, int rentalLength) {
        if (this.checkRentDate(rentDate)) {
            int numOfRecords = this.getNumberOfRecords();
            if (numOfRecords == 0) {
                return true;
            }
            else {
                // condition: check if the rentDate conflicts (rent date must be later than the closest return date existed)
                DateTime closestRentDateBefore = new DateTime();
                DateTime correspondingRtnDate = new DateTime();
                int i;
                for (i = 0; i < numOfRecords; i++) {
                    if ((DateTime.diffDays(rentDate, closestRentDateBefore) >= (DateTime.diffDays(rentDate, this.rentalRecord[i].getRentDate()))) && (DateTime.diffDays(rentDate, this.rentalRecord[i].getRentDate()) >= 0)) {
                        closestRentDateBefore = this.rentalRecord[i].getRentDate();
                        correspondingRtnDate = this.rentalRecord[i].getEstRtnDate();
                    }
                }
                if (DateTime.diffDays(rentDate, correspondingRtnDate) >= 0) {
                    // condition: check if the estimated return date conflicts (estimated return date must be earlier than the closest starting date existed)
                    DateTime closestRentDateAfter = new DateTime(1, 1, 2100);
                    for (i = 0; i < numOfRecords; i++) {
                        if ((DateTime.diffDays(this.rentalRecord[i].getRentDate(), rentDate) >= 0) && (DateTime.diffDays(this.rentalRecord[i].getRentDate(), rentDate) <= DateTime.diffDays(closestRentDateAfter, rentDate))) {
                            closestRentDateAfter = this.rentalRecord[i].getRentDate();
                        }
                    }
                    if (DateTime.diffDays(closestRentDateAfter, rentDate) < rentalLength) {
                        System.out.println("Need to shorten the length.");
                        return false;
                    }
                    else {
                        return true;
                    }
                }
                else {
                    System.out.println("Need to defer the date.");
                    return false;
                }
            }
        }
        else {
            System.out.println("Need to reserve for today and future.");
            return false;
        }
    }

    void printRentalRecord() {
        int i;
        for (i = 0; i < 10; i++) {
            if (this.rentalRecord[i] != null) {
                System.out.println("---------- " + (i + 1) + " ----------");
                this.rentalRecord[i].printRecord();
            }
            else {
                break;
            }
        }
    }

    void addRentalRecord(RentalRecord rr) {
        int i = 9;
        while (i >= 1) {
            this.rentalRecord[i] = this.rentalRecord[i - 1];
            i--;
        }
        this.rentalRecord[0] = rr;
    }

    void showPropertyStatus() {
        System.out.println("Property ID: " + this.propertyId);
        System.out.println("Address: " + this.streetNum + " " + this.streetName + " " + this.suburb);
        System.out.println("Type: " + this.typeOfProperty);
        System.out.println("Bedroom: " + this.numberOfBedrooms);
        if (this.isAvailable) {
            System.out.println("Status: Available");
        }
        else {
            System.out.println("Status: Unavailable");
        }
    }

    private int findRentalRecord(String recordId) {
        int end = this.getNumberOfRecords();
        int i;
        boolean ifFound = false;
        for (i = 0; i < end; i++) {
            if (recordId.toUpperCase().equals(this.rentalRecord[i].getRecordId().toUpperCase())) {
                ifFound = true;
                break;
            }
        }
        if (ifFound) {
            return i;
        }
        else {
            System.out.println("Record does not exist!");
            i = -1;
            return i;
        }
    }

    void rtnProperty() {
        System.out.println("The available records are as follows:");
        int i;
        int end = this.getNumberOfRecords();
        Scanner sc = new Scanner(System.in);
        for (i = 0; i < end; i++) {
            if (this.rentalRecord[i].getInUse()) {
                System.out.println(this.rentalRecord[i].getRecordId());
            }
        }
        System.out.println("Input the record ID you are going to return:");
        String targetId = sc.nextLine();
        int targetIndex = this.findRentalRecord(targetId);
        if (targetIndex >= 0) {
            DateTime rtnDate;
            System.out.println("Enter the returning date (dd/mm/yyyy):");
            String parseDate = sc.nextLine();
            int day = Integer.valueOf(parseDate.substring(0,2));
            int month = Integer.valueOf(parseDate.substring(3,5));
            int year = Integer.valueOf(parseDate.substring(6,10));
            rtnDate = new DateTime(day, month, year);
            if (DateTime.diffDays(rtnDate, this.rentalRecord[targetIndex].getRentDate()) >= 0) {
                this.rentalRecord[targetIndex].setRtnDate(rtnDate);
                this.rentalRecord[targetIndex].finishRecord();
                this.rentalRecord[targetIndex].setRentalFee(this.dailyRental);
                this.rentalRecord[targetIndex].setLateFee(this.lateFeeRate);
                this.rentalRecord[targetIndex].printRecord();
                this.showPropertyStatus();
            }
            else {
                this.notCheckIn.add(this.rentalRecord[targetIndex]);
                int j;
                for (j = targetIndex; j < 9; j++) {
                    this.rentalRecord[j] = this.rentalRecord[j + 1];
                }
            }
        }
        else {
            System.out.println("Return failed.");
        }
    }

    void propertyMaintenance() {}

    void finishMaintenance() {}

/*
    ArrayList<RentalRecord> findRentalRecord(String customerId) {
        ArrayList<RentalRecord> result = new ArrayList<>();
        int numOfRecords = 0;
        int i;
        for (i = 0; i < 10; i++) {
            if (this.rentalRecord[i] != null) {
                numOfRecords += 1;
            }
            else {
                break;
            }
        }
        int j = 0;
        while (j < numOfRecords) {
            if (customerId.equals(this.rentalRecord[j].getCustomerId())) {
                result.add(this.rentalRecord[j]);
            }
            j++;
        }
        return result;
    }
    */
} 
