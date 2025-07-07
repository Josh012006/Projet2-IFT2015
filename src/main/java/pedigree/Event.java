package pedigree;

/**
 * An event in the pedigree simulation: birth, death, or reproduction.
 */
public class Event implements Comparable<Event> {
    public enum Type { BIRTH, DEATH, REPRODUCTION }

    private final Sim subject;
    private final Type type;
    private final double time;

    public Event(Sim subject, Type type, double time) {
        this.subject = subject;
        this.type = type;
        this.time = time;
    }

    public Sim getSubject() {
        return subject;
    }

    public Type getType() {
        return type;
    }

    public double getTime() {
        return time;
    }

    @Override
    public int compareTo(Event o) {
        return Double.compare(this.time, o.time);
    }

    @Override
    public String toString() {
        return "Event[" + type + " @" + time + " for " + subject + "]";
    }
}