package FlexiRent;
import utilities.DateTime;

public class RentalRecord {
    private String customerId;
    private String recordId;
    private DateTime rentDate;
    private DateTime estRtnDate;
    private DateTime actRtnDate;
    private double rentalFee;
    private double lateFee;
    private boolean inUse;

    RentalRecord(String customerId, String recordId, DateTime rentDate, int rentalLength) {
        this.customerId = customerId;
        this.recordId = recordId;
        this.rentDate = rentDate;
        this.estRtnDate = new DateTime(rentDate, rentalLength);
        this.actRtnDate = null;
        this.inUse = true;
    }

    // If a record is turned off, it still can be showed but can not be returned.
    void turnOffRecord() {
        this.inUse = false;
    }

    boolean getInUse() {
        return this.inUse;
    }

    String getCustomerId() {
        return this.customerId;
    }

    public DateTime getActRtnDate() {
        return this.actRtnDate;
    }

    DateTime getRentDate() {
        return this.rentDate;
    }

    DateTime getEstRtnDate() {
        return this.estRtnDate;
    }

    @Override
    public String toString() {
        return recordId + ":" + rentDate + ":" + estRtnDate + ":" + actRtnDate + ":" + rentalFee + ":" + lateFee;
    }

    void setRtnDate(DateTime rtnDate) {
        this.actRtnDate = rtnDate;
    }

    String getRecordId() {
        return this.recordId;
    }

    void setRentalFee(double feeRate) {
        int days = DateTime.diffDays(this.estRtnDate, this.rentDate);
        this.rentalFee = feeRate * days;
    }

    void setLateFee(double feeRate) {
        int days = DateTime.diffDays(this.actRtnDate, this.estRtnDate);
        if (days >= 0) {
            this.lateFee = feeRate * days;
        }
        else {
            this.lateFee = 0;
        }
    }

    void printRecord() {
        System.out.println("Record ID: " + this.recordId);
        System.out.println("Rent Date: " + this.rentDate.getFormattedDate());
        System.out.println("Estimated Return Date: " + this.estRtnDate.getFormattedDate());
        if (actRtnDate == null) {
            System.out.println("Actual Return Date: Currently in reservation.");
        }
        else {
            System.out.println("Actual Return Date: " + this.actRtnDate.getFormattedDate());
        }
        System.out.println("Rental Fee: " + String.format("%.2f", this.rentalFee));
        System.out.println("Late Fee: " + String.format("%.2f", this.lateFee));
    }

    // can only be used to change estimated return date in maintenance record, so the customers don't need to wait for the maintenance date to be over.
    void setEstRtnDateForMaintenanceRecord(DateTime dateTime) {
        this.estRtnDate = dateTime;
    }
}
