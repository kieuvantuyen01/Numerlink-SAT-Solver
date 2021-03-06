import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class GenerateTest {
    public static File folder = new File("E:\\Lab\\Test cases");

    public static void deleteFile(File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                deleteFile(fileEntry);
            } else {
                if (fileEntry.isFile()) {
                    fileEntry.delete();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Nhap so lan Test:");
        int m = sc.nextInt();
        System.out.println("Nhap kich thuoc cua ma tran:");
        int n = sc.nextInt();
        System.out.println("Nhap gia tri lon nhat cua o trong ma tran:");
        int p = sc.nextInt();
        Random rd = new Random();
        int im = 0;
        if (folder != null) {
            deleteFile(folder);
        }
        while (im++ < m) {
            File outputFile = new File(String.format("E:\\Lab\\Test cases\\%dx%d %d.in", n, n, im));
            FileWriter writer = new FileWriter(outputFile);
            writer.write(n + " " + n + "\n");
            writer.write(p + "\n");
            int a[][] = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    a[i][j] = 0;
                }
            }
            int p1 = 2 * p;
            while (p1-- > 0) {
                int p2 = p1 / 2;
                int x = rd.nextInt(n);
                int y = rd.nextInt(n);
                if (a[x][y] == 0) {
                    a[x][y] = p2 + 1;
                } else {
                    p1++;
                }
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    writer.write(a[i][j] + " ");
                }
                writer.write("\n");
            }
            writer.flush();
            writer.close();
        }
    }
}
