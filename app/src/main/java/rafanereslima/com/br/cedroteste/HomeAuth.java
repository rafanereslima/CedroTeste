package rafanereslima.com.br.cedroteste;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import rafanereslima.com.br.cedroteste.mDataBase.DBAdapter;
import rafanereslima.com.br.cedroteste.mDataObject.Spacecraft;
import rafanereslima.com.br.cedroteste.mListView.CustomAdapter;

public class HomeAuth extends AppCompatActivity {

    ListView lv;
    EditText nameEditText;
    EditText passEditText;
    Button saveBtn,retrieveBtn;
    ArrayList<Spacecraft> spacecrafts=new ArrayList<>();
    CustomAdapter adapter;
    final Boolean forUpdate=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_sites);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        lv= (ListView) findViewById(R.id.lv);
        adapter=new CustomAdapter(this,spacecrafts);

        this.getSpacecrafts();
        // lv.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog(false);
            }
        });
    }

    private void displayDialog(Boolean forUpdate)
    {
        Dialog d=new Dialog(this);
        d.setTitle("SQLITE DATA");
        d.setContentView(R.layout.dialog_layout);

        nameEditText=  d.findViewById(R.id.siteEditTxt);
        passEditText= d.findViewById(R.id.passEditTxt);
        saveBtn=  d.findViewById(R.id.saveBtn);
        retrieveBtn= d.findViewById(R.id.retrieveBtn);

        if(!forUpdate)
        {
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save(nameEditText.getText().toString(),passEditText.getText().toString());
                }
            });
            retrieveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSpacecrafts();
                }
            });
        }else {

            nameEditText.setText(adapter.getSelectedItemName());

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    update(nameEditText.getText().toString(),passEditText.getText().toString());
                }
            });
            retrieveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSpacecrafts();
                }
            });
        }

        d.show();

    }

    //SALVANDO
    private void save(String name, String password)
    {
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        boolean saved=db.add(name,password);

        if(saved)
        {
            nameEditText.setText("");
            getSpacecrafts();
        }else {
            Toast.makeText(this,"Impossível salvar",Toast.LENGTH_SHORT).show();
        }
    }


    private void getSpacecrafts()
    {
        spacecrafts.clear();
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        Cursor c=db.retrieve();
        Spacecraft spacecraft=null;

        while (c.moveToNext())
        {
            int id=c.getInt(0);
            String site=c.getString(1);
            String password=c.getString(2);

            spacecraft=new Spacecraft();
            spacecraft.setId(id);
            spacecraft.setSite(site);
            spacecraft.setPassword(password);

            spacecrafts.add(spacecraft);
        }

        db.closeDB();
        lv.setAdapter(adapter);
    }

    //UPDATE
    private void update(String newSite, String newPassword)
    {
        //GET ID
        int id=adapter.getSelectedItemID();

        //UPDATE DB
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        boolean updated=db.update(newSite,newPassword,id);
        db.closeDB();

        if(updated)
        {
            nameEditText.setText(newSite);
            getSpacecrafts();
        }else {
            Toast.makeText(this,"Impossível atualizar",Toast.LENGTH_SHORT).show();
        }

    }

    private void delete()
    {
        //GET ID
        int id=adapter.getSelectedItemID();

        //DELETE DB
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        boolean deleted=db.delete(id);
        db.closeDB();

        if(deleted)
        {
            getSpacecrafts();
        }else {
            Toast.makeText(this,"Impossível deletar",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        CharSequence title=item.getTitle();
        if(title=="NEW")
        {
            displayDialog(!forUpdate);

        }else  if(title=="EDIT")
        {
            displayDialog(forUpdate);

        }else  if(title=="DELETE")
        {
            delete();
        }

        return super.onContextItemSelected(item);
    }
}