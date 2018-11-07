package it.sapienza.simplenotes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import it.sapienza.simplenotes.activities.NoteActivity;
import it.sapienza.simplenotes.model.Note;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    private Note[] list;
    private final int CUT_SIZE_TITLE=35;
    private final int CUT_SIZE_TEXT =100;
    private Context context;
    private GlobalClass global;

    public RecyclerViewAdapter(Note[] list) {
        this.list = list;
    }

    public Note[] getList(){
        return this.list;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.layout_list_item, parent, false);

        // create a new view
        //TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        // return new holder instance
        MyViewHolder vh = new MyViewHolder(contactView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d(TAG,"RecyclerViewAdapter: position="+position);
        // Get the data model based on position
        final Note note = list[position];

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(note != null){
            if(note.getTitle()!=null) {
                if(note.getTitle().length()> CUT_SIZE_TITLE) holder.title.setText(note.getTitle().substring(0, CUT_SIZE_TITLE));
                else holder.title.setText(note.getTitle());
            }
            if(note.getText()!=null) {
                if(note.getText().length()> CUT_SIZE_TEXT) holder.preview.setText(note.getText().substring(0, CUT_SIZE_TEXT));
                else holder.preview.setText(note.getText());
            }
            if(note.getDate()!=null) {
                DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
                holder.date.setText(dateFormat.format(note.getDate()));
            }
        }
        global = (GlobalClass) context.getApplicationContext();
        //add on click listener
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(context, NoteActivity.class);
                newIntent.putExtra("title",note.getTitle());
                newIntent.putExtra("text",note.getText());
                newIntent.putExtra("id",note.getId());
                context.startActivity(newIntent);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(list == null) return 0;
        return list.length;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView preview;
        public TextView date;
        public LinearLayout layout;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.itemTitle);
            this.preview = (TextView) itemView.findViewById(R.id.itemPreview);
            this.date = (TextView) itemView.findViewById(R.id.itemDate);
            this.layout = itemView.findViewById(R.id.itemLayout);
        }
    }
}
