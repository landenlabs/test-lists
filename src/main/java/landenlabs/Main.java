package landenlabs;

import java.util.*;
import java.util.function.Supplier;

/**
 * Unit test ArrayList and LinkedList container add/get performance.
 *
 * To enable asserts, add VM options -ea to run configuration.
 *
 * [Start] Mac OS X aarch64
 * TestAddFIRST             1,000         10,000        100,000  ; # Elements
 *       ArrayList         25,179          2,724            246  ; Ops/Milli
 *      LinkedList        227,880        231,088        211,488  ; Ops/Milli
 * TestAddLAST              1,000         10,000        100,000  ; # Elements
 *       ArrayList        518,762        492,387        577,722  ; Ops/Milli
 *      LinkedList        327,233        378,741        342,057  ; Ops/Milli
 * TestGet                  1,000         10,000        100,000  ; # Elements
 *       ArrayList        211,807        218,321        305,594  ; Ops/Milli
 *      LinkedList          3,272            270             26  ; Ops/Milli
 * TestContains             1,000         10,000        100,000  ; # Elements
 *       ArrayList          4,948            467             51  ; Ops/Milli
 *      LinkedList          1,227            122             12  ; Ops/Milli
 * TestDelFIRST             1,000         10,000        100,000  ; # Elements
 *       ArrayList         26,436          2,607            233  ; Ops/Milli
 *      LinkedList        464,821        434,905        420,579  ; Ops/Milli
 * TestDelMIDDLE            1,000         10,000        100,000  ; # Elements
 *       ArrayList         34,921          3,672            330  ; Ops/Milli
 *      LinkedList          6,381            540             53  ; Ops/Milli
 * TestDelLAST              1,000         10,000        100,000  ; # Elements
 *       ArrayList        377,187        369,720        360,139  ; Ops/Milli
 *      LinkedList        308,185        310,209        300,699  ; Ops/Milli
 * TestSort                 1,000         10,000        100,000  ; # Elements
 *       ArrayList      1,065,024      1,488,261      1,569,730  ; Ops/Milli
 *      LinkedList        250,931        214,955        263,236  ; Ops/Milli
 * [Done]
 */
public class Main {

    static final long MIN_TEST_MILLI = 1_000;
    static final int[] sizes = { 1000, 10_000, 100_000 };


    static class TestLists <TT>  {
        interface Producer<TT> {
            TT get(int idx);
        }

        static abstract class ListOp <TT> {
            enum Mode { FIRST, MIDDLE, LAST };
            Mode mode = null;
            List<TT> prepare(List<TT> list, int size, Producer<TT> producer) { return list; }
            abstract void execute(List<TT> list, int size, Producer<TT> producer);
            String name() { return getClass().getSimpleName() + ((mode != null) ? mode.name() : ""); }
        }

        <TT> void testList(Supplier<List<TT>> listSupplier, ListOp<TT> listOp, int size, Producer<TT> producer) {
            long totalMilli = 0;
            long idx;
            for (idx = 1; idx < Integer.MAX_VALUE; idx++) {
                List<TT> list = listOp.prepare(listSupplier.get(), size, producer);
                long startMilli = System.currentTimeMillis();
                listOp.execute(list, size, producer);
                totalMilli += System.currentTimeMillis() - startMilli;
                if (totalMilli > MIN_TEST_MILLI)
                    break;
            }
            System.out.printf("%,15d",  (idx * size) / totalMilli);
        }

        <TT> void testListSizes(Supplier<List<TT>> listSupplier, ListOp<TT> listOp, Producer<TT> producer) {
            System.out.printf("%15s", listSupplier.get().getClass().getSimpleName());
            for (int size : sizes) {
                testList(listSupplier, listOp, size, producer);
            }
            System.out.println("  ; Ops/Milli");
        }

        <TT> void testLists(ListOp<TT> listOp, Producer<TT> producer) {
            System.out.printf("%-15s", listOp.name());
            for (int size : sizes) System.out.printf("%,15d", size);
            System.out.println("  ; # Elements");

            testListSizes(ArrayList::new, listOp, producer);
            testListSizes(LinkedList::new, listOp, producer);
            // testListSizes(ArrayDeque::new, listOp, producer);
        }

