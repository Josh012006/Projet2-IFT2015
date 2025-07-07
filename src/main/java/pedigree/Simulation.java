// File: Simulation.java
package pedigree;

import java.util.PriorityQueue;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;


public class Simulation {
    private final AgeModel ageModel = new AgeModel();
    private final double reproductionRate;
    private final double fidelity;
    private final Random rnd;
    private final PriorityQueue<Event> eventQ;
    private final ArrayList<Sim> livingSims;

    private double currentTime = 0;

    /**
     * @param reproductionRateNumerator rate for exponential waiting times
     * @param fidelity                  probability a mother stays with the same partner
     */

    public Simulation(double reproductionRateNumerator, double fidelity) {
        this.reproductionRate = reproductionRateNumerator / ageModel.expectedParenthoodSpan(Sim.MIN_MATING_AGE_F, Sim.MAX_MATING_AGE_F);
        this.fidelity = fidelity;
        this.rnd = new Random();
        this.eventQ = new PriorityQueue<Event>();
        this.livingSims = new ArrayList<>();
    }

    // The current time of the simulation
    public double getCurrentTime() { return currentTime; }
    // Getting the current population
    public ArrayList<Sim> getPopulation() { return livingSims; }

    //Run the simulation starting with n founders up to time tMax.

    public void simulate(int n, double tMax) {
        // initialize founders
        for (int i = 0; i < n; i++) {
            Sim.Sex sex = rnd.nextBoolean() ? Sim.Sex.M : Sim.Sex.F;
            Sim founder = new Sim(sex);
            eventQ.add(new Event(founder, Event.Type.BIRTH, 0.0));
        }

        double step = 0;

        System.out.println("Temps,Taille de la population");
        // main event loop
        while (!eventQ.isEmpty()) {
            Event e = eventQ.poll();
            currentTime = e.getTime();

            if(currentTime >= step) {
                System.out.println(currentTime + "," + livingSims.size());
                step += 100;
            }

            if (currentTime > tMax) break;

            Sim subject = e.getSubject();
            if (currentTime < subject.getDeathTime()) {
                switch (e.getType()) {
                    case BIRTH:
                        processBirth(e);
                        break;
                    case REPRODUCTION:
                        processReproduction(e);
                        break;
                }
            } else {
                if(e.getType() == Event.Type.DEATH) {
                    processDeath(e);
                }
            }
        }

    }

    private void processBirth(Event e) {
        Sim x = e.getSubject();
        double t = e.getTime();
        // schedule death
        double life = ageModel.randomAge(rnd);
        x.setDeathTime(t + life);
        eventQ.add(new Event(x, Event.Type.DEATH, t + life));

        // schedule reproduction if female
        if (x.getSex() == Sim.Sex.F) {
            double wait = AgeModel.randomWaitingTime(rnd, reproductionRate);
            eventQ.add(new Event(x, Event.Type.REPRODUCTION, t + wait));
        }

        // add to population and update gender counts
        livingSims.add(x);
    }

    private void processDeath(Event e) {
        Sim x = e.getSubject();
        livingSims.remove(x);
    }

    private void processReproduction(Event e) {
        Sim mother = e.getSubject();
        double t = e.getTime();
        // always schedule next reproduction
        double wait = AgeModel.randomWaitingTime(rnd, reproductionRate);
        eventQ.add(new Event(mother, Event.Type.REPRODUCTION, t + wait));

        if (!mother.isMatingAge(t)) return;

        // choose partner
        Sim father = choosePartner(mother, t);
        // create child
        Sim.Sex sex = rnd.nextBoolean() ? Sim.Sex.M : Sim.Sex.F;
        Sim child = new Sim(mother, father, t, sex);
        // record partnership
        mother.setMate(father);
        father.setMate(mother);
        // schedule child's birth handling
        eventQ.add(new Event(child, Event.Type.BIRTH, t));
    }

    /**
     * Partner selection per fidelity rules.
     */
    private Sim choosePartner(Sim mother, double time) {
        if (!mother.isInARelationship(time) || rnd.nextDouble() > fidelity) {
            Sim partner = null;
            while (partner == null) {
                Sim cand = livingSims.get(rnd.nextInt(livingSims.size()));
                if (cand.getSex() != mother.getSex()
                        && cand.isMatingAge(time)
                        && (!cand.isInARelationship(time) || rnd.nextDouble() > fidelity)) {
                    partner = cand;
                }
            }
            return partner;
        } else {
            return mother.getMate();
        }
    }
}
