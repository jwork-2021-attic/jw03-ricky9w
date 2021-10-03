package example;

public class RickyBubbleSorter implements Sorter {
    private int[] a;
    private String plan = "";
    
    @Override
    public void load(int[] a) {
        this.a = a;
    }

    private void swap(int i, int j) {
        int tmp;
        tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
        plan += String.format("%d<->%d\n", a[i], a[j]);
    }

    @Override
    public void sort() {
        boolean sorted = false;
        while(!sorted) {
            sorted = true;
            for (int i = 0; i < a.length - 1; i++) {
                if (a[i] > a[i + 1]) {
                    swap(i, i + 1);
                    sorted = false;
                }
            }
        }
    }

    @Override
    public String getPlan() {
        return this.plan;
    }

}
