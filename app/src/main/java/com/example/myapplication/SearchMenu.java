package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.AladdinOpenAPI;

import java.util.ArrayList;
import java.util.List;

public class SearchMenu extends AppCompatActivity {

    String query="";
    String queryTarget="";
    Integer page;

    AladdinOpenAPIHandler api = new AladdinOpenAPIHandler();
    MyAdapter myAdapter=new MyAdapter();
    ListView listView;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu);

        //dropdown list
        Spinner spinner = findViewById(R.id.search_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.search_type_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //search button
        ImageButton searchButton = findViewById(R.id.search_button);


        listView = findViewById(R.id.search_list);

        imm = (InputMethodManager) getSystemService((Context.INPUT_METHOD_SERVICE));


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.search_input);
                Spinner spinner = findViewById(R.id.search_type);
                queryTarget=spinner.getSelectedItem().toString();
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                switch (queryTarget){
                    case "제목+저자":
                        queryTarget="Keyword";
                        break;
                    case "제목":
                        queryTarget="Title";
                        break;
                    case "저자":
                        queryTarget="Author";
                        break;
                    case "출판사":
                        queryTarget="Publisher";
                        break;
                }
                query= editText.getText().toString();
                page = 1;

                SearchTask search = new SearchTask();
                search.execute();
            }
        });

    }

    public class SearchTask extends AsyncTask<Void, Void, List<Item>>{

        String url="";

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected List<Item> doInBackground(Void... v){

            try{
                url = AladdinOpenAPI.GetUrl(queryTarget,query,page.toString());
                api.parseXml(url);
            }catch (Exception e){
                e.printStackTrace();
            }

            return api.Items;
        }

        @Override
        protected void onProgressUpdate(Void... v){}

        @Override
        protected void onPostExecute(List<Item> items){
            myAdapter.items.clear();
            myAdapter.notifyDataSetChanged();
            for (Item item : items) {
                myAdapter.addItem(item.title, item.description, item.author, item.cover, item.publisher);
            }
            listView.setAdapter(null);
            listView.setAdapter(myAdapter);
        }

    }

    public class MyAdapter extends BaseAdapter {

        private ArrayList<Item> items = new ArrayList<>();

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Item getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            Context context = parent.getContext();

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.search_item, parent, false);
        }

            TextView title = view.findViewById(R.id.book_title);
            TextView description = view.findViewById(R.id.book_description);
            TextView author = view.findViewById(R.id.book_author);
            ImageView cover = view.findViewById(R.id.book_cover);
            TextView publisher = view.findViewById(R.id.book_publisher);

            Item item = getItem(position);

            title.setText(item.title);
            description.setText(item.description);
            author.setText(item.author);
            Glide.with(context).load(item.cover).into(cover);
            publisher.setText(item.publisher);

            /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */

            return view;
        }

        public void addItem(String title, String description, String author, String cover, String publisher) {
            Item item = new Item();

            item.title= title;
            item.description=description;
            item.author=author;
            item.cover= cover;
            item.publisher=publisher;

            items.add(item);
        }
    }
}