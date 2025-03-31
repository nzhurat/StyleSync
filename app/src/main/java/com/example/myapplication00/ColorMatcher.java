package com.example.myapplication00;
public class ColorMatcher {

    // Список кольорів та їх RGB значення
    private static final int[][] COLORS = {
            {255, 255, 255},  // Білий
            {255, 240, 200},  // Айворі
            {245, 230, 170},  // Бежевий
            {192, 192, 192},  // Перловий
            {255, 0, 0},      // Червоний
            {255, 0, 64},     // Вишневий
            {128, 0, 0},      // Бордовий
            {255, 127, 80},   // Кораловий
            {255, 182, 193},  // Пудровий
            {255, 0, 255},    // Фуксія
            {0, 0, 255},      // Синій
            {135, 206, 235},  // Блакитний
            {75, 0, 130},     // Індиго
            {0, 128, 0},      // Зелений
            {80, 200, 120},   // Смарагдовий
            {128, 128, 0},    // Оливковий
            {255, 255, 0},    // Жовтий
            {255, 219, 88},   // Гірчичний
            {255, 165, 0},    // Оранжевий
            {128, 0, 128},    // Фіолетовий
            {230, 230, 250},  // Лавандовий
            {139, 69, 19},    // Коричневий
            {255, 127, 80},   // Карамельний
            {169, 169, 169},  // Сірий
            {0, 0, 0}         // Чорний
    };

    // Список назв кольорів
    private static final String[] COLOR_NAMES = {
            "Білий", "Айворі", "Бежевий", "Перловий",
            "Червоний", "Вишневий", "Бордовий", "Кораловий",
            "Пудровий", "Фуксія", "Синій", "Блакитний", "Індиго",
            "Зелений", "Смарагдовий", "Оливковий", "Жовтий",
            "Гірчичний", "Оранжевий", "Фіолетовий", "Лавандовий",
            "Коричневий", "Карамельний", "Сірий", "Чорний"
    };

    // Метод для обчислення Евклідової відстані між двома кольорами
    private static double getColorDistance(int[] color1, int[] color2) {
        int rDiff = color1[0] - color2[0];
        int gDiff = color1[1] - color2[1];
        int bDiff = color1[2] - color2[2];
        return Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
    }

    // Метод для знаходження найближчого кольору
    public static String findClosestColor(int r, int g, int b) {
        int[] userColor = {r, g, b};
        double minDistance = Double.MAX_VALUE;
        int closestColorIndex = -1;

        for (int i = 0; i < COLORS.length; i++) {
            double distance = getColorDistance(userColor, COLORS[i]);
            if (distance < minDistance) {
                minDistance = distance;
                closestColorIndex = i;
            }
        }

        return COLOR_NAMES[closestColorIndex];
    }

    public static void main(String[] args) {
        // Приклад: перевірка для кольору з RGB (наприклад, блакитний)
        int r = 135, g = 206, b = 235; // RGB значення блакитного
        System.out.println("Найближчий колір: " + findClosestColor(r, g, b));
    }
}

