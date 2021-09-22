package com.iorbit_tech.healthcare.caretakerapp.ithingshealthcare;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.iorbit_tech.healthcare.caretakerapp.utils.CommonDataArea;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class DisplayAlertActivity extends AppCompatActivity
        implements AdapterView.OnItemLongClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Database_Helper database_helper = null;
    private ListView listView;
    private int selectPosition = -1;
    private Dao<AlertDetail, Integer> alertDao;
    private List<AlertDetail> alertList, alertList1;
    AlertArrayAdapter adapter;
    Button searchbtn;
    SearchView searchView;
    EditText searchtxt;
    String textsearch;
    String searchfield;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_alertdetails);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //searchView = (SearchView) findViewById(R.id.searchView);
        listView = (ListView)findViewById(R.id.listview);

        //searchbtn = (Button)findViewById(R.id.btn_search);
        //searchbtn.setOnClickListener(this);

        try {

            alertDao =getHelper().getInformationDao();
            alertList = alertDao.queryForAll();
            final LayoutInflater layoutInflater = (LayoutInflater)this.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = layoutInflater.inflate(R.layout.list_alertdetail,listView,false);
            //listView.setAdapter(null);
           listView.setAdapter(new AlertArrayAdapter(this,R.layout.
                    list_alertdetail,alertList,alertDao));

            /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    if(alertList.contains(query)){
                        adapter.getFilter().filter(query);
                    }else{
                        Toast.makeText(DisplayAlertActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //    adapter.getFilter().filter(newText);
                    return false;
                }
            });*/


            //listView.addHeaderView(view);
            listView.setOnItemLongClickListener(this);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        //searchview

        searchtxt = (EditText)findViewById(R.id.inputSearch);




        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);





    }

   /* @Override
    protected void onResume() {

        super.onResume();
        try {
            alertDao = null;
            alertDao =getHelper().getInformationDao();
            alertList = alertDao.queryForAll();
            final LayoutInflater layoutInflater = (LayoutInflater)this.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = layoutInflater.inflate(R.layout.list_alertdetail,listView,false);
            listView.setAdapter(new AlertArrayAdapter(this,R.layout.
                    list_alertdetail,alertList,alertDao));
            //listView.addHeaderView(view);
            listView.setOnItemLongClickListener(this);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }


    }*/
   public List<AlertDetail> RetrieveByAll(String all)throws SQLException {

       alertDao =getHelper().getInformationDao();
       QueryBuilder<AlertDetail,Integer> queryBuilder = alertDao.queryBuilder();
       List list;
       list = alertDao.queryForAll();

       return list;
   }

    public List<AlertDetail> RetrieveBySubscriber(String subname)throws SQLException {
        System.out.println("Sub"+subname);
        alertDao =getHelper().getInformationDao();
        QueryBuilder<AlertDetail,Integer> queryBuilder = alertDao.queryBuilder();
        List list;
        queryBuilder.where().like("alert_subscriber", "%"+subname+"%");
        list = queryBuilder.query();

        return list;
    }

    public List<AlertDetail> RetrieveByTime(String subname)throws SQLException {
        System.out.println("Sub"+subname);
        alertDao =getHelper().getInformationDao();
        QueryBuilder<AlertDetail,Integer> queryBuilder = alertDao.queryBuilder();
        List list;
        //columnName+" LIKE '%"+subString+"%'"
        //.where().like(<column name>, "bana")
        queryBuilder.where().like("alert_time","%"+subname+"%");
        list = queryBuilder.query();
        System.out.println("List val="+list);
                return list;
    }

    public List<AlertDetail> RetrieveByType(String subname)throws SQLException {
        alertDao =getHelper().getInformationDao();
        QueryBuilder<AlertDetail,Integer> queryBuilder = alertDao.queryBuilder();
        List list;
        queryBuilder.where().like("alert_name", "%"+subname+"%");
        list = queryBuilder.query();

        return list;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View arg1, int position,long id) {
        String item = parent.getItemAtPosition(position).toString();
        if(item.trim().equals("All")) {

            searchfield = "All";
            searchtxt.setOnClickListener(this);

        }

        else if(item.trim().equals("Subscriber")) {

            searchfield = "Subscriber";
            searchtxt.setOnClickListener(this);

        }


        else if(item.trim().equals("Alert Time")) {

            searchfield = "Alert Time";
            searchtxt.setOnClickListener(this);



        }

       else if(item.trim().equals("Alert Type")) {

            searchfield = "Alert Type";
            searchtxt.setOnClickListener(this);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
// TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        try {
            textsearch = searchtxt.getText().toString();

            System.out.println("Sub Val="+textsearch);
            if(searchfield.equals("All"))
            {
                alertList1 = RetrieveByAll(textsearch);
            }
            else if(searchfield.equals("Subscriber"))
            {
                alertList1 = RetrieveBySubscriber(textsearch);
            }

            else if(searchfield.equals("Alert Time"))
            {
                alertList1 = RetrieveByTime(textsearch);
            }

            else if(searchfield.equals("Alert Type"))
            {
                alertList1 = RetrieveByType(textsearch);
            }



            alertDao =getHelper().getInformationDao();

            //alertList1 = RetrieveBySubscriber(textsearch);
            final LayoutInflater layoutInflater = (LayoutInflater)this.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = layoutInflater.inflate(R.layout.list_alertdetail,listView,false);
            listView.setAdapter(new AlertArrayAdapter(this,R.layout.
                    list_alertdetail,alertList1,alertDao));
            //listView.addHeaderView(view);
            listView.setOnItemLongClickListener(this);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return true;
    }

    private Database_Helper getHelper() {
        if (database_helper == null) {
            database_helper = OpenHelperManager.getHelper(this, Database_Helper.class);
        }
        return database_helper;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(position > 0)
        {
            selectPosition = position - 1;
            showDialog();
        }
        return false;
    }

    private void showDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to delete?");
        alertDialogBuilder.setTitle("Delete");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    alertDao.delete(alertList.get(selectPosition));
                    alertList.remove(selectPosition);
                    listView.invalidateViews();
                    selectPosition = -1;
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database_helper != null) {
            OpenHelperManager.releaseHelper();
            database_helper = null;
        }
    }
}