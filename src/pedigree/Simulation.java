// File: Simulation.java
package pedigree;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;


public class Simulation {
    private final AgeModel ageModel;
    private final double reproductionRate;
    private final double fidelity;
    private final Random rnd;
    private final PQ<Event> eventQ;
    private final List<Sim> livingSims;

    private int maleCount = 0;
    private int femaleCount = 0;

    /**
     * @param ageModel        Gompertz-Makeham age model
     * @param reproductionRate rate for exponential waiting times
     * @param fidelity        probability a mother stays with the same partner
     */

    public Simulation(AgeModel ageModel, double reproductionRate, double fidelity) {
        this.ageModel = ageModel;
        this.reproductionRate = reproductionRate;
        this.fidelity = fidelity;
        this.rnd = new Random();
        this.eventQ = new PQ<>(new PQComparator<>());
        this.livingSims = new ArrayList<>();
    }

    //Live male countA
    public int getMaleCount() { return maleCount; }
    //Live female count
    public int getFemaleCount() { return femaleCount; }

    //Run the simulation starting with n founders up to time tMax.

    public void simulate(int n, double tMax) {
        // initialize founders
        for (int i = 0; i < n; i++) {
            Sim.Sex sex = rnd.nextBoolean() ? Sim.Sex.M : Sim.Sex.F;
            Sim founder = new Sim(sex);
            eventQ.insert(new Event(founder, Event.Type.BIRTH, 0.0));
        }

        // main event loop
        while (!eventQ.isEmpty()) {
            Event e = eventQ.deleteMin();
            double currentTime = e.getTime();
            if (currentTime > tMax) break;
            Sim subject = e.getSubject();
            if (currentTime < subject.getDeathTime()) {
                switch (e.getType()) {
                    case BIRTH:
                        processBirth(e);
                        break;
                    case DEATH:
                        processDeath(e);
                        break;
                    case REPRODUCTION:
                        processReproduction(e);
                        break;
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
        eventQ.insert(new Event(x, Event.Type.DEATH, t + life));

        // schedule reproduction if female
        if (x.getSex() == Sim.Sex.F) {
            double wait = AgeModel.randomWaitingTime(rnd, reproductionRate);
            eventQ.insert(new Event(x, Event.Type.REPRODUCTION, t + wait));
        }

        // add to population and update gender counts
        livingSims.add(x);
        if (x.getSex() == Sim.Sex.M) maleCount++;
        else                   femaleCount++;
    }

    private void processDeath(Event e) {
        Sim x = e.getSubject();
        livingSims.remove(x);
        if (x.getSex() == Sim.Sex.M) maleCount--;
        else                   femaleCount--;
    }

    private void processReproduction(Event e) {
        Sim mother = e.getSubject();
        double t = e.getTime();
        // always schedule next reproduction
        double wait = AgeModel.randomWaitingTime(rnd, reproductionRate);
        eventQ.insert(new Event(mother, Event.Type.REPRODUCTION, t + wait));

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
        eventQ.insert(new Event(child, Event.Type.BIRTH, t));
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
