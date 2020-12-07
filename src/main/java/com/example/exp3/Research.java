package com.example.exp3;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.SharedPreferences;

public class Research extends AppCompatActivity implements OnClickListener{
    Button ButtonDelete,ButtonSave,ButtonCancel;
    EditText EditTextContent,EditTextTitle,EditTextREAuthor;
    String tranTitle;
    String Author;
    MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Note.db",null,1);

    private void InitNote() {
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Note.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();     //同上，获得可写文件
        Cursor cursor  = db.query("Note",new String[]{"id","title","content"},"title=?",new String[]{tranTitle+""},null,null,null);

        if(cursor.moveToNext()) {       //逐行查找，得到匹配信息
            do {
                String Title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                EditTextContent.setText(content);
                EditTextTitle.setText(Title);
            } while (cursor.moveToNext());
        }

        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);     //向sharedpreferences文件读取作者信息
        String name = pref.getString("author","");
        Log.d("MainActivity","name is " + name);
        EditTextREAuthor.setText(name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_research);
        EditTextContent = (EditText)findViewById(R.id.EditTextREEditContent);
        EditTextTitle = (EditText)findViewById(R.id.EditTextREEditTitle) ;
        ButtonCancel = (Button)findViewById(R.id.ButtonRECancel);
        ButtonSave = (Button)findViewById(R.id.ButtonRESave);
        ButtonDelete = (Button)findViewById(R.id.ButtonREDelete);
        EditTextREAuthor = findViewById(R.id.EditTextREAuthor);


        ButtonCancel.setOnClickListener(this);
        ButtonSave.setOnClickListener(this);
        ButtonDelete.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        tranTitle = extras.getString("tranTitletoRE");      //接受主界面传来的title值

        InitNote();


    }
    @Override
    public void onClick(View v){
        switch (v.getId()){

            case R.id.ButtonREDelete:       //删除该title的日志
                Log.d("title is ",tranTitle);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("Note","title=?",new String[]{tranTitle+""});     //进行字符串匹配
                Research.this.setResult(RESULT_OK,getIntent());
                Research.this.finish();
                break;
            case R.id.ButtonRESave:         //将文界面内容保存
                SQLiteDatabase db1 = dbHelper.getWritableDatabase();        //获取可写文件
                Date date = new Date();
                ContentValues values = new ContentValues();         //获取信息
                String Title = String.valueOf(EditTextTitle.getText());
                String Content = String.valueOf(EditTextContent.getText());
                if(Title.length()==0){
                    Toast.makeText(this, "请输入一个标题", Toast.LENGTH_LONG).show();
                }else {
                    values.put("title", Title);         //填装信息
                    values.put("content", Content);
                    db1.update("Note", values, "title=?", new String[]{tranTitle + ""});        //字符串匹配
                    Research.this.setResult(RESULT_OK, getIntent());        //返回主界面
                    Research.this.finish();
                }

                Author = String.valueOf(EditTextREAuthor.getText());
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putString("author",Author);
                editor.apply();     //写入作者信息

                break;


            case R.id.ButtonRECancel:
                Research.this.setResult(RESULT_OK,getIntent());
                Research.this.finish();
                break;

        }

    }
}
