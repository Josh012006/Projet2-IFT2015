package pedigree;

public class Main {
    public static void main(String[] args) {

        int n = Integer.parseInt(args[0]);
        double tMax = Double.parseDouble(args[1]);


        Simulation simulation = new Simulation(2.0, 0.9);
        simulation.simulate(n, tMax);

        Coalescence computing = new Coalescence(simulation);

        computing.startPA();

        computing.startMA();

    }
}
