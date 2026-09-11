package arc.graphics.g2d;

import java.util.concurrent.ForkJoinPool;

import arc.graphics.g2d.SpriteBatch.PopulateTask;

public class ForkJoinHolder{
    public final ForkJoinPool pool = ForkJoinPool.commonPool();
    public PopulateTask populateTask = new PopulateTask();
}
