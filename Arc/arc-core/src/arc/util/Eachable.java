package arc.util;

import arc.func.Cons;

public interface Eachable<T>{
    void each(Cons<? super T> cons);
}
