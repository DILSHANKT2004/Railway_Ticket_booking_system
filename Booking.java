/** One booked seat. A booking reference groups up to four seats of one customer. */
public class Booking {
    public static final String HEADER = "Ref,TrainNo,Date,Carriage,Seat,IdNo,Name,Contact";

    public final String ref, trainNo, date, idNo, name, contact;
    public final int carriage, seat;

    public Booking(String ref, String trainNo, String date, int carriage, int seat,
                   String idNo, String name, String contact) {
        this.ref = ref;
        this.trainNo = trainNo;
        this.date = date;
        this.carriage = carriage;
        this.seat = seat;
        this.idNo = idNo;
        this.name = name;
        this.contact = contact;
    }

    public String seatKey() {
        return carriage + "-" + seat;
    }

    public String toCsv() {
        return String.join(",", ref, trainNo, date, String.valueOf(carriage),
                String.valueOf(seat), idNo, name.replace(",", " "), contact);
    }

    public static Booking fromCsv(String line) {
        String[] p = line.split(",", -1);
        return new Booking(p[0], p[1], p[2], Integer.parseInt(p[3]), Integer.parseInt(p[4]),
                p[5], p[6], p[7]);
    }
}
