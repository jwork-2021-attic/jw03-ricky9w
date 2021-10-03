package example;

public class mySelect implements Sorter {
    
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
        for (int i = 0; i < a.length; i++) {
            int mIdx = i;
            for (int j = i; j < a.length; j++)
                if (a[j] < a[mIdx])
                    mIdx = j;
            if (mIdx != i) 
                swap(i, mIdx);
        }
    }

    @Override
    public String getPlan() {
        return this.plan;
    }
}
