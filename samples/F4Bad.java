class F4Bad {
    void deep(int[] data) {
        if (data != null) {
            for (int item : data) {
                while (item > 0) {
                    if (item % 2 == 0) {
                        System.out.println(item);
                    }
                }
            }
        }
    }
}
