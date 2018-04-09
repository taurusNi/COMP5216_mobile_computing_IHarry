package com.taurus.iharry;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.taurus.iharry.database.*;
import java.util.ArrayList;
import java.util.List;


import static android.app.AlertDialog.*;
public class MainActivity extends AppCompatActivity {
    private ArrayList<Float> conditions = null;
    private ProgressDialog proDia;
    private Handler transferHandler;
    private ArrayList<Integer> sts ;
    private int status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        List<Page_Number> bk = Page_Number.listAll(Page_Number.class);
        this.conditions = readConditionFromDatabase();

//        for(int i=0;i<sts.size();i++){
//            Log.d("status",String.valueOf(sts.get(i)));
//        }
//        Log.d(">>>>>>>>>>>>>>>>>>",">>>>>>>>>>>>>>>>>>>");
        ImageView harry1 = (ImageView) findViewById(R.id.Harry1);
        ImageView harry2 = (ImageView) findViewById(R.id.Harry2);
        ImageView harry3 = (ImageView) findViewById(R.id.Harry3);
        ImageView harry4 = (ImageView) findViewById(R.id.Harry4);
        ImageView harry5 = (ImageView) findViewById(R.id.Harry5);
        ImageView harry6 = (ImageView) findViewById(R.id.Harry6);
        ImageView harry7 = (ImageView) findViewById(R.id.Harry7);
        ImageView harry8 = (ImageView) findViewById(R.id.Harry8);
        harry1.setAlpha(conditions.get(0));
        harry2.setAlpha(conditions.get(1));
        harry3.setAlpha(conditions.get(2));
        harry4.setAlpha(conditions.get(3));
        harry5.setAlpha(conditions.get(4));
        harry6.setAlpha(conditions.get(5));
        harry7.setAlpha(conditions.get(6));
        harry8.setAlpha(conditions.get(7));
        onBookClickListener(harry1,0);
        onBookClickListener(harry2,1);
        onBookClickListener(harry3,2);
        onBookClickListener(harry4,3);
        onBookClickListener(harry5,4);
        onBookClickListener(harry6,5);
        onBookClickListener(harry7,6);
        onBookClickListener(harry8,7);
        onBookLongClickListener(harry1,0);
        onBookLongClickListener(harry2,1);
        onBookLongClickListener(harry3,2);
        onBookLongClickListener(harry4,3);
        onBookLongClickListener(harry5,4);
        onBookLongClickListener(harry6,5);
        onBookLongClickListener(harry7,6);
        onBookLongClickListener(harry8,7);
        transferHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                sts= readBookStatusFromDatabase();
                Intent intent2 = new Intent(MainActivity.this,ReadActivity2.class);
                Intent itent = new Intent(MainActivity.this,ReadActivity.class);
                switch(msg.what){
                    case 0:
                        status = sts.get(0);
                        if(status==0){
                            itent.putExtra("bookNum","1");//第一本书
                            MainActivity.this.startActivity(itent);
                        }else{
                            intent2.putExtra("bookNum","1");//第一本书
                            MainActivity.this.startActivity(intent2);
                        }
                        break;
                    case 1:
                        status = sts.get(1);
                        if(status==0){
                            itent.putExtra("bookNum","2");
                            MainActivity.this.startActivity(itent);
                        }else{
                            intent2.putExtra("bookNum","2");
                            MainActivity.this.startActivity(intent2);
                        }
                        break;
                    case 2:
                        status = sts.get(2);
                        if(status==0){
                            itent.putExtra("bookNum","3");
                            MainActivity.this.startActivity(itent);

                        }else{
                            intent2.putExtra("bookNum","3");
                            MainActivity.this.startActivity(intent2);

                        }
                        break;
                    case 3:
                        status = sts.get(3);
                        if(status==0){
                            itent.putExtra("bookNum","4");
                            MainActivity.this.startActivity(itent);
                        }else{
                            intent2.putExtra("bookNum","4");
                            MainActivity.this.startActivity(intent2);

                        }
                        break;
                    case 4:
                        status = sts.get(4);
                        if(status==0){
                            itent.putExtra("bookNum","5");
                            MainActivity.this.startActivity(itent);
                        }else{
                            intent2.putExtra("bookNum","5");
                            MainActivity.this.startActivity(intent2);
                        }
                        break;
                    case 5:
                        status = sts.get(5);
                        if(status==0){
                            itent.putExtra("bookNum","6");
                            MainActivity.this.startActivity(itent);
                        }else{
                            intent2.putExtra("bookNum","6");
                            MainActivity.this.startActivity(intent2);
                        }
                        break;
                    case 6:
                        status = sts.get(6);
                        if(status==0){
                            itent.putExtra("bookNum","7");
                            MainActivity.this.startActivity(itent);
                        }else{
                            intent2.putExtra("bookNum","7");
                            MainActivity.this.startActivity(intent2);
                        }
                        break;
                }


//                        startThread(position);
                    }

