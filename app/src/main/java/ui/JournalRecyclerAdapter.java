package ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.self.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Journal;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Journal> journalList;   //create a list of type Journal

    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.journal_row, viewGroup, false) ;

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecyclerAdapter.ViewHolder viewHolder, int position) {

        Journal journal = journalList.get(position);
        String imageUrl;

        viewHolder.title.setText(journal.getTitle());
        viewHolder.thoughts.setText(journal.getThought());
        //viewHolder.name.setText(journal.getUserName());
        imageUrl = journal.getImageUrl();
        //1 hour ago, how do we create a time stamp for whenever something happened
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded()
                .getSeconds()*1000);
        viewHolder.dateAdded.setText(timeAgo);

        /*

        Use Picasso library to download and show image
         */

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.image3)
                .fit()
                .into(viewHolder.image);

    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView
                title,
                thoughts,
                dateAdded,
                name;


        public ImageView image;
        public ImageButton shareButton;
        String userId;
        String username;


        public ViewHolder (@NonNull View itemView, Context ctx) {                           //The context ctx makes it clickable so we can go to the next view or next activity
            super(itemView);
            context = ctx;
            title = itemView.findViewById(R.id.journal_title_list);
            thoughts = itemView.findViewById(R.id.journal_thought_list);
            dateAdded = itemView.findViewById(R.id.journal_timestamp_list);
            image = itemView.findViewById(R.id.journal_image_list);
        //    name = itemView.findViewById(R.id.username_account);
            name = itemView.findViewById(R.id.journal_row_username);
            shareButton = itemView.findViewById(R.id.journal_row_share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //context.startActivity();
                }
            });

        }
    }
}