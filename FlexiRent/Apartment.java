package FlexiRent;
import utilities.DateTime;
import java.util.ArrayList;
import java.util.Scanner;

class Apartment extends RentalProperty {

    private ArrayList<RentalRecord> maintenanceRecord = new ArrayList<>();

    Apartment(String propertyId, String streetNum, String streetName, String suburb, int numOfBedrooms) {
        super(propertyId, streetNum, streetName, suburb);
        super.numberOfBedrooms = numOfBedrooms;
        super.typeOfProperty = "Apartment";
        if (numOfBedrooms == 1) {
            super.dailyRental = 143;
        } else if (numOfBedrooms == 2) {
            super.dailyRental = 210;
        } else if (numOfBedrooms == 3) {
            super.dailyRental = 319;
        }
        super.lateFeeRate = super.dailyRental * 1.15;
    }

    private boolean checkMaintenanceStatus(DateTime rentDate, int rentalLength) {
        if (this.maintenanceRecord.size() == 0) {
            return true;
        } else {
            DateTime closestMaintenanceDateBefore = new DateTime();
            DateTime correspondingRtnDate = new DateTime();
            int i;
            for (i = 0; i < this.maintenanceRecord.size(); i++) {
                if ((DateTime.diffDays(rentDate, closestMaintenanceDateBefore) >= (DateTime.diffDays(rentDate, this.maintenanceRecord.get(i).getRentDate()))) && (DateTime.diffDays(rentDate, this.maintenanceRecord.get(i).getRentDate()) >= 0)) {
                    closestMaintenanceDateBefore = this.maintenanceRecord.get(i).getRentDate();
                    correspondingRtnDate = this.maintenanceRecord.get(i).getEstRtnDate();
                }
            }
            if (DateTime.diffDays(rentDate, correspondingRtnDate) >= 0) {
                // condition: check if the estimated return date conflicts (estimated return date must be earlier than the closest starting date existed)
                DateTime closestRentDateAfter = new DateTime(1, 1, 2100);
                for (i = 0; i < maintenanceRecord.size(); i++) {
                    if ((DateTime.diffDays(this.maintenanceRecord.get(i).getRentDate(), rentDate) >= 0) && (DateTime.diffDays(this.maintenanceRecord.get(i).getRentDate(), rentDate) <= DateTime.diffDays(closestRentDateAfter, rentDate))) {
                        closestRentDateAfter = this.maintenanceRecord.get(i).getRentDate();
                    }
                }
                if (DateTime.diffDays(closestRentDateAfter, rentDate) < rentalLength) {
                    System.out.println("Need to shorten the rental length.");
                    return false;
                } else {
                    return true;
                }
            } else {
                System.out.println("Need to defer the rent date.");
                return false;
            }
        }
    }

    @Override
    boolean checkAvailability(String customerId, DateTime rentDate, int rentalLength) {
        boolean previousCheck = super.checkAvailability(customerId, rentDate, rentalLength);
        boolean maintenanceCheck = this.checkMaintenanceStatus(rentDate, rentalLength);
        if (previousCheck && maintenanceCheck) {
            if (rentDate.getWeekDay() <= 5 && rentDate.getWeekDay() >= 1) {
                if (rentalLength > 28) {
                    System.out.println("Too many days.");
                    return false;
                } else if (rentalLength < 2) {
                    System.out.println("Too few days");
                    return false;
                } else {
                    return true;
                }
            } else {
                if (rentalLength > 28) {
                    System.out.println("Too many days.");
                    return false;
                } else if (rentalLength < 3) {
                    System.out.println("Too few days");
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    @Override
    void propertyMaintenance() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Input maintenance date (dd/mm/yyyy):");
        String datetime = sc.nextLine();
        int day = Integer.valueOf(datetime.substring(0,2));
        int month = Integer.valueOf(datetime.substring(3,5));
        int year = Integer.valueOf(datetime.substring(6,10));
        DateTime mDate = new DateTime(day, month, year);
        System.out.println("Enter maintenance length:");
        int mLength = Integer.parseInt(sc.nextLine());
        boolean ifM = super.checkAvailability("Admin", mDate, mLength);
        if (ifM) {
            this.maintenanceRecord.add(new RentalRecord("Admin", "Maintenance" + mDate.getEightDigitDate(), mDate, mLength));
            System.out.println("Maintenance arranged on " + mDate.getFormattedDate());
        }
        else {
            System.out.println("Cannot perform maintenance.");
        }
    }

    @Override
    void finishMaintenance() {
        Scanner sc = new Scanner(System.in);
        int k = 0;
        System.out.println("Maintenance starting dates are as follows: ");
        while (k < this.maintenanceRecord.size()) {
            if (this.maintenanceRecord.get(k).getActRtnDate() == null) {
                System.out.println(this.maintenanceRecord.get(k).getRentDate().getFormattedDate());
                k++;
            }
            else {
                k++;
            }
        }
        System.out.println("Input maintenance starting date (dd/mm/yyyy): ");
        String parseSDate = sc.nextLine();
        int sDay = Integer.valueOf(parseSDate.substring(0,2));
        int sMonth = Integer.valueOf(parseSDate.substring(3,5));
        int sYear = Integer.valueOf(parseSDate.substring(6,10));
        DateTime sDate = new DateTime(sDay, sMonth, sYear);
        int i;
        boolean ifFound = false;
        for (i = 0; i < this.maintenanceRecord.size(); i++) {
            if (this.maintenanceRecord.get(i).getRentDate().getFormattedDate().equals(sDate.getFormattedDate())) {
                ifFound = true;
                break;
            }
        }
        if (ifFound) {
            System.out.println("Input maintenance finishing date (dd/mm/yyyy): ");
            String parseFDate = sc.nextLine();
            int fDay = Integer.valueOf(parseFDate.substring(0,2));
            int fMonth = Integer.valueOf(parseFDate.substring(3,5));
            int fYear = Integer.valueOf(parseFDate.substring(6,10));
            DateTime fDate = new DateTime(fDay, fMonth, fYear);
            this.maintenanceRecord.get(i).setRtnDate(fDate);
            this.maintenanceRecord.get(i).setEstRtnDateForMaintenanceRecord(fDate);
            System.out.println("Maintenance finished on " + fDate.getFormattedDate());
        }
        else {
            System.out.println("No such maintenance record. Please check the date.");
        }
    }
}
