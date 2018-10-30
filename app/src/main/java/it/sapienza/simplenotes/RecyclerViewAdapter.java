package it.sapienza.simplenotes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";
    private List<Note> list;
    final private int CUT_SIZE=50;

    public RecyclerViewAdapter(List<Note> list) {
        this.list = list;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
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
        Note note = list.get(position);

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(note != null){
            holder.title.setText(note.getTitle());
            if(note.getText().length()>CUT_SIZE) holder.preview.setText(note.getText().substring(0,CUT_SIZE));
            else holder.preview.setText(note.getText());
            holder.date.setText(note.getDate().toString());

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView preview;
        public TextView date;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.itemTitle);
            this.preview = (TextView) itemView.findViewById(R.id.itemPreview);
            this.date = (TextView) itemView.findViewById(R.id.itemDate);
        }
    }
}
