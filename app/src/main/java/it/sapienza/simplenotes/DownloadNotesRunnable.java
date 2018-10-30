package it.sapienza.simplenotes;


        import android.content.Context;
        import android.os.Handler;
        import android.os.Looper;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;

        import java.util.Calendar;
        import java.util.Collection;
        import java.util.Collections;
        import java.util.Date;
        import java.util.LinkedList;
        import java.util.List;
        import java.util.logging.LogRecord;

public class DownloadNotesRunnable implements Runnable {

    private RecyclerView recycler;
    private Context context;
    private GlobalClass global;

    public DownloadNotesRunnable(RecyclerView recycler, Context context, GlobalClass global){
        this.recycler = recycler;
        this.context = context;
        this.global = global;

    }

    @Override
    public void run() {
        Date date = new Date();
        Note n1 = new Note("Patate","fai la spesa mannaggia satana!fai la spesa mannaggia satana!fai la spesa mannaggia ", date, 1);
        Note n2 = new Note("Patate2","fai la spesa mannaggia satana!", add(date),2);
        List<Note> tmp = new LinkedList<>();
        tmp.add(n1);
        tmp.add(n2);
        global.addList(tmp);
        recycler.post(new Runnable() {
            @Override
            public void run() {
                RecyclerViewAdapter recycleAdapter = new RecyclerViewAdapter(global.getList());
                recycler.setAdapter(recycleAdapter);
                recycler.setLayoutManager(new LinearLayoutManager(context));
            }
        });
    }

    private Date add(Date date){
        // convert date to calendar
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        // manipulate date
        c.add(Calendar.YEAR, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DATE, 1); //same with c.add(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.HOUR, 1);
        c.add(Calendar.MINUTE, 1);
        c.add(Calendar.SECOND, 1);

        // convert calendar to date
        Date currentDatePlusOne = c.getTime();
        return currentDatePlusOne;
    }
}
