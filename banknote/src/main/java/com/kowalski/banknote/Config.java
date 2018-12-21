package com.kowalski.banknote;

class Config {
    private static String[] titles = {
            //face, back
            "1 UAH", "1 UAH",
            "2 UAH", "2 UAH",
            "5 UAH", "5 UAH",
            "10 UAH", "10 UAH",
            "20 UAH", "20 UAH",
            "50 UAH", "50 UAH",
            "100 UAH", "100 UAH",
            "200 UAH", "200 UAH",
            "500 UAH", "500 UAH"
    };
    private static String[] files = {
            //face, back
            "1", "1b",
            "2", "2b",
            "5", "5b",
            "10", "10b",
            "20", "20b",
            "50", "50b",
            "100", "100b",
            "200", "200b",
            "500", "500b"
    };
    private static String[] cascNames = {
            // face, back
            "one", "one_b",
            "two", "two_b",
            "five", "five_b",
            "ten", "ten_b",
            "twenty", "twenty_b",
            "fifty", "fifty_b",
            "oneh", "oneh_b",
            "twoh", "twoh_b",
            "fiveh", "fiveh_b"
    };
    private static double[] scales = {
            //face, back
            2, 2,   // 1 UAH
            3, 3,   // 2 UAH
            2, 2,   // 5 UAH
            2, 2,   // 10 UAH
            2, 2,   // 20 UAH
            2, 2,   // 50 UAH
            2, 2,   // 100 UAH
            2, 2,   // 200 UAH
            2, 2    // 500 UAH
    };
    private static int[] neighbors = {
            //face, back
            20, 20, // 1 UAH
            25, 25, // 2 UAH
            20, 20, // 5 UAH
            20, 20, // 10 UAH
            20, 20, // 20 UAH
            20, 20, // 50 UAH
            20, 20, // 100 UAH
            20, 20, // 200 UAH
            20, 20  // 500 UAH
    };

    private static double[] histPercentages = {
            //face, back
            0.3, 0.3,   // 1 UAH
            0.3, 0.3,   // 2 UAH
            0.3, 0.3,   // 5 UAH
            0.3, 0.3,   // 10 UAH
            0.3, 0.3,   // 20 UAH
            0.3, 0.3,   // 50 UAH
            0.3, 0.3,   // 100 UAH
            0.3, 0.3,   // 200 UAH
            0.3, 0.3    // 500 UAH

    };

    static String getTitle(int i) {
        return titles[i];
    }

    static String getFile(int i) {
        return files[i];
    }

    static String getCascName(int i) {
        return cascNames[i];
    }

    static double getScale(int i) {
        return scales[i];
    }

    static int getNeighbors(int i) {
        return neighbors[i];
    }

    static double getHistPercentage(int i) {
        return histPercentages[i];
    }

    static int getLength() {
        return files.length;
    }
}
