package pedigree;

import java.util.PriorityQueue;

public class Coalescence {

    private static class Pair<E, F> {
        public E first;
        public F second;

        public Pair(E first, F second) {
            this.first = first;
            this.second = second;
        }
    }

    private static class Allele extends Pair<Sim, Pair<Double, Integer>> implements Comparable<Allele> {
        public Allele(Sim sim, double p, int n) {
            super(sim, new Pair<>(p, n));
        }

        @Override
        public int compareTo(Allele o) {
            return Double.compare(this.first.getBirthTime(),o.first.getBirthTime());
        }
    }

    private final PriorityQueue<Allele> PA = new PriorityQueue<>();
    private final PriorityQueue<Allele> MA = new PriorityQueue<>();


    public Coalescence(Simulation simulation) {

        double p = simulation.getActualTime();
        int males = simulation.getNumMales();
        int females = simulation.getFemales();

        // Initialiser les deux priorityQueue
        for(Sim sim : simulation.getPopulation()) {
            if(sim.getSex() == Sim.Sex.M) {
                PA.add(new Allele(sim, p, males));
            } else {
                MA.add(new Allele(sim, p, females));
            }
        }
    }

    public void start() {
        // Premier essai d'éciture de logique avec les hommes
        while (!allFounders(PA)) {

        }
    }


    private boolean allFounders(PriorityQueue<Allele> record) {
        for(Allele allele : record) {
            Sim sim = allele.first;
            if(sim.getMother() != null || sim.getFather() != null) {
                return false;
            }
        }

        return true;
    }

    private boo

}
