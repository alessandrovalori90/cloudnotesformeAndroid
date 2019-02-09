package it.sapienza.simplenotes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
    private Context context;


    public RecyclerViewAdapter(Note[] list) {
        this.list = list;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.layout_list_item, parent, false);

        // create a new view
        //TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        // return new holder instance
        return new MyViewHolder(contactView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final int CUT_SIZE_TITLE=35;
        final int CUT_SIZE_TEXT =100;
        Log.d(TAG,"RecyclerViewAdapter: size:"+getItemCount()+" position="+position+" actual pos: "+actualPosition(position));
        // Get the data model based on position
        final Note note = list[actualPosition(position)];

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(note != null && !note.isDelete()){
            if(note.getTitle()!=null) {
                if(note.getTitle().length()> CUT_SIZE_TITLE) holder.title.setText(note.getTitle().substring(0, CUT_SIZE_TITLE));
                else holder.title.setText(note.getTitle());
            }
            if(note.getText()!=null) {
                if(note.getText().length()> CUT_SIZE_TEXT) holder.preview.setText(note.getText().substring(0, CUT_SIZE_TEXT));
                else holder.preview.setText(note.getText());
            }
            if(note.getDate()!=null) {
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yy");//should add country detection
                holder.date.setText(dateFormat.format(note.getDate()));
            }
        }
        //add on click listener
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(context, NoteActivity.class);
                assert note != null;
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
        int count = 0;
        for(Note note:list)
            count = (note.isDelete())? ++count: count; //if condition respected set count to count++ else to count
        return list.length-count;
    }
    /*
    * returns the position of all the notes that are not deleted.
    * if we have |deleted|good|good|
    * with pos zero returns 1
    * with pos 1 returns 2
    * pos 2 is not called because size is resized in getItemCount
    * basically substitutes the deleted items with the non deleted to make these notes invisible to the user
     */

    private int actualPosition(int pos){
        int b=0;
        for(int i=0; i<pos;i++){
            if(list[i].isDelete()) ++b;
        }

        int count=0;
        for(int i=pos; i<list.length;i++){
            if(!list[i].isDelete()){
                if(count<b) ++count;
                else return i;
            }
        }

        return 0;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView preview;
        private TextView date;
        private LinearLayout layout;
        private MyViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.itemTitle);
            this.preview = itemView.findViewById(R.id.itemPreview);
            this.date =  itemView.findViewById(R.id.itemDate);
            this.layout = itemView.findViewById(R.id.itemLayout);
        }
    }
}
