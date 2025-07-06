package pedigree;

import java.util.Comparator;

public class PQComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        if(o1 instanceof Event && o2 instanceof Event) {
            return o1.time.compareTo(o2.time);
        }
        return 0;
    }
}
