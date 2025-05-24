import java.util.Random;

public class ModularHash implements HashFactory<Integer> {
    private Random rand;
    private HashingUtils utils;

    public ModularHash() {
        this.rand = new Random();
        utils = new HashingUtils();
    }

    @Override
    public HashFunctor<Integer> pickHash(int k) {
        Functor func  = new Functor(k);
        return func;
    }

    public class Functor implements HashFunctor<Integer> {
        final private int a;
        final private int b;
        final private long p;
        final private int m;

        public Functor(int k){
            this.a = 1 + ModularHash.this.rand.nextInt(Integer.MAX_VALUE - 1);
            this.b = ModularHash.this.rand.nextInt(Integer.MAX_VALUE);
            this.p = ModularHash.this.utils.genPrime((long)Integer.MAX_VALUE + 1, Long.MAX_VALUE);
            this.m = 1<<k;
        }

        @Override
        public int hash(Integer key) {
            long x = a * (long) key + b;
            long result = ModularHash.this.utils.mod(x, p);
            long hashVal = ModularHash.this.utils.mod(result, m);
            return (int) hashVal;
        }

        public int a() {
            return a;
        }

        public int b() {
            return b;
        }

        public long p() {
            return p;
        }

        public int m() {
            return m;
        }
    }
}
