public class DataConverter {
    public void convert(int[] data) {
        int i = 0;
        int val;
        while (i < data.length)
            if ((val = data[i++]) > 10)
                if ((val = val - 5) > 0)
                    System.out.println(val);
    }
}