        static class TestAdd<TT> extends ListOp<TT> {
            public TestAdd(Mode mode) {
                this.mode = mode;
            }
            public void execute(List<TT> list, int size, Producer<TT> producer) {
                if (mode == Mode.FIRST) {
                    for (int idx = 0; idx < size; idx++) {
                        list.add(0, producer.get(idx));
                    }
                } else {
                    for (int idx = 0; idx < size; idx++) {
                        list.add(producer.get(idx));
                    }
                }
            }
        }
        static abstract class TestFilled <TT> extends ListOp<TT> {
            List<TT> preparedList;

            public List<TT> prepare(List<TT> list, int size, Producer<TT> producer) {
                if (preparedList == null || preparedList.size() != size) {
                    for (int idx = 0; idx < size; idx++) {
                        list.add( producer.get(idx));
                    }
                    preparedList = list;
                }
                return preparedList;
            }
        }
        static class TestContains <TT> extends TestFilled<TT> {
            boolean pass;
            public void execute(List<TT> list, int size, Producer<TT> producer) {
                assert(list.size() == size);
                int pos = size /2; // start in middle
                pass = true;
                for (int idx= 0; idx < size; idx++) {
                    pass &= list.contains(producer.get(pos));
                    pos = (pos + 11) % size;  // pseudo random
                }
                assert(pass);
            }
        }
        static class TestGet <TT> extends TestFilled<TT> {
            boolean pass;
            public void execute(List<TT> list, int size, Producer<TT> producer) {
                assert(list.size() == size);
                int pos = size /2; // start in middle
                pass = true;
                for (int idx= 0; idx < size; idx++) {
                    TT data = producer.get(pos);
                    pass &= data.equals(list.get(pos));
                    pos = (pos + 11) % size;  // pseudo random
                }
                assert(pass);
            }
        }
        static class TestDel<TT> extends TestFilled<TT> {
            public TestDel(Mode mode) {
                this.mode = mode;
            }
            public void execute(List<TT> list, int size, Producer<TT> producer) {
                assert(list.size() == size);
                int cnt =
                    switch (mode) {
                        case FIRST -> 0;
                        case MIDDLE -> size/2;
                        case LAST -> size-1;
                    };
                while (!list.isEmpty()) {
                    list.remove(cnt);
                    if (cnt > 0) cnt--;
                }
            }
        }
        static class TestSort <TT> extends TestFilled<TT> {
            Comparator<TT> compartor;
            public TestSort(Comparator<TT> comparator) {
                this.compartor = comparator;
            }
            public void execute(List<TT> list, int size, Producer<TT> producer) {
                assert(list.size() == size);
                list.sort(compartor);
                assert(compartor.compare(list.get(0), list.get(1)) < 0);
            }
        }
        static class TestBinSearch <TT  extends Comparable<? super TT>> extends TestFilled<TT> {
            public void execute(List<TT> list, int size, Producer<TT> producer) {
                assert(list.size() == size);
                // Assumes list is sorted.
                int pos = size /2; // start in middle
                for (int idx= 0; idx < size; idx++) {
                    TT data = producer.get(pos);
                    int posAt = Collections.<TT>binarySearch(list, data);
                    assert(data.equals(list.get(posAt)));
                    pos = (pos + 11) % size;  // pseudo random
                }
            }
        }

         void execute() {
             System.out.println("[Start] "+ System.getProperty("os.name") + " " + System.getProperty("os.arch"));
             testLists(new TestAdd<>(ListOp.Mode.FIRST), this::producer);
             testLists(new TestAdd<>(ListOp.Mode.LAST), this::producer);
             testLists(new TestGet<>(), this::producer);
             testLists(new TestContains<>(), this::producer);
             testLists(new TestDel<>(TestDel.Mode.FIRST), this::producer);
             testLists(new TestDel<>(TestDel.Mode.MIDDLE), this::producer);
             testLists(new TestDel<>(TestDel.Mode.LAST), this::producer);
             testLists(new TestSort<>(this::compare), this::producer);
             // testLists(new TestBinSearch<>(), this::producer);
             System.out.println("[Done]");
         }

        Integer producer(int idx) {
            return idx;
        }

        // Reverse order to make sort work harder
        int compare(int y, int x) {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
    }

    public static  void main(String[] args) {
        TestLists<Integer> testLists = new TestLists<>();
        testLists.execute();
    }
}