//                startThread(position);
//
//                if(msg.what==1){
//                if(proDia.isShowing()){
//                    proDia.dismiss();
//                }}
////                            switch(msg.what) {
//                                case 0 :

//                Intent itent = new Intent(MainActivity.this,ReadActivity.class);
//                itent.putExtra("bookNum","1");
//                MainActivity.this.startActivity(itent);
//                Log.d("test",">>>>>>>>>>>>>>>>>>>>>");
//                                    Intent itent = new Intent(MainActivity.this,ReadActivity.class);
//
//                                        itent.putExtra("bookNum","1");
//                                     MainActivity.this.startActivity(itent);
//                                        if(proDia.isShowing()){
//                                            proDia.dismiss();
//                                        }
//                                    break;
////                                case 1:
////                                    proDia.dismiss();
////                                    break;
//                            }
//            }
        };
    }
    private void onBookLongClickListener(final ImageView bookImage,final int position){
        bookImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(bookImage.getAlpha()==1.0f){
                    AlertDialog.Builder buy_section = new AlertDialog.Builder(MainActivity.this);
                    buy_section.setTitle("Delete the book")
                            .setMessage("Do you want to delete the book?")
                            .setNegativeButton(R.string.No,new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).setPositiveButton(R.string.Yes, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            bookImage.setAlpha(0.5f);
                            MainActivity.this.conditions.set(position,0.5f);
                            MainActivity.this.saveConditionsToDatabase(MainActivity.this.conditions);
                            Toast.makeText(MainActivity.this, "Delete succeed", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
                }else{
                    Toast.makeText(MainActivity.this, "The book is not existed", Toast.LENGTH_SHORT).show();
                }


                return true;
            }
        });
    }
    private void onBookClickListener(final ImageView bookImage,final int position){
        bookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bookImage.getAlpha()==0.5f){
                    if(position==7){
                        Toast.makeText(MainActivity.this,"The book is not available right now",Toast.LENGTH_SHORT).show();
                    }else{
                    AlertDialog.Builder buy_section = new AlertDialog.Builder(MainActivity.this);
                    buy_section.setTitle("Buy the book")
                            .setMessage("Do you want to buy the book?")
                            .setNegativeButton(R.string.No,new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).setPositiveButton(R.string.Yes, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            bookImage.setAlpha(1.0f);
                            MainActivity.this.conditions.set(position,1.0f);
                            MainActivity.this.saveConditionsToDatabase(MainActivity.this.conditions);
                        }
                    }).show();}
                }else {
                    proDia = new ProgressDialog(MainActivity.this);
                    proDia.setTitle("loading book...");
                    proDia.setMessage("We are trying best...");
                    proDia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    proDia.show();
                    stratLoadingThread(position);
                }
            }
        });
    }

    private ArrayList<Float> readConditionFromDatabase() {
        //read book condition from database
        List<Book_Condition> bookConditionList = Book_Condition.listAll(Book_Condition.class);
        ArrayList<Float> conditions = new ArrayList<Float>();
        if (bookConditionList != null && bookConditionList.size() > 0) {
            for (Book_Condition condition : bookConditionList) {
                        conditions.add(condition.getCondition());
            } }
        else{
            //set the original condition of books
            for(int i=0;i<8;i++){
            conditions.add(0.5f);}
        }
        return conditions;
    }

    private void saveConditionsToDatabase(ArrayList<Float> conditions)
    {   Book_Condition.deleteAll(Book_Condition.class);
        for (Float condition:conditions){
            Book_Condition item = new Book_Condition(condition);
            item.save();
    }
    }

    private void stratLoadingThread(final int num){
        new Thread(){
            @Override
            public void run() {
                    try {
                        Thread.sleep(300);
                        Message msg = new Message();
                        msg.what = num;
                        transferHandler.sendEmptyMessage(num);
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        proDia.dismiss();
                    }
//                Message msg = new Message();
//                msg.what=1;
//                transferHandler.sendMessage(msg);
            }
        }.start();
    }

    private ArrayList<Integer> readBookStatusFromDatabase(){
        List<bookStatus> statuses = bookStatus.listAll(bookStatus.class);
        ArrayList<Integer> statuse = new ArrayList<Integer>();
        if (statuses != null && statuses.size() > 0) {
            for (bookStatus index : statuses) {
                statuse.add(index.getStatus());
            }
        } else {
            for (int i = 0; i < 8; i++) {
                statuse.add(0);
            }
        }
        return statuse;
    }
    private void saveBookStatusToDatabase(ArrayList<Integer> sta){
        bookStatus.deleteAll(bookStatus.class);
        for(int e:sta){
            bookStatus item = new bookStatus(e);
            item.save();
        }
    }
}
