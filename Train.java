/** An express train with its own number of carriages and seats per carriage. */
public class Train {
    public final String no, name, route, departs, arrives;
    public final int carriages, seatsPerCarriage;

    public Train(String no, String name, String route, String departs, String arrives,
                 int carriages, int seatsPerCarriage) {
        this.no = no;
        this.name = name;
        this.route = route;
        this.departs = departs;
        this.arrives = arrives;
        this.carriages = carriages;
        this.seatsPerCarriage = seatsPerCarriage;
    }

    public int totalSeats() {
        return carriages * seatsPerCarriage;
    }

    public String toCsv() {
        return String.join(",", no, name, route, departs, arrives,
                String.valueOf(carriages), String.valueOf(seatsPerCarriage));
    }

    public static Train fromCsv(String line) {
        String[] p = line.split(",", -1);
        return new Train(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim(),
                Integer.parseInt(p[5].trim()), Integer.parseInt(p[6].trim()));
    }

    @Override
    public String toString() {
        return no + "  " + name + "  (" + departs + " - " + arrives + ")";
    }
}
