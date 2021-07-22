import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.TimeoutException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Controller {
    private static CNFConverter cnfConverter = new CNFConverter();
    private static SATSolver satSolver;
    public static int rows;
    public static int cols;

    public static List<Long> main(File file) throws IOException, TimeoutException, ParseFormatException, ContradictionException {
        List<Long> res = new ArrayList<>();
        Scanner sc = new Scanner(file);
        NumberLink numberLink = new NumberLink();
        numberLink.setRow(sc.nextInt());
        rows = numberLink.getRow();
        numberLink.setCol(sc.nextInt());
        cols = numberLink.getCol();
        NumberLink.setMaxNum(sc.nextInt());
        int[][] input = new int[numberLink.getRow() + 1][numberLink.getCol() + 1];
        for (int i = 1; i < numberLink.getRow() + 1; i++) {
            for (int j = 1; j < numberLink.getCol() + 1; j++) {
                input[i][j] = sc.nextInt();
            }
        }
        numberLink.setInputs(input);

        System.out.println(numberLink);


        // Ghi ra file CNF
        File fileCNF = new File("text.cnf");
        FileWriter writer = new FileWriter(fileCNF);

        //long t1 = System.currentTimeMillis();
        SatEncoding satEncoding = cnfConverter.generateSat(numberLink);

        long clause = satEncoding.getClauses();
        long vars = satEncoding.getVariables();
        String firstLine = "p cnf " + vars + " " + clause;
        System.out.println("So luong bien la: " + vars);
        System.out.println("So luong menh de la: " + clause);
        writer.write(firstLine + "\n");
        List<String> rules = satEncoding.getRules();
        for (int i = 0; i < rules.size(); i++) {
            // dong cuoi khong xuong dong
            if (i == rules.size() - 1) {
                writer.write(rules.get(i));
                continue;
            }
            writer.write(rules.get(i) + "\n");
        }
        writer.flush();
        writer.close();

        // SAT Solve
        NumberLinkResponse response = new NumberLinkResponse();
        DimacsReader reader = new DimacsReader(SolverFactory.newDefault());
        reader.parseInstance("text.cnf");
        satSolver = new SATSolver(reader);
        IProblem problem = null;

        //while (System.currentTimeMillis() < t1 + 360 * 1000) {
             problem = satSolver.solve("text.cnf");
        //}
        //long t2 = System.currentTimeMillis();
        if (problem.isSatisfiable()) {
            System.out.print("SAT");
            int[] model = problem.model();
            int[][] board = numberLink.getInputs();
            int countBreak = 0;
            for (int k = CNFConverter.num_of_x; k < model.length; k++) {

                if (model[k] > 0) {

                    int positionValue = model[k];
                    int i = cnfConverter.getValueOfYI(positionValue, numberLink);
                    int j = cnfConverter.getValueOfYJ(positionValue, numberLink);

                    int breakPoint = (i - 1) % numberLink.getCol();
                    int value = cnfConverter.getValueOfY(model[k], numberLink);

                    if (breakPoint == countBreak) {
                        System.out.println();
                        countBreak++;
                    }
                    if (value-4 < 10) {
                        System.out.print(" ");
                    }
                    System.out.print((value - 4) + " ");
                }
            }
        }  else {
            System.out.print("UNSAT");
        }
        res.add((long) rows);
        res.add(clause);
        res.add(vars);
        //res.add(t2-t1);
        return res;
    }

    public static void printFormat(NumberLinkResponse response) {
        for (int i = 0; i < response.getCells().size(); i++) {
            int j = 0;
            for (j = 0; j < response.getCells().get(i).size(); j++) {
                if (response.getCells().get(i).get(j) == null) {
                    System.out.println("  ");
                } else {
                    if (response.getCells().get(i).get(j).getPattern().size() == 1) {
                        System.out.print(response.getCells().get(i).get(j).getValue());
                        if (response.getCells().get(i).get(j).getValue() < 10) {
                            System.out.print(" ");
                        }
                    } else if (response.getCells().get(i).get(j).getPattern().size() == 2) {
                        //System.out.print(response.getCells().get(i).get(j).getPattern());
                        int first = response.getCells().get(i).get(j).getPattern().get(0);
                        int second = response.getCells().get(i).get(j).getPattern().get(1);
                        if (first == CNFConverter.LEFT && second == CNFConverter.RIGHT)
                            System.out.print("- ");
                        else if (first == CNFConverter.LEFT && second == CNFConverter.DOWN)
                            System.out.print("┐ ");
                        else if (first == CNFConverter.LEFT && second == CNFConverter.UP)
                            System.out.print("┘ ");
                        else if (first == CNFConverter.RIGHT && second == CNFConverter.DOWN)
                            System.out.print("┌ ");
                        else if (first == CNFConverter.RIGHT && second == CNFConverter.UP)
                            System.out.print("└ ");
                        else if (first == CNFConverter.UP && second == CNFConverter.DOWN)
                            System.out.print("│ ");
                    } else if (response.getCells().get(i).get(j).getPattern().size() == 3) {
                        System.out.print(" * ");
                    }
                }
            }
            System.out.println();
        }
    }
}
