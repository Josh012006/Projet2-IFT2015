package pedigree;

import java.util.*;

public class Coalescence {

    private static class Pair<E, F> {
        public E first;
        public F second;

        public Pair(E first, F second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return first + "," + second;
        }
    }

    private static class MyComparator implements Comparator<Sim> {

        @Override
        public int compare(Sim o1, Sim o2) {
            return -1 * Double.compare(o1.getBirthTime(), o2.getBirthTime());
        }// Multiplier par -1 pour avoir un max-tas
    }


    private final PriorityQueue<Sim> PA = new PriorityQueue<>(new MyComparator());
    private final Set<Sim> PASet = new HashSet<>();
    private final PriorityQueue<Sim> MA = new PriorityQueue<>(new MyComparator());
    private final Set<Sim> MASet = new HashSet<>();

    private final Simulation simulation;


    public Coalescence(Simulation simulation) {
        this.simulation = simulation;
    }


    public void startPA() {
        // Voir ce qu'on fera avec après
        final ArrayList<Pair<Double, Integer>> results = new ArrayList<>();

        double p = this.simulation.getCurrentTime();

        // Etat initial avant le lancement des opérations
        for (Sim sim : this.simulation.getPopulation()) {
            if(sim.getSex() == Sim.Sex.M) {
                PA.add(sim);
                PASet.add(sim);
            }
        }



        results.add(new Pair<>(p, PA.size()));


        // Lancement de la boucle
        while (notAllFounders(PASet) && !PASet.isEmpty()) {
            Sim youngest = PA.poll();

            if(!(youngest == null)) {
                if(!youngest.isFounder()) {
                    PASet.remove(youngest);  // le retirer s'il n'est pas un fondateur
                    double t = youngest.getBirthTime();
                    int n = PASet.size();

                    Sim father = youngest.getFather();

                    // Vérifier si son père est dans la liste d'allèles
                    if(!PASet.contains(father)) {
                        PASet.add(father);       // Ne l'ajoutera pas s'il y est déjà
                        PA.add(father);
                    } else {
                        n--;
                        results.add(new Pair<>(t, n));
                    }
                }
            }

        }

        System.out.println("Temps,Aieux");
        for(Pair<Double, Integer> val : results) {
            System.out.println(val);
        }
    }




    public void startMA() {
        // Voir ce qu'on fera avec après
        final ArrayList<Pair<Double, Integer>> results = new ArrayList<>();

        double p = this.simulation.getCurrentTime();

        // Etat initial avant le lancement des opérations
        for (Sim sim : this.simulation.getPopulation()) {
            if(sim.getSex() == Sim.Sex.F) {
                MA.add(sim);
                MASet.add(sim);
            }
        }

        results.add(new Pair<>(p, MA.size()));


        // Lancement de la boucle
        while (notAllFounders(MASet) && !MASet.isEmpty()) {
            Sim youngest = MA.poll();

            if(!(youngest == null) && !youngest.isFounder()) {
                MASet.remove(youngest);  // la retirer si elle n'est pas une fondatrice
                double t = youngest.getBirthTime();
                int n = MASet.size();

                Sim mother = youngest.getMother();

                // Vérifier si sa mère est dans la liste d'allèles
                if(!MASet.contains(mother)) {
                    MASet.add(mother);       // Ne l'ajoutera pas si elle y est déjà
                    MA.add(mother);
                } else {
                    n--;
                    results.add(new Pair<>(t, n));
                }

            }

        }

        System.out.println("Temps,Aieules");
        for(Pair<Double, Integer> val : results) {
            System.out.println(val);
        }
    }


    private boolean notAllFounders(Set<Sim> record) {
        for(Sim sim : record) {
            if(!sim.isFounder()) {
                return true;
            }
        }
        return false;
    }


}
