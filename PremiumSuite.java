package FlexiRent;
import utilities.DateTime;

class PremiumSuite extends RentalProperty {

    private DateTime lastMaintenanceDate;
    private DateTime nextMaintenanceDate;
    private boolean maintenanceStatus;

    PremiumSuite(String propertyId, String streetNum, String streetName, String suburb) {
        super(propertyId, streetNum, streetName, suburb);
        super.numberOfBedrooms = 3;
        super.typeOfProperty = "Premium Suite";
        super.dailyRental = 554;
        super.lateFeeRate = 662;
        String datetime = DateTime.getCurrentTime();
        DateTime dateTime;
        int day = Integer.valueOf(datetime.substring(8,10));
        int month = Integer.valueOf(datetime.substring(5,7));
        int year = Integer.valueOf(datetime.substring(0,4));
        dateTime = new DateTime(day,month,year);
        this.maintenanceStatus = false;
        this.lastMaintenanceDate = dateTime;
        this.nextMaintenanceDate = new DateTime(dateTime, 10);
    }

    private void setLastMaintenanceDate(DateTime dateTime) {
        this.lastMaintenanceDate = dateTime;
    }

    boolean getMaintenanceStatus() {
        return this.maintenanceStatus;
    }

    DateTime getNextMaintenanceDate() {
        return this.nextMaintenanceDate;
    }

    @Override
    void showPropertyStatus() {
        System.out.println("Property ID: " + super.getPropertyId());
        System.out.println("Address: " + super.getStreetNum() + " " + super.getStreetName() + " " + super.getSuburb());
        System.out.println("Type: " + this.typeOfProperty);
        System.out.println("Bedroom: " + this.numberOfBedrooms);
        if (!this.maintenanceStatus) {
            System.out.println("Status: Available");
        }
        else {
            System.out.println("Status: Unavailable");
        }
        System.out.println("Last Maintenance Date: " + this.lastMaintenanceDate.getFormattedDate());
        System.out.println("Next Maintenance Date: " + this.nextMaintenanceDate.getFormattedDate());
    }

    boolean checkMaintenance(DateTime rentDate, int rentalLength) {
        if (this.maintenanceStatus) {
            System.out.println("Property under maintenance.");
            return false;
        }
        else {
            if (DateTime.diffDays(nextMaintenanceDate, rentDate) < rentalLength) {
                System.out.println("The date you rent contains maintenance date. Reservation failed.");
                return false;
            }
            else {
                return true;
            }
        }

    }

    @Override
    boolean checkAvailability(String customerId, DateTime rentDate, int rentalLength) {
        boolean previousCheck = super.checkAvailability(customerId, rentDate, rentalLength);
        boolean mCheck = this.checkMaintenance(rentDate, rentalLength);
        if (previousCheck && mCheck) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    void propertyMaintenance() {
        this.maintenanceStatus = true;
        System.out.println("This premium suite is now under maintenance, no more reservation allowed.");
    }

    @Override
    void finishMaintenance() {
        setLastMaintenanceDate(new DateTime());
        while (DateTime.diffDays(nextMaintenanceDate, lastMaintenanceDate) < 0) {
            nextMaintenanceDate = new DateTime(nextMaintenanceDate, 10);
        }
        System.out.println("Maintenance completion date: " + this.lastMaintenanceDate.getFormattedDate());
        System.out.println(this.getTypeOfProperty() + " " + this.getPropertyId() + " has all maintenance completed and ready for rent.");
        System.out.println("The next maintenance date is: " + this.nextMaintenanceDate.getFormattedDate());
        this.maintenanceStatus = false;
    }
}