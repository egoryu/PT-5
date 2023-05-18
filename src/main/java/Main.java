import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (true) {
            int numOfPoint, input;
            double x, y;
            ArrayList<Double> points = new ArrayList<>();
            StringTokenizer st;

            System.out.println("""
                    Введите:
                    0 - для выхода
                    1 - для ввода данных из файла""");

            input = inputInt(in);

            switch (input) {
                case (1) -> {
                    String name = "";
                    System.out.println("Введите имя файла:");
                    while (name.equals("")) {
                        name = in.nextLine();
                    }
                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/input/" + name))) {
                        try {
                            numOfPoint = Integer.parseInt(bufferedReader.readLine());
                        } catch (Exception e) {
                            System.out.println("Ошибка при чтении количества точек");
                            continue;
                        }
                        if (numOfPoint < 1) {
                            System.out.println("Некорректный ввод. Введите положительное количество");
                            continue;
                        }

                        points.clear();

                        st = new StringTokenizer(bufferedReader.readLine());

                        for (int i = 0; i < numOfPoint; i++) {
                            try {
                                x = Double.parseDouble(st.nextToken());
                            } catch (Exception e) {
                                System.out.println("Ошибка при чтении x-" + (i + 1));
                                continue;
                            }
                            points.add(x);
                        }

                        Collections.sort(points);

                        System.out.println();

                        System.out.println("Вариационный ряд:");
                        for (var q : points) {
                            System.out.print(q + " ");
                        }

                        System.out.println("\n");

                        System.out.println("Экстремальные значения: ");
                        System.out.println("Минимум: " + points.get(0));
                        System.out.println("Максимум: " + points.get(points.size() - 1));

                        System.out.println();

                        System.out.println("Размах выборки: " + (points.get(points.size() - 1) - points.get(0)));

                        System.out.println();

                        Methods.analyze(points);

                        Methods.empiricFunction();

                        Methods.drawEmpiricFunction();

                        System.out.println();

                        System.out.println("Количество интервалов: " + Methods.getCount(points));
                        System.out.printf("Шаг: %.3f\n", Methods.getStep(points));

                        System.out.println();

                        Methods.drawFrequencyPolygon(points);
                        Methods.drawHistogram(points);

                    } catch (IOException e) {
                        System.out.println("Не удалось открыть файл");
                        continue;
                    }
                }
                case (0) -> {
                    System.out.println("Конец работы");
                    System.exit(0);
                }
                default -> {
                    System.out.println("Некорректный ввод");
                    continue;
                }
            }


        }
    }

    public static int inputInt(Scanner in) {
        int num;
        try {
            num = in.nextInt();
        } catch (Exception e) {
            if (!in.hasNextLine()) {
                System.out.println("Конец работы");
                System.exit(0);
            } else {
                System.out.println("Плохая строка");
            }
            in.nextLine();
            return -1;
        }
        return num;
    }

    public static double inputDouble(Scanner in) {
        double num;
        try {
            num = in.nextDouble();
        } catch (Exception e) {
            if (!in.hasNextLine()) {
                System.out.println("Конец работы");
                System.exit(0);
            } else {
                System.out.println("Плохая строка");
            }
            in.nextLine();
            return Double.MIN_VALUE;
        }
        return num;
    }
}
