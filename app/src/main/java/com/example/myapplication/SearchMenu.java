package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.AladdinOpenAPI;

import java.util.ArrayList;

public class SearchMenu extends AppCompatActivity {

    String query = "";
    String queryTarget = "";
    Integer page;

    AladdinOpenAPIHandler api = new AladdinOpenAPIHandler();
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu);

        //dropdown list
        Spinner spinner = findViewById(R.id.search_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.search_type_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //search button
        ImageButton searchButton = findViewById(R.id.search_button);


        recyclerView = findViewById(R.id.search_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        imm = (InputMethodManager) getSystemService((Context.INPUT_METHOD_SERVICE));


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.search_input);
                Spinner spinner = findViewById(R.id.search_type);
                queryTarget = spinner.getSelectedItem().toString();
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                switch (queryTarget) {
                    case "제목+저자":
                        queryTarget = "Keyword";
                        break;
                    case "제목":
                        queryTarget = "Title";
                        break;
                    case "저자":
                        queryTarget = "Author";
                        break;
                    case "출판사":
                        queryTarget = "Publisher";
                        break;
                }
                query = editText.getText().toString();
                page = 1;

                SearchTask search = new SearchTask();
                search.execute();
            }
        });

    }

    public class SearchTask extends AsyncTask<Void, Void, ArrayList<Item>> {

        String url = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Item> doInBackground(Void... v) {

            try {
                url = AladdinOpenAPI.GetUrl(queryTarget, query, page.toString());
                api.Items.clear();
                api.parseXml(url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return api.Items;
        }

        @Override
        protected void onProgressUpdate(Void... v) {
        }

        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            adapter = new MyAdapter(items);
            recyclerView.setAdapter(adapter);
        }

    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<Item> items;

        public MyAdapter(ArrayList<Item> items) {
            this.items = items;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public TextView description;
            public TextView author;
            public ImageView cover;
            public TextView publisher;

            public MyViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.book_title);
                description = view.findViewById(R.id.book_description);
                author = view.findViewById(R.id.book_author);
                cover = view.findViewById(R.id.book_cover);
                publisher = view.findViewById(R.id.book_publisher);
            }
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
            View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_item,viewGroup,false);
            MyViewHolder myViewHolder = new MyViewHolder((holderView));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Item item = items.get(position);
            holder.title.setText(item.title);
            holder.description.setText(item.description);
            holder.author.setText(item.author);
            Glide.with(holder.itemView.getContext()).load(item.cover).into(holder.cover);
            holder.publisher.setText(item.publisher);
        }

        @Override
        public int getItemCount(){
            return items.size();
        }
    }
}